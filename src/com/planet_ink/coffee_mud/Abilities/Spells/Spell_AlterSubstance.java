package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

/* 
 Copyright 2000-2014 Bo Zimmerman

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
@SuppressWarnings("rawtypes")
public class Spell_AlterSubstance extends Spell {
	public String ID() {
		return "Spell_AlterSubstance";
	}

	public String name() {
		return "Alter Substance";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public String newName = "";
	public int oldMaterial = 0;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (newName.length() > 0)
			affectableStats.setName(newName);
	}

	public void unInvoke() {
		if ((affected != null) && (affected instanceof Item)) {
			Item I = (Item) affected;
			I.setMaterial(oldMaterial);
			if (I.owner() instanceof Room)
				((Room) I.owner()).showHappens(CMMsg.MSG_OK_VISUAL, I.name()
						+ " reverts to its natural form.");
			else if (I.owner() instanceof MOB)
				((MOB) I.owner()).tell(I.name(((MOB) I.owner()))
						+ " reverts to its natural form.");
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		String material = "";
		if (commands.size() > 0) {
			material = (String) commands.lastElement();
			commands.removeElement(material);
		}
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;
		RawMaterial.Material m = RawMaterial.Material.findIgnoreCase(material);
		if (m == null)
			m = RawMaterial.Material.startsWithIgnoreCase(material);
		int newMaterial = (m == null) ? -1 : m.mask();
		if ((newMaterial >= 0) && (m != null)) {
			List<Integer> rscs = RawMaterial.CODES
					.COMPOSE_RESOURCES(newMaterial);
			if (rscs.size() > 0) {
				newMaterial = rscs.get(0).intValue();
				material = RawMaterial.CODES.NAME(newMaterial);
			} else {
				material = m.desc();
				newMaterial = m.mask();
			}
		} else {
			newMaterial = RawMaterial.CODES.FIND_IgnoreCase(material.trim());
			if (newMaterial < 0)
				newMaterial = RawMaterial.CODES
						.FIND_StartsWith(material.trim());
			if (newMaterial >= 0)
				material = RawMaterial.CODES.NAME(newMaterial);
		}
		if (newMaterial < 0) {
			mob.tell("'" + material + "' is not a known substance!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				material = CMStrings.capitalizeAndLower(material);
				mob.location().show(mob, target, CMMsg.MSG_OK_ACTION,
						"<T-NAME> change(s) into " + material + "!");
				oldMaterial = target.material();
				target.setMaterial(newMaterial);
				String oldResourceName = RawMaterial.CODES.NAME(oldMaterial);
				String oldMaterialName = RawMaterial.Material.findByMask(
						oldMaterial & RawMaterial.MATERIAL_MASK).desc();
				String oldName = target.name().toUpperCase();
				oldName = CMStrings.replaceAll(oldName, oldResourceName,
						material);
				oldName = CMStrings.replaceAll(oldName, oldMaterialName,
						material);
				if (oldName.indexOf(material) < 0) {
					int x = oldName.lastIndexOf(' ');
					if (x < 0)
						oldName = material + " " + oldName;
					else
						oldName = oldName.substring(0, x) + " " + material
								+ oldName.substring(x);
				}
				newName = CMStrings.capitalizeAndLower(oldName);
				beneficialAffect(mob, target, asLevel, 100);
			}

		} else
			beneficialWordsFizzle(
					mob,
					target,
					"<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting but nothing happens.");

		// return whether it worked
		return success;
	}
}
