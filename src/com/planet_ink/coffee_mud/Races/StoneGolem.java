package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
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
public class StoneGolem extends StdRace {
	public String ID() {
		return "StoneGolem";
	}

	public String name() {
		return "Stone Golem";
	}

	public int shortestMale() {
		return 64;
	}

	public int shortestFemale() {
		return 60;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 400;
	}

	public int weightVariance() {
		return 100;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Stone Golem";
	}

	public boolean fertile() {
		return false;
	}

	public boolean uncharmable() {
		return true;
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

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,
				affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,
				affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_GAS,
				affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS,
				affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD,
				affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,
				affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_FIRE,
				affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 100);
	}

	public String makeMobName(char gender, int age) {
		return makeMobName('N', Race.AGE_MATURE);
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is near destruction!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is massively cracked and damaged.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is extremely cracked and damaged.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y is very cracked and damaged.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y is cracked and damaged.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p is cracked and slightly damaged.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is showing large cracks.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g is showing some cracks.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g is showing small cracks.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer)
					+ "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pound of stone",
						RawMaterial.RESOURCE_STONE));
				resources.addElement(makeResource("essence of golem",
						RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
