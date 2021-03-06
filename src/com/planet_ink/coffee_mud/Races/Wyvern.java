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
public class Wyvern extends StdRace {
	public String ID() {
		return "Wyvern";
	}

	public String name() {
		return "Wyvern";
	}

	public int shortestMale() {
		return 64;
	}

	public int shortestFemale() {
		return 58;
	}

	public int heightVariance() {
		return 40;
	}

	public int lightestWeight() {
		return 1500;
	}

	public int weightVariance() {
		return 500;
	}

	public long forbiddenWornBits() {
		return Wearable.WORN_HELD | Wearable.WORN_WIELD | Wearable.WORN_WAIST;
	}

	public String racialCategory() {
		return "Reptile";
	}

	private String[] culturalAbilityNames = { "Draconic" };
	private int[] culturalAbilityProficiencies = { 50 };

	protected String[] culturalAbilityNames() {
		return culturalAbilityNames;
	}

	protected int[] culturalAbilityProficiencies() {
		return culturalAbilityProficiencies;
	}

	private String[] racialAbilityNames = { "WingFlying" };
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

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1,
			1, 1, 2 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 5, 20, 110, 325, 500, 850, 950, 1050 };

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
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 13);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 10);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("huge talons");
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
			return name().toLowerCase() + " hatchling";
		case Race.AGE_CHILD:
		default:
			return super.makeMobName(gender, age);
		}
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is raging in bloody pain!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer) + "^r is covered in blood.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is bleeding badly from lots of wounds.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer)
					+ "^y has some bloody wounds and gashed scales.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer) + "^p has a few bloody wounds.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is cut and bruised heavily.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer)
					+ "^g has a few bruises and scratched scales.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has a few small bruises.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a " + name().toLowerCase()
						+ " tail", RawMaterial.RESOURCE_BONE));
				for (int i = 0; i < 50; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " scales",
							RawMaterial.RESOURCE_SCALES));
				for (int i = 0; i < 20; i++)
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
