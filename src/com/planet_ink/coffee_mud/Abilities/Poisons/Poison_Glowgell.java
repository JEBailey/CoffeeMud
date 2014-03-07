package com.planet_ink.coffee_mud.Abilities.Poisons;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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

public class Poison_Glowgell extends Poison {
	public String ID() {
		return "Poison_Glowgell";
	}

	public String name() {
		return "Glowgell";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_EXITS;
	}

	protected int POISON_DAMAGE() {
		return 0;
	}

	protected String POISON_DONE() {
		return "";
	}

	protected String POISON_START() {
		return "^G<S-NAME> start(s) glowing!^?";
	}

	protected String POISON_AFFECT() {
		return "";
	}

	protected String POISON_CAST() {
		return "^F^<FIGHT^><S-NAME> attempt(s) to smear something on <T-NAMESELF>!^</FIGHT^>^?";
	}

	protected String POISON_FAIL() {
		return "<S-NAME> attempt(s) to smear something on <T-NAMESELF>, but fail(s).";
	}

	protected boolean catchIt(MOB mob, Physical target) {
		return false;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GLOWING);
	}
}