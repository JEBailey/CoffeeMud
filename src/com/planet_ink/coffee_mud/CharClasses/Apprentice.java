package com.planet_ink.coffee_mud.CharClasses;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Apprentice extends StdCharClass {
	public String ID() {
		return "Apprentice";
	}

	public String name() {
		return "Apprentice";
	}

	public String baseClass() {
		return "Commoner";
	}

	public int getBonusPracLevel() {
		return 5;
	}

	public int getBonusAttackLevel() {
		return -1;
	}

	public int getAttackAttribute() {
		return CharStats.STAT_WISDOM;
	}

	public int getLevelsPerBonusDamage() {
		return 10;
	}

	public int getTrainsFirstLevel() {
		return 6;
	}

	public String getHitPointsFormula() {
		return "((@x6<@x7)/9)+(1*(1?4))";
	}

	public String getManaFormula() {
		return "((@x4<@x5)/10)+(1*(1?2))";
	}

	public int getLevelCap() {
		return 1;
	}

	public SubClassRule getSubClassRule() {
		return SubClassRule.ANY;
	}

	public int allowedArmorLevel() {
		return CharClass.ARMOR_CLOTH;
	}

	public int allowedWeaponLevel() {
		return CharClass.WEAPONS_DAGGERONLY;
	}

	private HashSet disallowedWeapons = buildDisallowedWeaponClasses();

	protected HashSet disallowedWeaponClasses(MOB mob) {
		return disallowedWeapons;
	}

	protected HashSet currentApprentices = new HashSet();

	public void initializeClass() {
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_Natural", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 25,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Climb", true);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "ClanCrafting", false);
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_HEROIC | Area.THEME_TECHNOLOGY;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((tickID == Tickable.TICKID_MOB) && (ticking instanceof MOB)
				&& (!((MOB) ticking).isMonster())) {
			if (((MOB) ticking).baseCharStats().getCurrentClass().ID()
					.equals(ID())) {
				if (!currentApprentices.contains(ticking))
					currentApprentices.add(ticking);
			} else if (currentApprentices.contains(ticking)) {
				currentApprentices.remove(ticking);
				((MOB) ticking)
						.tell("\n\r\n\r^ZYou are no longer an apprentice!!!!^N\n\r\n\r");
				CMLib.leveler().postExperience((MOB) ticking, null, null, 1000,
						false);
			}
		}
		return super.tick(ticking, tickID);
	}

	private final String[] raceRequiredList = new String[] { "All" };

	public String[] getRequiredRaceList() {
		return raceRequiredList;
	}

	private final Pair<String, Integer>[] minimumStatRequirements = new Pair[] {
			new Pair<String, Integer>("Wisdom", Integer.valueOf(5)),
			new Pair<String, Integer>("Intelligence", Integer.valueOf(5)) };

	public Pair<String, Integer>[] getMinimumStatRequirements() {
		return minimumStatRequirements;
	}

	public List<Item> outfit(MOB myChar) {
		if (outfitChoices == null) {
			outfitChoices = new Vector();
			Weapon w = CMClass.getWeapon("Dagger");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

	public String getOtherBonusDesc() {
		return "Gains lots of xp for training to a new class.";
	}
}
