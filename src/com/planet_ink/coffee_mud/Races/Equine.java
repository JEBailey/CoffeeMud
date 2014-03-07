package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
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
public class Equine extends StdRace {
	public String ID() {
		return "Equine";
	}

	public String name() {
		return "Equine";
	}

	public int shortestMale() {
		return 60;
	}

	public int shortestFemale() {
		return 60;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 350;
	}

	public int weightVariance() {
		return 100;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_HEAD | Wearable.WORN_EARS | Wearable.WORN_EYES);
	}

	public String racialCategory() {
		return "Equine";
	}

	private String[] racialAbilityNames = { "Fighter_Kick" };
	private int[] racialAbilityLevels = { 5 };
	private int[] racialAbilityProficiencies = { 40 };
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

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 1, 2, 6, 9, 18, 24, 28, 32 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 18);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 6);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a pair of hooves");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
		}
		return naturalWeapon;
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
			return name().toLowerCase() + " foal";
		case Race.AGE_CHILD:
		case Race.AGE_YOUNGADULT:
			switch (gender) {
			case 'M':
			case 'm':
				return name().toLowerCase() + " colt";
			case 'F':
			case 'f':
				return name().toLowerCase() + " filly";
			default:
				return "young " + name().toLowerCase();
			}
		case Race.AGE_MATURE:
		case Race.AGE_MIDDLEAGED:
		default:
			switch (gender) {
			case 'M':
			case 'm':
				return name().toLowerCase() + " stud";
			case 'F':
			case 'f':
				return name().toLowerCase() + " stallion";
			default:
				return name().toLowerCase();
			}
		case Race.AGE_OLD:
		case Race.AGE_VENERABLE:
		case Race.AGE_ANCIENT:
			switch (gender) {
			case 'M':
			case 'm':
				return "old male " + name().toLowerCase();
			case 'F':
			case 'f':
				return "old female " + name().toLowerCase();
			default:
				return "old " + name().toLowerCase();
			}
		}
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

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("" + name().toLowerCase()
						+ " mane", RawMaterial.RESOURCE_FUR));
				for (int i = 0; i < 2; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " leather",
							RawMaterial.RESOURCE_LEATHER));
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