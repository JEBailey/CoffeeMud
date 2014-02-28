package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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

public class Disease_Tinnitus extends Disease {
	public String ID() {
		return "Disease_Tinnitus";
	}

	public String name() {
		return "Tinnitus";
	}

	public String displayText() {
		return "(Tinnitus)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public boolean putInCommandlist() {
		return false;
	}

	protected int DISEASE_TICKS() {
		return 100;
	}

	protected int DISEASE_DELAY() {
		return 1;
	}

	protected String DISEASE_DONE() {
		return "Your ears stop ringing.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> come(s) down with tinnitus.^?";
	}

	protected String DISEASE_AFFECT() {
		return "";
	}

	public int abilityCode() {
		return 0;
	}

	public int difficultyLevel() {
		return 4;
	}

	protected boolean ringing = false;

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (affected == null)
			return false;
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
			diseaseTick = DISEASE_DELAY();
			if (CMLib.dice().rollPercentage() > mob.charStats().getSave(
					CharStats.STAT_SAVE_DISEASE))
				ringing = true;
			else
				ringing = false;
			mob.recoverPhyStats();
			return true;
		}
		return true;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if ((affected == null) || (!ringing))
			return;
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_HEAR);
	}
}
