package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Chant_Shillelagh extends Chant {
	public String ID() {
		return "Chant_Shillelagh";
	}

	public String name() {
		return "Shillelagh";
	}

	public String displayText() {
		return "(Shillelagh)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_BONUS);
		if (affected instanceof Item)
			affectableStats.setAbility(affectableStats.ability() + 4);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (canBeUninvoked()) {
			if (((affected != null) && (affected instanceof Item))
					&& ((((Item) affected).owner() != null) && (((Item) affected)
							.owner() instanceof MOB)))
				((MOB) ((Item) affected).owner()).tell("The enchantment on "
						+ ((Item) affected).name() + " fades.");
		}
		super.unInvoke();
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if ((mob.fetchWieldedItem() instanceof Weapon)
					&& ((((Weapon) mob.fetchWieldedItem()).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
					&& ((((Weapon) mob.fetchWieldedItem()).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_VEGETATION)
					&& (mob.fetchWieldedItem().fetchEffect(ID()) == null))
				return super.castingQuality(mob, target,
						Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null) {
			if ((mob.isMonster())
					&& (mob.fetchWieldedItem() instanceof Weapon)
					&& ((((Weapon) mob.fetchWieldedItem()).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
					&& ((((Weapon) mob.fetchWieldedItem()).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_VEGETATION))
				target = mob.fetchWieldedItem();
			else
				return false;
		}

		if (!(target instanceof Weapon)) {
			mob.tell("You can only enchant weapons.");
			return false;
		}
		if (((((Weapon) target).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
				&& ((((Weapon) target).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_VEGETATION)) {
			mob.tell("You cannot enchant this foreign material.");
			return false;
		}
		if (((Weapon) target).fetchEffect(this.ID()) != null) {
			mob.tell(target.name(mob) + " is already enchanted.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> appear(s) enchanted!"
							: "^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
				mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
						"<T-NAME> glow(s)!");
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens.");
		// return whether it worked
		return success;
	}
}