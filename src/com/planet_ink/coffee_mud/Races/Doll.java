package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Doll extends StdRace {
	public String ID() {
		return "Doll";
	}

	public String name() {
		return "Doll";
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
		return 20;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Wood Golem";
	}

	public boolean fertile() {
		return false;
	}

	public int[] getBreathables() {
		return breatheAnythingArray;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 0, 0 };

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

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 5);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 5);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 13);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
	}

	public Weapon myNaturalWeapon() {
		return funHumanoidWeapon();
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is nearly disassembled!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is covered in tears and cracks.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is broken badly with lots of tears.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y has numerous tears and gashes.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y has some tears and gashes.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer) + "^p has a few cracks.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is scratched heavily.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g has some minor scratches.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g is a bit disheveled.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer)
					+ "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " clothes",
						RawMaterial.RESOURCE_COTTON));
				resources.addElement(makeResource("a pile of "
						+ name().toLowerCase() + " parts",
						RawMaterial.RESOURCE_WOOD));
			}
		}
		return resources;
	}
}
