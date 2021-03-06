package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Shield;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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

public class Fighter_ShieldBlock extends FighterSkill {
	public int hits = 0;

	public String ID() {
		return "Fighter_ShieldBlock";
	}

	public String name() {
		return "Shield Block";
	}

	public String displayText() {
		return "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_SHIELDUSE;
	}

	protected volatile int amountOfShieldArmor = -1;

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;

		if (msg.amITarget(mob)
				&& (amountOfShieldArmor > 0)
				&& (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
				&& (CMLib.flags().aliveAwakeMobileUnbound(mob, true))
				&& (msg.tool() != null)
				&& (msg.tool() instanceof Weapon)
				&& (proficiencyCheck(null,
						mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 90
								+ (2 * getXLEVELLevel(mob)), false))
				&& (msg.source().getVictim() == mob)) {
			CMMsg msg2 = CMClass.getMsg(msg.source(), mob, mob.fetchHeldItem(),
					CMMsg.MSG_QUIETMOVEMENT,
					"<T-NAME> block(s) <S-YOUPOSS> attack with <O-NAME>!");
			if (mob.location().okMessage(mob, msg2)) {
				mob.location().send(mob, msg2);
				helpProficiency(mob, 0);
				return false;
			}
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);

		if (!(affected instanceof MOB))
			return;

		MOB mob = (MOB) affected;

		if (msg.amISource(mob) && (msg.target() instanceof Shield))
			amountOfShieldArmor = -1;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((amountOfShieldArmor < 0) && (tickID == Tickable.TICKID_MOB)
				&& (ticking instanceof MOB)) {
			amountOfShieldArmor = 0;
			for (Enumeration<Item> i = ((MOB) ticking).items(); i
					.hasMoreElements();) {
				final Item I = i.nextElement();
				if ((I instanceof Shield)
						&& (I.amWearingAt(Wearable.WORN_HELD) || I
								.amWearingAt(Wearable.WORN_WIELD))
						&& (I.owner() == ticking) && (I.container() == null))
					amountOfShieldArmor += I.phyStats().armor();
			}
			((MOB) ticking).recoverPhyStats();
		}
		return true;
	}

	public void affectPhyStats(Physical affected, PhyStats stats) {
		super.affectPhyStats(affected, stats);
		if ((affected instanceof MOB) && (amountOfShieldArmor > 0)) {
			stats.setArmor(stats.armor()
					- (int) Math.round(CMath.mul(amountOfShieldArmor,
							CMath.mul(getXLEVELLevel((MOB) affected), 0.5))));
		}
	}

	public boolean autoInvocation(MOB mob) {
		amountOfShieldArmor = -1;
		return super.autoInvocation(mob);
	}
}
