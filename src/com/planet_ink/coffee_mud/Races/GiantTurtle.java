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
public class GiantTurtle extends StdRace {
	public String ID() {
		return "GiantTurtle";
	}

	public String name() {
		return "Giant Turtle";
	}

	public int shortestMale() {
		return 58;
	}

	public int shortestFemale() {
		return 58;
	}

	public int heightVariance() {
		return 20;
	}

	public int lightestWeight() {
		return 1500;
	}

	public int weightVariance() {
		return 200;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_HEAD | Wearable.WORN_EYES);
	}

	public String racialCategory() {
		return "Reptile";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1,
			0, 0, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 4, 8, 16, 28, 60, 80, 82, 84 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 26);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 2);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public String arriveStr() {
		return "crawls in";
	}

	public String leaveStr() {
		return "crawls";
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a snapping jaw");
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
			return name().toLowerCase() + " hatchling";
		default:
			return super.makeMobName('N', age);
		}
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is facing a slow death!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is slowly being covered in blood.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer) + "^r is slowly bleeding badly.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer) + "^y has numerous bloody wounds.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y has some bloody wounds.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer) + "^p has a few bloody wounds.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is cut and bruised heavily.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer)
					+ "^g has some minor cuts and bruises.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer)
					+ "^g has a few bruises and scratched shell.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has a few small bruises.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a " + name().toLowerCase()
						+ " shell", RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
