package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;

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
public class Calf extends Cow {
	public String ID() {
		return "Calf";
	}

	public String name() {
		return "Cow";
	}

	public int shortestMale() {
		return 36;
	}

	public int shortestFemale() {
		return 36;
	}

	public int heightVariance() {
		return 6;
	}

	public int lightestWeight() {
		return 150;
	}

	public int weightVariance() {
		return 100;
	}

	public String racialCategory() {
		return "Bovine";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 10);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 5);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public boolean canBreedWith(Race R) {
		return false; // too young
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pair of "
						+ name().toLowerCase() + " hooves",
						RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource("a strip of "
						+ name().toLowerCase() + " leather",
						RawMaterial.RESOURCE_LEATHER));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " meat",
						RawMaterial.RESOURCE_BEEF));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource("a pile of "
						+ name().toLowerCase() + " bones",
						RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
