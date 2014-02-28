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
public class Song_Charm extends Song {
	public String ID() {
		return "Song_Charm";
	}

	public String name() {
		return "Suave";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public void affectCharStats(MOB affectedMob, CharStats affectableStats) {
		super.affectCharStats(affectedMob, affectableStats);
		if (invoker == null)
			return;
		affectableStats.setStat(
				CharStats.STAT_CHARISMA,
				affectableStats.getStat(CharStats.STAT_CHARISMA) + 4
						+ super.getXLEVELLevel(invoker()));
	}
}
