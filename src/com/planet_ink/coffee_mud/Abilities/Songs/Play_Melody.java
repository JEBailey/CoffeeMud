package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
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
public class Play_Melody extends Play {
	public String ID() {
		return "Play_Melody";
	}

	public String name() {
		return "Melody";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected String songOf() {
		return "a " + name();
	}

	public void affectCharStats(MOB mob, CharStats stats) {
		super.affectCharStats(mob, stats);
		if (mob == invoker)
			return;
		if (invoker() != null)
			stats.setStat(
					CharStats.STAT_SAVE_MIND,
					stats.getStat(CharStats.STAT_SAVE_MIND)
							- (invoker().charStats().getStat(
									CharStats.STAT_CHARISMA) + (adjustedLevel(
									invoker(), 0) * 2)));
	}
}
