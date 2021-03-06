package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Toadstool extends StdRace {
	public String ID() {
		return "Toadstool";
	}

	public String name() {
		return "Toadstool";
	}

	public int shortestMale() {
		return 1;
	}

	public int shortestFemale() {
		return 1;
	}

	public int heightVariance() {
		return 1;
	}

	public int lightestWeight() {
		return 1;
	}

	public int weightVariance() {
		return 1;
	}

	public long forbiddenWornBits() {
		return Integer.MAX_VALUE;
	}

	public String racialCategory() {
		return "Vegetation";
	}

	public int availabilityCode() {
		return 0;
	}

	public int[] getBreathables() {
		return breatheAnythingArray;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
			0, 0, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 0, 0, 0, 0, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setWeight(1);
		affectableStats.setHeight(1);
		affectableStats.setAttackAdjustment(0);
		affectableStats.setArmor(0);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_MOVE | PhyStats.CAN_NOT_SPEAK
				| PhyStats.CAN_NOT_TASTE);
		affectableStats.setDamage(0);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 1);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 1);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("the toadstool shuffle");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_MUSHROOMS);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}

	public String makeMobName(char gender, int age) {
		return super.makeMobName('N', Race.AGE_MATURE);
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is almost squashed!^N";
		else if (pct < .25)
			return "^y" + mob.name(viewer)
					+ "^y is severely gashed and bruised.^N";
		else if (pct < .40)
			return "^p" + mob.name(viewer)
					+ "^p has lots of gashes and bruises.^N";
		else if (pct < .55)
			return "^p" + mob.name(viewer) + "^p has some serious bruises.^N";
		else if (pct < .70)
			return "^g" + mob.name(viewer) + "^g has some bruises.^N";
		else if (pct < .85)
			return "^g" + mob.name(viewer) + "^g has a few small bruises.^N";
		else if (pct < .95)
			return "^g" + mob.name(viewer) + "^g is barely bruised.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				for (int i = 0; i < 5; i++)
					resources.addElement(makeResource("some "
							+ name().toLowerCase() + " flesh",
							RawMaterial.RESOURCE_MUSHROOMS));
			}
		}
		return resources;
	}
}
