package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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

public class Disease_Fleas extends Disease {
	public String ID() {
		return "Disease_Fleas";
	}

	public String name() {
		return "Fleas";
	}

	public String displayText() {
		return "(Fleas)";
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

	public int difficultyLevel() {
		return 1;
	}

	protected int DISEASE_TICKS() {
		return 35;
	}

	protected int DISEASE_DELAY() {
		return 5;
	}

	protected String DISEASE_DONE() {
		return "Your problem with fleas clears up.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> <S-HAS-HAVE> fleas!^?";
	}

	protected String DISEASE_AFFECT() {
		return "<S-NAME> scratch(es) <S-HIM-HERSELF>.";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_CONTACT | DiseaseAffect.SPREAD_STD
				| DiseaseAffect.SPREAD_PROXIMITY;
	}

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
			mob.location().show(mob, null, CMMsg.MSG_NOISYMOVEMENT,
					DISEASE_AFFECT());
			catchIt(mob);
			return true;
		}
		return true;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		if (affected == null)
			return;
		affectableStats.setStat(CharStats.STAT_DEXTERITY,
				affectableStats.getStat(CharStats.STAT_DEXTERITY) - 4);
		if (affectableStats.getStat(CharStats.STAT_DEXTERITY) <= 0)
			affectableStats.setStat(CharStats.STAT_DEXTERITY, 1);
	}
}