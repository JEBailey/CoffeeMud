package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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

public class Disease_Cannibalism extends Disease {
	public String ID() {
		return "Disease_Cannibalism";
	}

	public String name() {
		return "Cannibalism";
	}

	public String displayText() {
		return "(Cannibalism)";
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
		return 6;
	}

	protected int DISEASE_TICKS() {
		return 999999;
	}

	protected int DISEASE_DELAY() {
		return 100;
	}

	protected String DISEASE_DONE() {
		String desiredMeat = "";
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			desiredMeat = mob.charStats().raceName();
		} else {
			desiredMeat = "your race's";
		}
		return "<S-NAME> no longer hunger for " + desiredMeat + " meat.";
	}

	protected String DISEASE_START() {
		String desiredMeat = "";
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			desiredMeat = mob.charStats().raceName();
		} else {
			desiredMeat = "your race's";
		}
		return "^G<S-NAME> hunger(s) for " + desiredMeat + " meat.^?";
	}

	protected String DISEASE_AFFECT() {
		return "";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_CONSUMPTION;
	}

	public void unInvoke() {
		if (affected == null)
			return;
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;

			super.unInvoke();
			if (canBeUninvoked())
				mob.tell(mob, null, this, DISEASE_DONE());
		} else
			super.unInvoke();
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)) {
			MOB source = msg.source();
			if (source == null)
				return false;
			MOB mob = (MOB) affected;
			if (msg.targetMinor() == CMMsg.TYP_EAT) {
				Environmental food = msg.target();
				if ((food != null)
						&& (food.name().toLowerCase()
								.indexOf(mob.charStats().raceName()) < 0)) {
					CMMsg newMessage = CMClass.getMsg(mob, null, this,
							CMMsg.MSG_OK_VISUAL,
							"^S<S-NAME> attempt(s) to eat " + food.Name()
									+ ", but can't stomach it....^?");
					if (mob.location().okMessage(mob, newMessage))
						mob.location().send(mob, newMessage);
					return false;
				}
			}
		}
		if ((affected != null) && (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if (msg.amITarget(mob) && (msg.tool() != null)
					&& (msg.tool().ID().equals("Spell_Hungerless"))) {
				mob.tell("You don't feel any less hungry.");
				return false;
			}
		}

		return super.okMessage(myHost, msg);
	}
}
