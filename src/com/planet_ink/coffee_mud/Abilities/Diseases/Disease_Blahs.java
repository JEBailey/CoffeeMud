package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
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

public class Disease_Blahs extends Disease {
	public String ID() {
		return "Disease_Blahs";
	}

	public String name() {
		return "Blahs";
	}

	public String displayText() {
		return "(The Blahs)";
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
		return 4;
	}

	protected int DISEASE_TICKS() {
		return 99999;
	}

	protected int DISEASE_DELAY() {
		return 20;
	}

	protected String DISEASE_DONE() {
		return "You feel a little better.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> get(s) the blahs.^?";
	}

	protected String DISEASE_AFFECT() {
		return "<S-NAME> sigh(s).";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_CONSUMPTION;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (affected instanceof MOB) {
			if (msg.source() != affected)
				return true;
			if (msg.source().location() == null)
				return true;

			if ((msg.amISource((MOB) affected))
					&& (msg.sourceMessage() != null)
					&& (msg.tool() == null)
					&& ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
							|| (msg.sourceMinor() == CMMsg.TYP_TELL) || (CMath
								.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL)))) {
				Ability A = CMClass.getAbility("Blah");
				if (A != null) {
					A.setProficiency(100);
					A.invoke(msg.source(), null, true, 0);
					A.setAffectedOne(msg.source());
					if (!A.okMessage(myHost, msg))
						return false;
				}
			}
		} else {

		}
		return true;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (affected == null)
			return false;
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if (mob.curState().getFatigue() < CharState.FATIGUED_MILLIS)
			mob.curState().setFatigue(CharState.FATIGUED_MILLIS);
		return true;
	}

}
