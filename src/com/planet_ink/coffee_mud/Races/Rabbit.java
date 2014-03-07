package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Rabbit extends StdRace {
	public String ID() {
		return "Rabbit";
	}

	public String name() {
		return "Rabbit";
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
		return 10;
	}

	public int weightVariance() {
		return 10;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_HEAD | Wearable.WORN_FEET | Wearable.WORN_NECK
				| Wearable.WORN_EARS | Wearable.WORN_EYES);
	}

	public String racialCategory() {
		return "Rodent";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 1, 2, 4, 7, 15, 20, 21, 22 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_SEE_DARK);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 3);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 3);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public String arriveStr() {
		return "hops in";
	}

	public String leaveStr() {
		return "hops";
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a pair of sharp teeth");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_PIERCING);
		}
		return naturalWeapon;
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
		case Race.AGE_CHILD:
			switch (gender) {
			case 'M':
			case 'm':
				return "boy bunny";
			case 'F':
			case 'f':
				return "girl bunny";
			default:
				return "bunny";
			}
		case Race.AGE_YOUNGADULT:
		case Race.AGE_MATURE:
		case Race.AGE_MIDDLEAGED:
		default:
			switch (gender) {
			case 'M':
			case 'm':
				return name().toLowerCase() + " buck";
			case 'F':
			case 'f':
				return name().toLowerCase() + " doe";
			default:
				return name().toLowerCase();
			}
		case Race.AGE_OLD:
		case Race.AGE_VENERABLE:
		case Race.AGE_ANCIENT:
			switch (gender) {
			case 'M':
			case 'm':
				return "old " + name().toLowerCase() + " buck";
			case 'F':
			case 'f':
				return "old " + name().toLowerCase() + " doe";
			default:
				return "old " + name().toLowerCase();
			}
		}
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is one unhappy bunny!^N";
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

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a " + name().toLowerCase()
						+ " foot", RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " hair",
						RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource("some "
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
}