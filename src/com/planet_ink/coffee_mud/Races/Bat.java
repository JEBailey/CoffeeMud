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
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Bat extends StdRace {
	public String ID() {
		return "Bat";
	}

	public String name() {
		return "Bat";
	}

	public int shortestMale() {
		return 2;
	}

	public int shortestFemale() {
		return 2;
	}

	public int heightVariance() {
		return 2;
	}

	public int lightestWeight() {
		return 2;
	}

	public int weightVariance() {
		return 0;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_NECK | Wearable.WORN_HEAD | Wearable.WORN_EARS | Wearable.WORN_EYES);
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public String racialCategory() {
		return "Pteropine";
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
	private static final int[] parts = { 0, 2, 2, 1, 0, 0, 0, 1, 2, 2, 1, 0, 1,
			0, 1, 2 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 1, 2, 4, 7, 15, 20, 21, 22 };

	public int[] getAgingChart() {
		return agingChart;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_SEE_DARK);
		if (!CMLib.flags().isSleeping(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_FLYING);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 3);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 13);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("some bat fangs");
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
			return name().toLowerCase() + " pup";
		default:
			return super.makeMobName(gender, age);
		}
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer)
					+ "^r is fluttering around dripping blood everywhere!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is covered in bloody matted hair.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is bleeding badly from lots of wounds.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y has numerous bloody wounds and gashes.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer)
					+ "^y has some bloody wounds and gashes.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer) + "^p has a few bloody wounds.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer)
					+ "^p is cut and no longer flying straight.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer)
					+ "^g has some minor cuts and nicks.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer)
					+ "^g has a few nicks and scratches.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has a few small scratches.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " hair",
						RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource("a pair of "
						+ name().toLowerCase() + " wings",
						RawMaterial.RESOURCE_HIDE));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
