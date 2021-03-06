package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
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
public class Dance_RagsSharqi extends Dance {
	public String ID() {
		return "Dance_RagsSharqi";
	}

	public String name() {
		return "Rags Sharqi";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected String danceOf() {
		return name() + " Dance";
	}

	public void affectCharState(MOB affectedMOB, CharState affectedState) {
		affectedState.setHitPoints(affectedState.getHitPoints()
				+ ((adjustedLevel(invoker(), 0) + 10) * 5));
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
		super.affectCharStats(affectedMOB, affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_DISEASE,
				affectedStats.getStat(CharStats.STAT_SAVE_DISEASE)
						+ (adjustedLevel(invoker(), 0) * 2));
	}
}
