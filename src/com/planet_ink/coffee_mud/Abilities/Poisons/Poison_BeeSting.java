package com.planet_ink.coffee_mud.Abilities.Poisons;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;

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

public class Poison_BeeSting extends Poison {
	public String ID() {
		return "Poison_BeeSting";
	}

	public String name() {
		return "Bee Sting";
	}

	private static final String[] triggerStrings = { "POISONSTING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int POISON_TICKS() {
		return 10;
	} // 0 means no adjustment!

	protected int POISON_DELAY() {
		return 2;
	}

	protected String POISON_DONE() {
		return "The stinging poison runs its course.";
	}

	protected String POISON_START() {
		return "^G<S-NAME> turn(s) green.^?";
	}

	protected String POISON_AFFECT() {
		return "<S-NAME> cringe(s) from the poisonous itch.";
	}

	protected String POISON_CAST() {
		return "^F^<FIGHT^><S-NAME> sting(s) <T-NAMESELF>!^</FIGHT^>^?";
	}

	protected String POISON_FAIL() {
		return "<S-NAME> attempt(s) to sting <T-NAMESELF>, but fail(s).";
	}

	protected int POISON_DAMAGE() {
		return (invoker != null) ? 2 : 0;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,
				affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 1);
		affectableStats.setStat(CharStats.STAT_STRENGTH,
				affectableStats.getStat(CharStats.STAT_STRENGTH) - 1);
		if (affectableStats.getStat(CharStats.STAT_CONSTITUTION) <= 0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION, 1);
		if (affectableStats.getStat(CharStats.STAT_STRENGTH) <= 0)
			affectableStats.setStat(CharStats.STAT_STRENGTH, 1);
	}
}
