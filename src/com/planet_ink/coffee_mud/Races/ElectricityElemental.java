package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class ElectricityElemental extends StdRace {
	public String ID() {
		return "ElectricityElemental";
	}

	public String name() {
		return "Electricity Elemental";
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
		return "Electricity Elemental";
	}

	public boolean fertile() {
		return false;
	}

	public boolean uncharmable() {
		return true;
	}

	protected boolean destroyBodyAfterUse() {
		return true;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 0, 0, 0, 0, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER };

	public int[] getAgingChart() {
		return agingChart;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,
				affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,
				affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC,
				affectableStats.getStat(CharStats.STAT_SAVE_ELECTRIC) + 100);
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a deadly spark");
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_ELECTRICITY);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_STRIKING);
		}
		return naturalWeapon;
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
					+ "^r is flickering alot and massively damaged.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is flickering alot and extremely damaged.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y is flickering alot and very damaged.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y is flickering and damaged.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p is flickering and slightly damaged.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is showing large flickers.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g is showing some flickers.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g is showing small flickers.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer)
					+ "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a small conductive filing",
						RawMaterial.RESOURCE_IRON));
			}
		}
		return resources;
	}
}
