package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;
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
public class Thief_RunningFight extends ThiefSkill {
	public String ID() {
		return "Thief_RunningFight";
	}

	public String name() {
		return "Running Fight";
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

	protected MOB lastOpponent = null;

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_DIRTYFIGHTING;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (lastOpponent != null)) {
			synchronized (lastOpponent) {
				MOB mob = (MOB) affected;
				if ((mob.location() != null)
						&& (mob.location().isInhabitant(lastOpponent))) {
					mob.setVictim(lastOpponent);
					lastOpponent.setVictim(mob);
					lastOpponent = null;
				}
			}
		}
		super.executeMsg(myHost, msg);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return super.okMessage(myHost, msg);

		MOB mob = (MOB) affected;
		if (msg.amISource(mob)
				&& (msg.targetMinor() == CMMsg.TYP_LEAVE)
				&& (mob.isInCombat())
				&& (mob.getVictim() != null)
				&& (msg.target() != null)
				&& (msg.target() instanceof Room)
				&& (msg.tool() != null)
				&& (msg.tool() instanceof Exit)
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null,
						0, false))
				&& ((CMLib.dice().rollPercentage() + mob.phyStats().level() + (2 * getXLEVELLevel(mob))) > mob
						.getVictim().charStats()
						.getSave(CharStats.STAT_SAVE_TRAPS))
				&& ((CMLib.dice().rollPercentage() + mob.phyStats().level() + (2 * getXLEVELLevel(mob))) > mob
						.getVictim().charStats()
						.getSave(CharStats.STAT_SAVE_MIND))) {
			MOB M = mob.getVictim();
			if ((M == null) || (M.getVictim() != mob)) {
				mob.tell(M, null, null, "<S-NAME> is not fighting you!");
				return false;
			}
			int dir = -1;
			for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
				if (mob.location().getRoomInDir(d) != null) {
					if ((mob.location().getRoomInDir(d) != null)
							&& (mob.location().getReverseExit(d) == msg.tool())) {
						dir = d;
						break;
					}
				}
			}
			if (dir < 0)
				return super.okMessage(myHost, msg);
			mob.makePeace();
			if (CMLib.tracking().walk(M, dir, false, false)) {
				M.setVictim(mob);
				lastOpponent = M;
			} else {
				M.setVictim(mob);
				mob.setVictim(M);
			}
		}
		return super.okMessage(myHost, msg);
	}
}
