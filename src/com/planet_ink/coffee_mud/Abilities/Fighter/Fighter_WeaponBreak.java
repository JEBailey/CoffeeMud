package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Fighter_WeaponBreak extends FighterSkill {
	public String ID() {
		return "Fighter_WeaponBreak";
	}

	public String name() {
		return "Weapon Break";
	}

	private static final String[] triggerStrings = { "BREAK" };

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(1);
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public int castingQuality(MOB mob, Physical target) {
		if ((mob != null) && (target != null)) {
			MOB victim = mob.getVictim();
			if ((!mob.isInCombat()) || (victim == null))
				return Ability.QUALITY_INDIFFERENT;
			if (mob.isInCombat() && (mob.rangeToTarget() > 0))
				return Ability.QUALITY_INDIFFERENT;
			if (mob.fetchWieldedItem() == null)
				return Ability.QUALITY_INDIFFERENT;
			Item item = victim.fetchWieldedItem();
			if ((item == null)
					|| (!(item instanceof Weapon))
					|| (((Weapon) item).weaponClassification() == Weapon.CLASS_NATURAL))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB victim = mob.getVictim();
		if ((!mob.isInCombat()) || (victim == null)) {
			mob.tell("You must be in combat to do this!");
			return false;
		}
		if (mob.isInCombat() && (mob.rangeToTarget() > 0)) {
			mob.tell("You are too far away to try that!");
			return false;
		}
		if ((!auto) && (mob.fetchWieldedItem() == null)) {
			mob.tell("You need a weapon to break someone elses!");
			return false;
		}
		Item item = victim.fetchWieldedItem();
		if ((item == null)
				|| (!(item instanceof Weapon))
				|| (((Weapon) item).weaponClassification() == Weapon.CLASS_NATURAL)) {
			mob.tell(victim.charStats().HeShe() + " is not wielding a weapon!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int levelDiff = victim.phyStats().level()
				- (mob.phyStats().level() + (2 * super.getXLEVELLevel(mob)));
		if (levelDiff > 0)
			levelDiff = levelDiff * 5;
		else
			levelDiff = 0;
		Item hisWeapon = victim.fetchWieldedItem();
		int chance = (-levelDiff)
				+ (-(victim.charStats().getStat(CharStats.STAT_DEXTERITY) * 2));
		boolean hit = (auto) || CMLib.combat().rollToHit(mob, victim);
		boolean success = proficiencyCheck(mob, chance, auto) && (hit);
		if ((success)
				&& (hisWeapon != null)
				&& (hisWeapon.phyStats().ability() == 0)
				&& (!CMLib.flags().isABonusItems(hisWeapon))
				&& ((hisWeapon.rawProperLocationBitmap() == Wearable.WORN_WIELD) || (hisWeapon
						.rawProperLocationBitmap() == Wearable.WORN_WIELD
						+ Wearable.WORN_HELD))) {
			String str = auto ? hisWeapon.name()
					+ " break(s) in <T-HIS-HER> hands!"
					: "<S-NAME> attack(s) <T-NAMESELF> and destroy(s) "
							+ hisWeapon.name() + "!";
			hisWeapon.unWear();
			CMMsg msg = CMClass.getMsg(mob, victim, this,
					CMMsg.MSG_NOISYMOVEMENT, str);
			CMMsg msg2 = CMClass.getMsg(mob, hisWeapon, this, CMMsg.MASK_ALWAYS
					| CMMsg.MASK_MALICIOUS | CMMsg.TYP_CAST_SPELL, null);
			if (mob.location().okMessage(mob, msg)
					&& mob.location().okMessage(mob, msg2)) {
				mob.location().send(mob, msg);
				mob.location().send(mob, msg2);
				if (msg2.value() <= 0)
					hisWeapon.destroy();
				mob.location().recoverRoomStats();
			}
		} else if (hisWeapon != null)
			return maliciousFizzle(mob, victim,
					"<S-NAME> attempt(s) to destroy " + hisWeapon.name()
							+ " and fail(s)!");
		else
			return maliciousFizzle(mob, victim,
					"<S-NAME> attempt(s) to destroy <T-YOUPOSS> non-existant weapon and fail(s)!");
		return success;
	}

}
