package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
public class Skill_SlowFall extends BardSkill {
	public String ID() {
		return "Skill_SlowFall";
	}

	public String name() {
		return "Slow Fall";
	}

	public String displayText() {
		return activated ? "(Slow Fall)" : "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
	}

	public boolean activated = false;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (activated)
			affectableStats.setWeight(0);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (affected != null) {
			if ((affected.fetchEffect("Falling") != null)
					&& ((!(affected instanceof MOB))
							|| (((MOB) affected).fetchAbility(ID()) == null) || proficiencyCheck(
								(MOB) affected, 0, false))) {
				activated = true;
				affected.recoverPhyStats();
				if (affected instanceof MOB)
					helpProficiency((MOB) affected, 0);
			} else if (activated) {
				activated = false;
				affected.recoverPhyStats();
			}
		}
		return super.tick(ticking, tickID);
	}
}