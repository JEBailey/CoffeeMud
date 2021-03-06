package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class WereAmphibian extends StdRace {
	public String ID() {
		return "WereAmphibian";
	}

	public String name() {
		return "WereAmphibian";
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
		return "Amphibian";
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

	public int[] getBreathables() {
		return breatheAirWaterArray;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 4, 8, 12, 16, 20, 24, 28, 32 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("sharp claws");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_SLASHING);
		}
		return naturalWeapon;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		final MOB mob = (MOB) affected;
		final Room R = mob.location();
		if ((R != null)
				&& ((R.domainType() == Room.DOMAIN_INDOORS_WATERSURFACE)
						|| (R.domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE)
						|| (R.domainType() == Room.DOMAIN_INDOORS_UNDERWATER)
						|| (R.domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER) || ((RawMaterial.CODES
						.GET(R.getAtmosphere()) & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID)))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_SWIMMING);
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is facing a cold death!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer) + "^r is covered in blood.^N";
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
			return "^p" + mob.name(viewer) + "^p is cut and bruised heavily.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer)
					+ "^g has some minor cuts and bruises.^N";
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
						+ " tongue", RawMaterial.RESOURCE_MEAT));
				for (int i = 0; i < 5; i++)
					resources.addElement(makeResource("a pound of "
							+ name().toLowerCase() + " meat",
							RawMaterial.RESOURCE_MEAT));
				for (int i = 0; i < 15; i++)
					resources.addElement(makeResource("a "
							+ name().toLowerCase() + " hide",
							RawMaterial.RESOURCE_HIDE));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
