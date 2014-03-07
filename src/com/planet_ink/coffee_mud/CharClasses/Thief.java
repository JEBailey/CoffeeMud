package com.planet_ink.coffee_mud.CharClasses;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper;
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
public class Thief extends StdCharClass {
	public String ID() {
		return "Thief";
	}

	public String name() {
		return "Thief";
	}

	public String baseClass() {
		return "Thief";
	}

	public int getBonusPracLevel() {
		return 1;
	}

	public int getBonusAttackLevel() {
		return 0;
	}

	public int getAttackAttribute() {
		return CharStats.STAT_DEXTERITY;
	}

	public int getLevelsPerBonusDamage() {
		return 5;
	}

	public String getMovementFormula() {
		return "10*((@x2<@x3)/18)";
	}

	public String getHitPointsFormula() {
		return "((@x6<@x7)/3)+(1*(1?10))";
	}

	public String getManaFormula() {
		return "((@x4<@x5)/6)+(1*(1?3))";
	}

	public int allowedArmorLevel() {
		return CharClass.ARMOR_LEATHER;
	}

	public int allowedWeaponLevel() {
		return CharClass.WEAPONS_THIEFLIKE;
	}

	private HashSet disallowedWeapons = buildDisallowedWeaponClasses();

	protected HashSet disallowedWeaponClasses(MOB mob) {
		return disallowedWeapons;
	}

	public Thief() {
		super();
		maxStatAdj[CharStats.STAT_DEXTERITY] = 7;
	}

	public void initializeClass() {
		super.initializeClass();
		if (!ID().equals(baseClass()))
			return;
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_Ranged", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_EdgedWeapon", 50, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_Sword", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Apothecary", false,
				"+WIS 12");
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "ThievesCant", 75,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Unbinding", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Climb", 50,
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Thief_Swipe", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Thief_Hide", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Thief_SneakAttack",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 3,
				"Thief_Countertracking", false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 3, "Skill_WandUse", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Thief_Sneak", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Thief_Autosneak",
				false, CMParms.parseSemicolons("*_Sneak", true));

		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Thief_DetectTraps",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Skill_Dirt", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Thief_Pick", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Skill_Dodge", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Thief_Peek", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Thief_UsePoison",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Thief_RemoveTraps",
				false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 8, "Skill_Disarm", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Thief_Observation",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Skill_Parry", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Thief_BackStab",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Skill_Haggle",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Thief_Steal", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Skill_Trip", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Thief_Listen",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 12,
				"Skill_TwoWeaponFighting", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Thief_Graffiti",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Thief_Detection",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Thief_Bind", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Thief_Arsonry",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Thief_Surrender",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Fighter_RapidShot",
				false);

		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 15, "Thief_Snatch", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Spell_ReadMagic",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Thief_ConcealItem",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Thief_SilentGold",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 16,
				"Spell_DetectInvisible", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Thief_Hideout",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Thief_Shadow",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_Attack2",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Thief_CarefulStep",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Thief_SilentLoot",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Thief_Comprehension", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Thief_SetDecoys",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Thief_Distract",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Thief_Snatch",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 19,
				"Spell_Ventriloquate", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Thief_Lore", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Thief_Alertness",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Skill_AttackHalf",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Thief_Sap", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Thief_Panhandling",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Thief_ConcealDoor",
				false);

		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 22, "Thief_Flank", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 22,
				"Thief_ImprovedDistraction", false,
				CMParms.parseSemicolons("Thief_Distract", true));

		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Thief_Trap", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Skill_Warrants",
				true);

		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 24, "Thief_Bribe", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Thief_EscapeBonds",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Thief_ConcealWalkway", false);

		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 25, "Thief_Ambush", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Thief_Squatting",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 30,
				"Thief_Nondetection", true);
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY;
	}

	private final String[] raceRequiredList = new String[] { "All" };

	public String[] getRequiredRaceList() {
		return raceRequiredList;
	}

	private final Pair<String, Integer>[] minimumStatRequirements = new Pair[] { new Pair<String, Integer>(
			"Dexterity", Integer.valueOf(9)) };

	public Pair<String, Integer>[] getMinimumStatRequirements() {
		return minimumStatRequirements;
	}

	public List<Item> outfit(MOB myChar) {
		if (outfitChoices == null) {
			outfitChoices = new Vector();
			Weapon w = CMClass.getWeapon("Shortsword");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

	public void grantAbilities(MOB mob, boolean isBorrowedClass) {
		super.grantAbilities(mob, isBorrowedClass);
		if (mob.playerStats() == null) {
			List<AbilityMapper.AbilityMapping> V = CMLib.ableMapper()
					.getUpToLevelListings(ID(),
							mob.charStats().getClassLevel(ID()), false, false);
			for (AbilityMapper.AbilityMapping able : V) {
				Ability A = CMClass.getAbility(able.abilityID);
				if ((A != null)
						&& (!CMLib.ableMapper().getAllQualified(ID(), true,
								A.ID()))
						&& (!CMLib.ableMapper().getDefaultGain(ID(), true,
								A.ID())))
					giveMobAbility(mob, A, CMLib.ableMapper()
							.getDefaultProficiency(ID(), true, A.ID()), CMLib
							.ableMapper().getDefaultParm(ID(), true, A.ID()),
							isBorrowedClass);
			}
		}
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (myHost instanceof MOB) {
			MOB myChar = (MOB) myHost;
			if (msg.amISource(myChar)
					&& (!myChar.isMonster())
					&& (msg.sourceCode() == CMMsg.MSG_THIEF_ACT)
					&& (msg.target() instanceof MOB)
					&& (msg.targetMessage() == null)
					&& (msg.tool() instanceof Ability)
					&& (msg.tool().ID().equals("Thief_Steal")
							|| msg.tool().ID().equals("Thief_Robbery")
							|| msg.tool().ID().equals("Thief_Embezzle")
							|| msg.tool().ID().equals("Thief_Mug")
							|| (msg.tool().ID().equals("Thief_Pick") && (msg
									.value() == 1))
							|| (msg.tool().ID().equals("Thief_RemoveTraps") && (msg
									.value() == 1))
							|| msg.tool().ID().equals("Thief_Racketeer") || msg
							.tool().ID().equals("Thief_Swipe"))) {
				int xp = CMLib.flags().aliveAwakeMobileUnbound(
						(MOB) msg.target(), true) ? 10 : 5;
				CMLib.leveler().postExperience(myChar, (MOB) msg.target(),
						" for a successful " + msg.tool().name(), xp, false);
			}
		}
		super.executeMsg(myHost, msg);
	}

	public String getOtherBonusDesc() {
		return "Bonus experience for using certain skills.";
	}
}