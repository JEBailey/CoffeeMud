package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class TreeGolem extends StdRace {
	public String ID() {
		return "TreeGolem";
	}

	public String name() {
		return "Tree Golem";
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
		return Integer.MAX_VALUE;
	}

	public String racialCategory() {
		return "Vegetation";
	}

	public boolean uncharmable() {
		return true;
	}

	public int[] getBreathables() {
		return breatheAnythingArray;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 0, 0, 0, 0, 8, 8, 1, 0, 0, 0, 0, 0,
			0, 0, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 10, 20, 50, 65, 75, 100, 150, 160 };

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

	public String arriveStr() {
		return "slides in";
	}

	public String leaveStr() {
		return "slides";
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("a jagged limb");
			naturalWeapon.setRanges(0, 2);
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_OAK);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
		}
		return naturalWeapon;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((myHost != null) && (myHost instanceof MOB)
				&& (msg.amISource((MOB) myHost))) {
			if (msg.targetMinor() == CMMsg.TYP_LEAVE) {
				msg.source().tell(
						"You can't really go anywhere -- you are rooted!");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		affectableStats.setStat(CharStats.STAT_GENDER, 'N');
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
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is near destruction!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is massively splintered and broken.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is extremely splintered and broken.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y is very splintered and broken.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y is splintered and broken.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p is splintered and slightly broken.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p has lost lots of leaves.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g has lost some more leaves.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g has lost a few leaves.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer)
					+ "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
			return name().toLowerCase() + " seedling";
		case Race.AGE_TODDLER:
		case Race.AGE_CHILD:
			return name().toLowerCase() + " sapling";
		default:
			return super.makeMobName('N', age);
		}
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pound of wood",
						RawMaterial.RESOURCE_WOOD));
				resources.addElement(makeResource("a pound of leaves",
						RawMaterial.RESOURCE_GREENS));
			}
		}
		return resources;
	}
}
