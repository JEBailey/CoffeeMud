package com.planet_ink.coffee_mud.Abilities.Misc;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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

public class WanderHomeLater extends StdAbility {
	public String ID() {
		return "WanderHomeLater";
	}

	public String name() {
		return "WanderHomeLater";
	}

	public String displayText() {
		return "(Waiting til you're clear to go home)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (affected instanceof MOB) {
			MOB M = (MOB) affected;
			if (M.getStartRoom() == M.location())
				unInvoke();
			else if (M.amDead())
				unInvoke();
			else if (CMLib.flags().canActAtAll(M) && (!M.isInCombat())
					&& (M.amFollowing() == null) && (M.getStartRoom() != null)
					&& (M.getStartRoom().numPCInhabitants() == 0)) {
				CMLib.tracking().wanderAway(M, true, true);
				if (M.getStartRoom() == M.location())
					unInvoke();
			}
		}
		return super.tick(ticking, tickID);
	}
}
