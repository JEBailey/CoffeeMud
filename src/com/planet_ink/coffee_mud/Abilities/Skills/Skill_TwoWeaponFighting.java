package com.planet_ink.coffee_mud.Abilities.Skills;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Skill_TwoWeaponFighting extends StdSkill {
	public String ID() {
		return "Skill_TwoWeaponFighting";
	}

	public String name() {
		return "Two Weapon Fighting";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	protected boolean attackedSinceLastTick = false;

	protected Weapon getFirstWeapon(MOB mob) {
		Item I = mob.fetchWieldedItem();
		if (I instanceof Weapon)
			return (Weapon) I;
		return null;
	}

	private Weapon getSecondWeapon(MOB mob) {
		Item I = mob.fetchHeldItem();
		if ((I instanceof Weapon) && (!I.amWearingAt(Wearable.WORN_WIELD)))
			return (Weapon) I;
		return null;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;

			if ((getSecondWeapon(mob) != null) && (getFirstWeapon(mob) != null)
					&& (mob.isInCombat())) {
				int xlvl = super.getXLEVELLevel(invoker());
				affectableStats.setSpeed(affectableStats.speed() + 1.0
						+ (0.1 * xlvl));
				affectableStats.setAttackAdjustment(affectableStats
						.attackAdjustment()
						- (affectableStats.attackAdjustment() / (5 + xlvl)));
				affectableStats.setDamage(affectableStats.damage()
						- (affectableStats.damage() / (20 + xlvl)));
			}
		}
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		if ((msg.source() == affected) && (msg.target() instanceof MOB)
				&& (msg.sourceMinor() == CMMsg.TYP_WEAPONATTACK))
			attackedSinceLastTick = true;
		super.executeMsg(host, msg);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((tickID == Tickable.TICKID_MOB) && (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if ((mob != null) && (mob.isInCombat())) {
				if (CMath.bset(mob.getBitmap(), MOB.ATT_AUTODRAW))
					CMLib.commands().postDraw(mob, true, true);

				Item primaryWeapon = getFirstWeapon(mob);
				Item weapon = getSecondWeapon(mob);
				if ((weapon != null) // try to wield anything!
						&& (primaryWeapon != null)
						&& attackedSinceLastTick
						&& (mob.rangeToTarget() >= 0)
						&& (mob.rangeToTarget() >= weapon.minRange())
						&& (mob.rangeToTarget() <= weapon.maxRange())
						&& (CMLib.flags().aliveAwakeMobileUnbound(mob, true))
						&& (!mob.amDead())
						&& (mob.curState().getHitPoints() > 0)
						&& (CMLib.flags().isStanding(mob))
						&& (proficiencyCheck(mob, 0, false))
						&& (!mob.getVictim().amDead())) {
					primaryWeapon.setRawWornCode(Wearable.WORN_HELD);
					weapon.setRawWornCode(Wearable.WORN_WIELD);
					mob.recoverPhyStats();
					CMLib.combat().postAttack(mob, mob.getVictim(), weapon);
					weapon.setRawWornCode(Wearable.WORN_HELD);
					primaryWeapon.setRawWornCode(Wearable.WORN_WIELD);
					mob.recoverPhyStats();
					if (CMLib.dice().rollPercentage() == 1)
						helpProficiency(mob, 0);
				}
			}
			attackedSinceLastTick = false;
		}
		return super.tick(ticking, tickID);
	}
}
