package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Social;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Monkey extends StdRace {
	public String ID() {
		return "Monkey";
	}

	public String name() {
		return "Monkey";
	}

	public int shortestMale() {
		return 18;
	}

	public int shortestFemale() {
		return 18;
	}

	public int heightVariance() {
		return 6;
	}

	public int lightestWeight() {
		return 50;
	}

	public int weightVariance() {
		return 60;
	}

	public long forbiddenWornBits() {
		return ~(Wearable.WORN_HEAD | Wearable.WORN_FEET | Wearable.WORN_NECK
				| Wearable.WORN_HELD | Wearable.WORN_WIELD | Wearable.WORN_EARS | Wearable.WORN_EYES);
	}

	public String racialCategory() {
		return "Primate";
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 1, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 1, 2, 4, 7, 15, 20, 21, 22 };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 13);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 15);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
	}

	public Weapon myNaturalWeapon() {
		return funHumanoidWeapon();
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is hovering on deaths door!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is covered in blood and matted hair.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is bleeding badly from lots of wounds.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y has large patches of bloody matted fur.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer) + "^y has some bloody matted fur.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p has a lot of cuts and gashes.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p has a few cut patches.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g has a cut patch of fur.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g has some disheveled fur.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has some misplaced hairs.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		// the sex rules
		if (!(myHost instanceof MOB))
			return;

		MOB myChar = (MOB) myHost;
		if ((msg.amITarget(myChar))
				&& (CMLib.dice().rollPercentage() < 10)
				&& (msg.tool() instanceof Social)
				&& (msg.tool().Name().equals("MATE <T-NAME>") || msg.tool()
						.Name().equals("SEX <T-NAME>"))
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
			Ability A = CMClass.getAbility("Disease_Aids");
			if (A != null)
				A.invoke(msg.source(), myChar, true, 0);
		}
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a " + name().toLowerCase()
						+ " hide", RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " fingers",
						RawMaterial.RESOURCE_HIDE));
				resources.addElement(makeResource("a pound of "
						+ name().toLowerCase() + " flesh",
						RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource("a pile of "
						+ name().toLowerCase() + " bones",
						RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
