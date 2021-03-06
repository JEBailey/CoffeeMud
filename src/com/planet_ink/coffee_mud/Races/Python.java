package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Python extends Snake {
	public String ID() {
		return "Python";
	}

	public String name() {
		return "Python";
	}

	public int shortestMale() {
		return 6;
	}

	public int shortestFemale() {
		return 6;
	}

	public int heightVariance() {
		return 3;
	}

	public int lightestWeight() {
		return 15;
	}

	public int weightVariance() {
		return 20;
	}

	public String racialCategory() {
		return "Serpent";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1,
			0, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a fearsome hug");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_LEATHER);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 15);
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				for (int i = 0; i < 5; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " scales",
							RawMaterial.RESOURCE_SCALES));
				for (int i = 0; i < 3; i++)
					resources.addElement(makeResource("a pound of "
							+ name().toLowerCase() + " meat",
							RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
