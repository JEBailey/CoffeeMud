package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Thief_TrapImmunity extends ThiefSkill {
	public String ID() {
		return "Thief_TrapImmunity";
	}

	public String name() {
		return "Trap Immunity";
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
		return Ability.QUALITY_OK_SELF;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_DETRAP;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		affectableStats
				.setStat(CharStats.STAT_SAVE_TRAPS,
						affectableStats.getStat(CharStats.STAT_SAVE_TRAPS)
								+ (proficiency() / 2)
								+ (2 * getXLEVELLevel(invoker())));
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return super.okMessage(myHost, msg);
		MOB mob = (MOB) affected;
		if (msg.amITarget(mob) && (!msg.amISource(mob)) && (msg.tool() != null)
				&& (msg.tool() instanceof Trap)) {
			mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
					"<S-NAME> deftly avoid(s) a trap.");
			helpProficiency(mob, 0);
			return false;
		}
		return super.okMessage(myHost, msg);
	}
}
