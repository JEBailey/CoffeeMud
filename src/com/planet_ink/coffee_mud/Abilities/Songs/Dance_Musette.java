package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Dance_Musette extends Dance {
	public String ID() {
		return "Dance_Musette";
	}

	public String name() {
		return "Musette";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected String danceOf() {
		return name() + " Dance";
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (invoker == null)
			return;
		if (invoker == affected)
			return;

		affectableStats.setSpeed(CMath.div(affectableStats.speed(),
				2.0 + CMath.mul(super.getXLEVELLevel(invoker()), 0.30)));
	}
}
