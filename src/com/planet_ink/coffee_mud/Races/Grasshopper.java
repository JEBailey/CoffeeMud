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
public class Grasshopper extends StdRace {
	public String ID() {
		return "Grasshopper";
	}

	public String name() {
		return "Grasshopper";
	}

	public int shortestMale() {
		return 1;
	}

	public int shortestFemale() {
		return 1;
	}

	public int heightVariance() {
		return 0;
	}

	public int lightestWeight() {
		return 1;
	}

	public int weightVariance() {
		return 0;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_TORSO | Wearable.WORN_EYES);
	}

	public String racialCategory() {
		return "Insect";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 2, 2, 0, 1, 1, 0, 0, 1, 2, 2, 0, 0, 1,
			0, 0, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 0, 0, 1, 1, 1, 1, 2, 2 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_SNEAKING);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
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
			naturalWeapon.setName("a nasty maw");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_NATURAL);
		}
		return naturalWeapon;
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pair of "
						+ name().toLowerCase() + " legs",
						RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
		case Race.AGE_CHILD:
			return "baby " + name().toLowerCase();
		default:
			return super.makeMobName('N', age);
		}
	}
}
