package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;

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
public class Minotaur extends Cow {
	public String ID() {
		return "Minotaur";
	}

	public String name() {
		return "Minotaur";
	}

	public int shortestMale() {
		return 65;
	}

	public int shortestFemale() {
		return 64;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 450;
	}

	public int weightVariance() {
		return 100;
	}

	public long forbiddenWornBits() {
		return Wearable.WORN_HEAD;
	}

	public String racialCategory() {
		return "Bovine";
	}

	private int[] agingChart = { 0, 1, 3, 15, 35, 53, 70, 74, 78 };

	public int[] getAgingChart() {
		return agingChart;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a pair of deadly horns");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_PIERCING);
		}
		return naturalWeapon;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		affectableStats.setStat(CharStats.STAT_STRENGTH,
				affectableStats.getStat(CharStats.STAT_STRENGTH) + 5);
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
		case Race.AGE_CHILD:
			return name().toLowerCase() + " calf";
		case Race.AGE_YOUNGADULT:
			switch (gender) {
			case 'M':
			case 'm':
				return "young " + name().toLowerCase() + " bull";
			case 'F':
			case 'f':
				return "young " + name().toLowerCase() + " cow";
			default:
				return "young " + name().toLowerCase();
			}
		default:
			return super.makeMobName(gender, age);
		}
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pair of "
						+ name().toLowerCase() + " horns",
						RawMaterial.RESOURCE_BONE));
				for (int i = 0; i < 10; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " leather",
							RawMaterial.RESOURCE_LEATHER));
				for (int i = 0; i < 2; i++)
					resources.addElement(makeResource("a pound of "
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
