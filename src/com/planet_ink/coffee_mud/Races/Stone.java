package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
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
public class Stone extends StdRace {
	public String ID() {
		return "Stone";
	}

	public String name() {
		return "Stone";
	}

	public int shortestMale() {
		return 2;
	}

	public int shortestFemale() {
		return 2;
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
		return "Stone Golem";
	}

	public boolean uncharmable() {
		return true;
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

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_SPEAK | PhyStats.CAN_NOT_TASTE);
		affectableStats.setArmor(affectableStats.armor()
				+ affectableStats.armor());
		affectableStats.setAttackAdjustment(0);
		affectableStats.setDamage(0);
	}

	public void affectCharState(MOB affectedMOB, CharState affectableState) {
		affectableState.setHitPoints(affectableState.getHitPoints() * 4);
		affectableState.setHunger(999999);
		affectedMOB.curState().setHunger(affectableState.getHunger());
		affectableState.setThirst(999999);
		affectedMOB.curState().setThirst(affectableState.getThirst());
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

	public String arriveStr() {
		return "rolls in";
	}

	public String leaveStr() {
		return "rolls";
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("<S-HIS-HER> body");
			naturalWeapon.setRanges(0, 3);
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_STONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
		}
		return naturalWeapon;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((myHost != null) && (myHost instanceof MOB)
				&& (msg.amISource((MOB) myHost))) {
			if (((msg.targetMinor() == CMMsg.TYP_LEAVE)
					|| (msg.sourceMinor() == CMMsg.TYP_ADVANCE)
					|| (msg.sourceMinor() == CMMsg.TYP_RETREAT) || (msg
						.sourceMinor() == CMMsg.TYP_RECALL))) {
				msg.source().tell(
						"You can't really go anywhere -- you're a rock!");
				return false;
			}
		} else if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
				&& (myHost instanceof MOB) && (msg.amITarget(myHost))
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))) {
			MOB target = (MOB) msg.target();
			if ((!target.isInCombat()) && (msg.source().isMonster())
					&& (msg.source().location() == target.location())
					&& (msg.source().getVictim() != target)) {
				msg.source().tell("Attack a rock?!");
				if (target.getVictim() == msg.source()) {
					target.makePeace();
					target.setVictim(null);
				}
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public String makeMobName(char gender, int age) {
		return makeMobName('N', Race.AGE_MATURE);
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is almost broken!^N";
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
			return "^p" + mob.name(viewer) + "^p is showing numerous cracks.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g is showing some crachs.^N";
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
				resources.addElement(makeResource("some pebbles",
						RawMaterial.RESOURCE_STONE));
			}
		}
		return resources;
	}

}
