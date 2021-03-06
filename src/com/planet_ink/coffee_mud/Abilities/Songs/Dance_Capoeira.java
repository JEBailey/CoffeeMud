package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
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
public class Dance_Capoeira extends Dance {
	public String ID() {
		return "Dance_Capoeira";
	}

	public String name() {
		return "Capoeira";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	protected String danceOf() {
		return name() + " Dance";
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if ((affected instanceof MOB)
				&& (((MOB) affected).fetchWieldedItem() == null)) {
			affectableStats.setAttackAdjustment(affectableStats
					.attackAdjustment()
					+ invoker().charStats().getStat(CharStats.STAT_CHARISMA)
					+ (2 * adjustedLevel(invoker(), 0)));
			affectableStats.setDamage(affectableStats.damage()
					+ (adjustedLevel(invoker(), 0) / 3));
		}
	}
}
