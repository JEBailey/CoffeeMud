package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class Insect extends StdRace {
	public String ID() {
		return "Insect";
	}

	public String name() {
		return "Insect";
	}

	public int shortestMale() {
		return 2;
	}

	public int shortestFemale() {
		return 2;
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
		return Integer.MAX_VALUE;
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

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		MOB mob = (MOB) myHost;
		if (msg.amISource(mob)
				&& (!msg.amITarget(mob))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.target() instanceof MOB)
				&& (mob.fetchWieldedItem() == null)
				&& (msg.tool() != null)
				&& (msg.tool() instanceof Weapon)
				&& (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_NATURAL)
				&& (!((MOB) msg.target()).isMonster())
				&& (((msg.value()) > (((MOB) msg.target()).maxState()
						.getHitPoints() / 20)))
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
			Ability A = CMClass.getAbility("Disease_Lyme");
			if ((A != null)
					&& (((MOB) msg.target()).fetchEffect(A.ID()) == null))
				A.invoke(mob, (MOB) msg.target(), true, 0);
		}
		super.executeMsg(myHost, msg);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 3);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 3);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,
				affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
	}

	public String arriveStr() {
		return "creeps in";
	}

	public String leaveStr() {
		return "creeps";
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
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " guts",
						RawMaterial.RESOURCE_MEAT));
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