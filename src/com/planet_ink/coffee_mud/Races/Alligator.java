package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
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
public class Alligator extends GreatLizard {
	public String ID() {
		return "Alligator";
	}

	public String name() {
		return "Alligator";
	}

	private String[] racialAbilityNames = { "Skill_Swim" };
	private int[] racialAbilityLevels = { 1 };
	private int[] racialAbilityProficiencies = { 100 };
	private boolean[] racialAbilityQuals = { false };

	protected String[] racialAbilityNames() {
		return racialAbilityNames;
	}

	protected int[] racialAbilityLevels() {
		return racialAbilityLevels;
	}

	protected int[] racialAbilityProficiencies() {
		return racialAbilityProficiencies;
	}

	protected boolean[] racialAbilityQuals() {
		return racialAbilityQuals;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_SWIMMING);
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
			return name().toLowerCase() + " hatchling";
		case Race.AGE_CHILD:
			return "young " + name().toLowerCase();
		default:
			return super.makeMobName(gender, age);
		}
	}
}
