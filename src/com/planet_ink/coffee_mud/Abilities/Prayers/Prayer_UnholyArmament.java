package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Map;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class Prayer_UnholyArmament extends Prayer {
	public String ID() {
		return "Prayer_UnholyArmament";
	}

	public String name() {
		return "Unholy Armament";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	protected int overridemana() {
		return Ability.COST_ALL;
	}

	public static final long[] checkOrder = { Wearable.WORN_WIELD,
			Wearable.WORN_TORSO, Wearable.WORN_LEGS, Wearable.WORN_WAIST,
			Wearable.WORN_HEAD, Wearable.WORN_ARMS, Wearable.WORN_FEET,
			Wearable.WORN_HANDS, Wearable.WORN_LEFT_WRIST,
			Wearable.WORN_RIGHT_WRIST, Wearable.WORN_ABOUT_BODY,
			Wearable.WORN_HELD, };

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		if (mob.isInCombat()) {
			mob.tell("Not during combat!");
			return false;
		}

		long pos = -1;
		int i = 0;
		Item I = null;
		while (i < checkOrder.length) {
			if (mob.freeWearPositions(checkOrder[i], (short) 0, (short) 0) <= 0) {
				i++;
				continue;
			}
			pos = checkOrder[i];
			if (pos < 0) {
				if (mob.getWorshipCharID().length() > 0)
					mob.tell(mob.getWorshipCharID()
							+ " can see that you are already completely armed.");
				else
					mob.tell("The gods can see that you are already armed.");
				return false;
			}
			int numThatsOk = 1;
			if (pos == Wearable.WORN_WIELD) {
				I = CMClass.getWeapon("GenWeapon");
				I.setName("an unholy blade");
				I.setDisplayText("an wicked looking blade sits here.");
				((Weapon) I).setWeaponClassification(Weapon.CLASS_SWORD);
				((Weapon) I).setWeaponType(Weapon.TYPE_SLASHING);
				I.setDescription("Whatever made this sharp twisted thing couldn`t have been good..");
				I.basePhyStats().setLevel(mob.phyStats().level());
				I.basePhyStats().setWeight(20);
				I.setMaterial(RawMaterial.RESOURCE_MITHRIL);
				I.recoverPhyStats();
				Map<String, String> H = CMLib.itemBuilder()
						.timsItemAdjustments(
								I,
								mob.phyStats().level()
										+ (2 * super.getXLEVELLevel(mob)),
								I.material(), 1,
								((Weapon) I).weaponClassification(), 0,
								I.rawProperLocationBitmap());
				I.basePhyStats().setDamage(CMath.s_int(H.get("DAMAGE")));
				I.basePhyStats().setAttackAdjustment(
						CMath.s_int(H.get("ATTACK")));
				I.setBaseValue(0);
			} else if (pos == Wearable.WORN_HELD) {
				I = CMClass.getArmor("GenShield");
				I.setName("an unholy shield");
				I.setDisplayText("an unholy shield sits here.");
				I.setDescription("Whatever made this hideous shield couldn`t have been good.");
				I.basePhyStats().setLevel(mob.phyStats().level());
				I.basePhyStats().setWeight(20);
				I.setMaterial(RawMaterial.RESOURCE_MITHRIL);
				I.recoverPhyStats();
				Map<String, String> H = CMLib.itemBuilder()
						.timsItemAdjustments(
								I,
								mob.phyStats().level()
										+ (2 * super.getXLEVELLevel(mob)),
								I.material(), 1, 0, 0,
								I.rawProperLocationBitmap());
				I.basePhyStats().setArmor(CMath.s_int(H.get("ARMOR")));
				I.basePhyStats().setWeight(CMath.s_int(H.get("WEIGHT")));
				I.setBaseValue(0);
			} else {
				I = CMClass.getArmor("GenArmor");
				I.setRawProperLocationBitmap(pos);
				I.basePhyStats().setLevel(mob.phyStats().level());
				if (pos == Wearable.WORN_ABOUT_BODY)
					I.setMaterial(RawMaterial.RESOURCE_COTTON);
				else
					I.setMaterial(RawMaterial.RESOURCE_MITHRIL);
				I.recoverPhyStats();
				Map<String, String> H = CMLib.itemBuilder()
						.timsItemAdjustments(
								I,
								mob.phyStats().level()
										+ (2 * super.getXLEVELLevel(mob)),
								I.material(), 1, 0, 0,
								I.rawProperLocationBitmap());
				I.basePhyStats().setArmor(CMath.s_int(H.get("ARMOR")));
				I.basePhyStats().setWeight(CMath.s_int(H.get("WEIGHT")));
				I.setBaseValue(0);
				if (pos == Wearable.WORN_TORSO) {
					I.setName("an unholy breast plate");
					I.setDisplayText("a wicked looking breast plate sits here.");
					I.setDescription("Whatever made this black spiked armor couldn`t have been good.");
				}
				if (pos == Wearable.WORN_HEAD) {
					I.setName("an unholy helm");
					I.setDisplayText("a wicked looking helmet sits here.");
					I.setDescription("Whatever made this spiked helmet couldn`t have been good.");
				}
				if (pos == Wearable.WORN_ABOUT_BODY) {
					I.setName("an unholy cape");
					I.setDisplayText("a torn black cape sits here.");
					I.setDescription("Whatever made this cape couldn`t have been good.");
				}
				if (pos == Wearable.WORN_ARMS) {
					I.setName("some unholy arm cannons");
					I.setDisplayText("a pair of wicked looking arm cannons sit here.");
					I.setDescription("Whatever made this couldn`t have been good.");
				}
				if ((pos == Wearable.WORN_LEFT_WRIST)
						|| (pos == Wearable.WORN_RIGHT_WRIST)) {
					numThatsOk = 2;
					I.setName("an unholy vambrace");
					I.setDisplayText("a wicked looking spiked vambrace sit here.");
					I.setDescription("Whatever made this twisted black metal couldn`t have been good.");
				}
				if (pos == Wearable.WORN_HANDS) {
					I.setName("a pair of unholy gauntlets");
					I.setDisplayText("some wicked looking gauntlets sit here.");
					I.setDescription("Whatever made this twisted black metal couldn`t have been good.");
				}
				if (pos == Wearable.WORN_WAIST) {
					I.setName("an unholy girdle");
					I.setDisplayText("a wicked looking girdle sits here.");
					I.setDescription("Whatever made this twisted black metal couldn`t have been good.");
				}
				if (pos == Wearable.WORN_LEGS) {
					I.setName("a pair of unholy leg cannons");
					I.setDisplayText("a wicked looking pair of leg cannons sits here.");
					I.setDescription("Whatever made this twisted and spiked black metal couldn`t have been good.");
				}
				if (pos == Wearable.WORN_FEET) {
					I.setName("a pair of unholy boots");
					I.setDisplayText("a wicked looking pair of boots sits here.");
					I.setDescription("Whatever made this pair of twisted and spiked black metal boots couldn`t have been good.");
				}
			}
			Ability A = CMClass.getAbility("Prop_HaveZapper");
			if (A != null) {
				A.setMiscText("ACTUAL -GOOD -NEUTRAL -NAMES \"+" + mob.Name()
						+ "\"");
				I.addNonUninvokableEffect(A);
			}
			A = CMClass.getAbility("Prop_ScrapExplode");
			if (A != null)
				I.addNonUninvokableEffect(A);
			I.recoverPhyStats();
			int numFound = mob.findItems(null, "$" + I.name() + "$").size()
					+ mob.location().findItems(null, "$" + I.name() + "$")
							.size();
			if (numFound >= numThatsOk) {
				i++;
				I = null;
				continue;
			}
			break;
		}

		boolean success = proficiencyCheck(mob, 0, auto);

		if ((success) && (I != null)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? "" : "^S<S-NAME> "
							+ prayWord(mob) + " to be provided armament!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().addItem(I, ItemPossessor.Expire.Monster_EQ);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
						I.name() + " materializes out of the ground.");
			}
		} else
			return beneficialWordsFizzle(mob, null, "<S-NAME> " + prayWord(mob)
					+ " for armament, but flub(s) it.");

		// return whether it worked
		return success;
	}
}
