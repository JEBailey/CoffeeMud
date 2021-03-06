package com.planet_ink.coffee_mud.Abilities.Paladin;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.Common.EnhancedCraftingSkill;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class Paladin_CraftHolyAvenger extends EnhancedCraftingSkill {
	public String ID() {
		return "Paladin_CraftHolyAvenger";
	}

	public String name() {
		return "Craft Holy Avenger";
	}

	private static final String[] triggerStrings = { "CRAFTHOLY",
			"CRAFTHOLYAVENGER", "CRAFTAVENGER" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			MOB mob = (MOB) affected;
			if ((buildingI == null) || (getRequiredFire(mob, 0) == null)) {
				messedUp = true;
				unInvoke();
			}
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((buildingI != null) && (!aborted)) {
					if (messedUp)
						commonEmote(mob,
								"<S-NAME> mess(es) up crafting the Holy Avenger.");
					else
						mob.location().addItem(buildingI,
								ItemPossessor.Expire.Player_Drop);
				}
				buildingI = null;
			}
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		int completion = 16;
		Item fire = getRequiredFire(mob, 0);
		if (fire == null)
			return false;
		PairVector<Integer, Integer> enhancedTypes = enhancedTypes(mob,
				commands);
		buildingI = null;
		messedUp = false;
		int woodRequired = 50;
		int[] pm = { RawMaterial.MATERIAL_METAL, RawMaterial.MATERIAL_MITHRIL };
		int[][] data = fetchFoundResourceData(mob, woodRequired, "metal", pm,
				0, null, null, false, auto ? RawMaterial.RESOURCE_MITHRIL : 0,
				enhancedTypes);
		if (data == null)
			return false;
		woodRequired = data[0][FOUND_AMT];

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		if (!auto)
			CMLib.materials().destroyResourcesValue(mob.location(),
					woodRequired, data[0][FOUND_CODE], 0, null);
		buildingI = CMClass.getWeapon("GenWeapon");
		completion = 50 - CMLib.ableMapper().qualifyingClassLevel(mob, this);
		String itemName = "the Holy Avenger";
		buildingI.setName(itemName);
		String startStr = "<S-NAME> start(s) crafting " + buildingI.name()
				+ ".";
		displayText = "You are crafting " + buildingI.name();
		verb = "crafting " + buildingI.name();
		buildingI.setDisplayText(itemName + " lies here");
		buildingI.setDescription(itemName + ". ");
		buildingI.basePhyStats().setWeight(woodRequired);
		buildingI.setBaseValue(0);
		buildingI.setMaterial(data[0][FOUND_CODE]);
		buildingI.basePhyStats().setLevel(mob.phyStats().level());
		buildingI.basePhyStats().setAbility(5);
		Weapon w = (Weapon) buildingI;
		w.setWeaponClassification(Weapon.CLASS_SWORD);
		w.setWeaponType(Weapon.TYPE_SLASHING);
		w.setRanges(w.minRange(), 1);
		buildingI.setRawLogicalAnd(true);
		Ability A = CMClass.getAbility("Prop_HaveZapper");
		A.setMiscText("-CLASS +Paladin -ALIGNMENT +Good");
		buildingI.addNonUninvokableEffect(A);
		A = CMClass.getAbility("Prop_Doppleganger");
		A.setMiscText("120%");
		buildingI.addNonUninvokableEffect(A);

		buildingI.recoverPhyStats();
		buildingI.text();
		buildingI.recoverPhyStats();

		messedUp = !proficiencyCheck(mob, 0, auto);
		if (completion < 6)
			completion = 6;
		CMMsg msg = CMClass
				.getMsg(mob, null, CMMsg.MSG_NOISYMOVEMENT, startStr);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			beneficialAffect(mob, mob, asLevel, completion);
			enhanceItem(mob, buildingI, enhancedTypes);
		}
		return true;
	}
}
