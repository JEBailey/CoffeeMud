package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Dance_Jitterbug extends Dance {
	public String ID() {
		return "Dance_Jitterbug";
	}

	public String name() {
		return "Jitterbug";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (invoker == null)
			return;
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
				- adjustedLevel(invoker(), 0));
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		if (invoker == null)
			return;
		affectableStats
				.setStat(CharStats.STAT_DEXTERITY,
						(int) Math.round(CMath.div(affectableStats
								.getStat(CharStats.STAT_DEXTERITY), 3.0)));
		int bonus = adjustedLevel(invoker(), 0) * 2;
		affectableStats.setStat(CharStats.STAT_SAVE_ACID,
				affectableStats.getStat(CharStats.STAT_SAVE_ACID) + bonus);
		affectableStats.setStat(CharStats.STAT_SAVE_COLD,
				affectableStats.getStat(CharStats.STAT_SAVE_COLD) + bonus);
		affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC,
				affectableStats.getStat(CharStats.STAT_SAVE_ELECTRIC) + bonus);
		affectableStats.setStat(CharStats.STAT_SAVE_FIRE,
				affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + bonus);
		affectableStats.setStat(CharStats.STAT_SAVE_GAS,
				affectableStats.getStat(CharStats.STAT_SAVE_GAS) + bonus);
	}
}
