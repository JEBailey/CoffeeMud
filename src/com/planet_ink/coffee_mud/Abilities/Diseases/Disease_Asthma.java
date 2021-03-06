package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
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

public class Disease_Asthma extends Disease {
	public String ID() {
		return "Disease_Asthma";
	}

	public String name() {
		return "Asthma";
	}

	public String displayText() {
		return "(Asthma)";
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
		return 2;
	}

	protected int DISEASE_TICKS() {
		return 99999;
	}

	protected int DISEASE_DELAY() {
		return 5;
	}

	protected String DISEASE_DONE() {
		return "Your asthma clears up.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> start(s) wheezing.^?";
	}

	protected String DISEASE_AFFECT() {
		return "<S-NAME> wheeze(s) loudly.";
	}

	public int abilityCode() {
		return 0;
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
			if (CMLib.dice().rollPercentage() == 1) {
				int damage = mob.curState().getHitPoints() / 2;
				MOB diseaser = invoker;
				if (diseaser == null)
					diseaser = mob;
				CMLib.combat()
						.postDamage(diseaser, mob, this, damage,
								CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1,
								"<S-NAME> <S-HAS-HAVE> an asthma attack! It <DAMAGE> <S-NAME>!");
			} else
				mob.location().show(mob, null, CMMsg.MSG_NOISE,
						DISEASE_AFFECT());
			return true;
		}
		return true;
	}

	public void affectCharState(MOB affected, CharState affectableState) {
		if (affected == null)
			return;
		affectableState.setMovement(affectableState.getMovement() / 4);
	}
}
