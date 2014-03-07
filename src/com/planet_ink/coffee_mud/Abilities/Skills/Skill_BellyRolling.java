package com.planet_ink.coffee_mud.Abilities.Skills;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Skill_BellyRolling extends StdSkill {
	public String ID() {
		return "Skill_BellyRolling";
	}

	public String name() {
		return "Belly Rolling";
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
		return Ability.ACODE_SKILL | Ability.DOMAIN_FOOLISHNESS;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	protected boolean doneThisRound = false;

	public boolean tick(Tickable ticking, int tickID) {
		if (tickID == Tickable.TICKID_MOB)
			doneThisRound = false;
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;

		if (msg.amITarget(mob) && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
				&& (CMLib.flags().aliveAwakeMobile(mob, true))
				&& (CMLib.flags().isSitting(mob)) && (msg.tool() != null)
				&& (!doneThisRound) && (msg.tool() instanceof Weapon)) {
			// can't use -NAME for msg.source() lest sitting prevent it
			CMMsg msg2 = CMClass.getMsg(mob, msg.source(), null,
					CMMsg.MSG_SITMOVE,
					"<S-NAME> roll(s) away from the attack by <T-NAMESELF>!");
			if ((proficiencyCheck(null,
					mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 50
							+ (super.getXLEVELLevel(mob) * 5), false))
					&& ((msg.source().getVictim() == mob) || (msg.source()
							.getVictim() == null))
					&& (mob.location().okMessage(mob, msg2))) {
				doneThisRound = true;
				mob.location().send(mob, msg2);
				helpProficiency(mob, 0);
				return false;
			}
		}
		return true;
	}
}