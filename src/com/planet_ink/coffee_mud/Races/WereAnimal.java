package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;

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
public class WereAnimal extends StdRace {
	public String ID() {
		return "WereAnimal";
	}

	public String name() {
		return "WereAnimal";
	}

	public int shortestMale() {
		return 59;
	}

	public int shortestFemale() {
		return 59;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 80;
	}

	public int weightVariance() {
		return 80;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Animal";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 4, 8, 12, 16, 20, 24, 28, 32 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " claws",
						RawMaterial.RESOURCE_BONE));
				for (int i = 0; i < 4; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " hide",
							RawMaterial.RESOURCE_FUR));
				for (int i = 0; i < 2; i++)
					resources.addElement(makeResource("a pound of "
							+ name().toLowerCase() + " meat",
							RawMaterial.RESOURCE_MEAT));
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

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("sharp claws");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_PIERCING);
		}
		return naturalWeapon;
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is hovering on deaths door!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is covered in blood and matted hair.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is bleeding badly from lots of wounds.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y has large patches of bloody matted fur.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y has some bloody matted fur.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p has a lot of cuts and gashes.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p has a few cut patches.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g has a cut patch of fur.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g has some disheveled fur.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has some misplaced hairs.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}
}
