package com.planet_ink.coffee_mud.CharClasses;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.collections.Pair;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Missionary extends Cleric {
	public String ID() {
		return "Missionary";
	}

	public String name() {
		return "Missionary";
	}

	public String baseClass() {
		return "Cleric";
	}

	public int getAttackAttribute() {
		return CharStats.STAT_WISDOM;
	}

	public int allowedWeaponLevel() {
		return CharClass.WEAPONS_NEUTRALCLERIC;
	}

	private HashSet disallowedWeapons = buildDisallowedWeaponClasses();

	protected HashSet disallowedWeaponClasses(MOB mob) {
		return disallowedWeapons;
	}

	public Missionary() {
		super();
		maxStatAdj[CharStats.STAT_WISDOM] = 4;
		maxStatAdj[CharStats.STAT_DEXTERITY] = 4;
	}

	public void initializeClass() {
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 100,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "Prayer_Marry", false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "Prayer_Annul", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Revoke", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_WandUse",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Convert", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_Ranged", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Prayer_RestoreSmell", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_DivineLuck",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseEvil",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseGood",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseLife",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_Bury", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 3,
				"Prayer_InfuseBalance", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtUndead",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_Position",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_CreateFood",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_BirdsEye",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_CreateWater",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_SenseTraps",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 7,
				"Prayer_ElectricStrike", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 8,
				"Prayer_ProtParalyzation", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_Revival",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Prayer_AiryForm",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 9,
				"Prayer_MinorInfusion", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_SenseMagic",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 10,
				"Prayer_SenseInvisible", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 11,
				"Prayer_SenseHidden", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_ProtPoison",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 12,
				"Prayer_ProtDisease", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_Sanctuary",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_BloodMoon",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_HolyWind",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_Wings",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 16,
				"Prayer_Etherealness", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_AttackHalf",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindsight",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Prayer_Retribution", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Prayer_ProtectElements", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 19,
				"Prayer_ChainStrike", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 20,
				"Prayer_MassMobility", true,
				CMParms.parseSemicolons("Prayer_ProtParalyzation", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Prayer_Monolith",
				0, "AIR", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Prayer_Gateway",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21,
				"Prayer_MoralBalance", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_Disenchant",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 22,
				"Prayer_ModerateInfusion", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 23,
				"Prayer_LinkedHealth", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_Weather",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Prayer_Nullification", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Prayer_UndeniableFaith", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_SummonElemental", 0, "AIR", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_ElectricHealing", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Prayer_Sermon",
				true);
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		for (int i : CharStats.CODES.SAVING_THROWS())
			affectableStats.setStat(i, affectableStats.getStat(i)
					+ (affectableStats.getClassLevel(this)));
	}

	public String[] getRequiredRaceList() {
		return super.getRequiredRaceList();
	}

	private final Pair<String, Integer>[] minimumStatRequirements = new Pair[] {
			new Pair<String, Integer>("Wisdom", Integer.valueOf(9)),
			new Pair<String, Integer>("Dexterity", Integer.valueOf(9)) };

	public Pair<String, Integer>[] getMinimumStatRequirements() {
		return minimumStatRequirements;
	}

	public String getOtherBonusDesc() {
		return "Never fumbles neutral prayers, and receives 1pt/level luck bonus to all saving throws per level.  Receives 1pt/level electricity damage reduction.";
	}

	public String getOtherLimitsDesc() {
		return "Using non-neutral prayers introduces failure chance.  Vulnerable to acid attacks.";
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(myHost instanceof MOB))
			return super.okMessage(myHost, msg);
		MOB myChar = (MOB) myHost;
		if (!super.okMessage(myChar, msg))
			return false;

		if ((msg.amITarget(myChar)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_ELECTRIC)) {
			int recovery = myChar.charStats().getClassLevel(this);
			msg.setValue(msg.value() - recovery);
		} else if ((msg.amITarget(myChar))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_ACID)) {
			int recovery = msg.value();
			msg.setValue(msg.value() + recovery);
		}
		return true;
	}

	public List<Item> outfit(MOB myChar) {
		if (outfitChoices == null) {
			outfitChoices = new Vector();
			Weapon w = CMClass.getWeapon("SmallMace");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

}
