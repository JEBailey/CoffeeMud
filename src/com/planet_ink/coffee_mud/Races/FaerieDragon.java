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
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

/*
 Copyright 2006-2014 Bo Zimmerman

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
public class FaerieDragon extends StdRace {
	public String ID() {
		return "Faerie Dragon";
	}

	public String name() {
		return "Faerie Dragon";
	}

	public int shortestMale() {
		return 12;
	}

	public int shortestFemale() {
		return 14;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 10;
	}

	public int weightVariance() {
		return 15;
	}

	public long forbiddenWornBits() {
		return Wearable.WORN_WIELD | Wearable.WORN_WAIST | Wearable.WORN_BACK
				| Wearable.WORN_ABOUT_BODY | Wearable.WORN_FEET
				| Wearable.WORN_HANDS;
	}

	public String racialCategory() {
		return "Dragon";
	}

	private String[] racialAbilityNames = { "Spell_Invisibility",
			"Spell_FaerieFire", "WingFlying" };
	private int[] racialAbilityLevels = { 5, 5, 1 };
	private int[] racialAbilityProficiencies = { 100, 100, 100 };
	private boolean[] racialAbilityQuals = { false, false, false };

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

	private String[] culturalAbilityNames = { "Draconic" };
	private int[] culturalAbilityProficiencies = { 100 };

	public String[] culturalAbilityNames() {
		return culturalAbilityNames;
	}

	public int[] culturalAbilityProficiencies() {
		return culturalAbilityProficiencies;
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

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_SEE_INFRARED);
		if (!CMLib.flags().isSleeping(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_FLYING);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_STRENGTH,
				affectableStats.getStat(CharStats.STAT_STRENGTH) - 2);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,
				affectableStats.getStat(CharStats.STAT_DEXTERITY) + 5);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,
				affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 2);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("tiny talons");
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
						+ " claw", RawMaterial.RESOURCE_BONE));
				for (int i = 0; i < 3; i++)
					resources.addElement(makeResource("a strip of "
							+ name().toLowerCase() + " scales",
							RawMaterial.RESOURCE_SCALES));
				for (int i = 0; i < 2; i++)
					resources.addElement(makeResource("a pound of "
							+ name().toLowerCase() + " meat",
							RawMaterial.RESOURCE_DRAGONMEAT));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_DRAGONBLOOD));
			}
		}
		return resources;
	}
}
