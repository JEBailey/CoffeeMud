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
public class Doomsayer extends Cleric {
	public String ID() {
		return "Doomsayer";
	}

	public String name() {
		return "Doomsayer";
	}

	public String baseClass() {
		return "Cleric";
	}

	public int getAttackAttribute() {
		return CharStats.STAT_WISDOM;
	}

	public int allowedWeaponLevel() {
		return CharClass.WEAPONS_EVILCLERIC;
	}

	private HashSet disallowedWeapons = buildDisallowedWeaponClasses();

	protected HashSet disallowedWeaponClasses(MOB mob) {
		return disallowedWeapons;
	}

	protected int alwaysFlunksThisQuality() {
		return 1000;
	}

	public Doomsayer() {
		super();
		maxStatAdj[CharStats.STAT_STRENGTH] = 4;
		maxStatAdj[CharStats.STAT_WISDOM] = 4;
	}

	public void initializeClass() {
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 100,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Revoke", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_WandUse",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Convert", 50,
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Specialization_Polearm", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_Ember", true);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "Prayer_Annul", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_Divorce",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_CurseFlames",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_Rot", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_Desecrate",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 3,
				"Prayer_DarkeningAura", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtFire",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtGood",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_Deafness",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_Faithless",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_FlameWeapon",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 6,
				"Prayer_CauseFatigue", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_Curse", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_Cannibalism",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_Paralyze",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 8,
				"Prayer_ProtParalyzation", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Prayer_CurseMetal",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Fighter_Intimidate",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_CurseMind",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_SenseMagic",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_Poison",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_ProtPoison",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_Plague",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 12,
				"Prayer_ProtDisease", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_BloodMoon",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_Fortress",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 14,
				"Prayer_Demonshield", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_AuraHarm",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_GreatCurse",
				true, CMParms.parseSemicolons("Prayer_Curse", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 15,
				"Prayer_MassDeafness", false,
				CMParms.parseSemicolons("Prayer_Deafness", true));

		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_Anger",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_DailyBread",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindness",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_AttackHalf",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17,
				"Prayer_InfuseUnholiness", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_DoomAura",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Prayer_ProtectElements", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_Hellfire",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_CurseLuck",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 20,
				"Prayer_MassParalyze", false,
				CMParms.parseSemicolons("Prayer_Curse", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 20,
				"Prayer_MassBlindness", false,
				CMParms.parseSemicolons("Prayer_Blindness", true));

		CMLib.ableMapper().addCharAbilityMapping(ID(), 21,
				"Prayer_DemonicConsumption", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21,
				"Prayer_Condemnation", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_CurseItem",
				true, CMParms.parseSemicolons("Prayer_Curse", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_Disenchant",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_CurseMinds",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_Doomspout",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Prayer_UnholyWord",
				true, CMParms.parseSemicolons("Prayer_GreatCurse", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Prayer_Nullification", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_SummonElemental", 0, "FIRE", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_Regeneration", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 30,
				"Prayer_FireHealing", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Prayer_Stoning", 0,
				"", false, true);
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY;
	}

	public String[] getRequiredRaceList() {
		return super.getRequiredRaceList();
	}

	private final Pair<String, Integer>[] minimumStatRequirements = new Pair[] {
			new Pair<String, Integer>("Strength", Integer.valueOf(9)),
			new Pair<String, Integer>("Wisdom", Integer.valueOf(9)) };

	public Pair<String, Integer>[] getMinimumStatRequirements() {
		return minimumStatRequirements;
	}

	public String getOtherBonusDesc() {
		return "Receives 1 pt damage reduction/level from fire attacks.";
	}

	public String getOtherLimitsDesc() {
		return "Always fumbles good prayers, and fumbles all prayers when alignment is above 500.  Qualifies and receives evil prayers.  Using non-aligned prayers introduces failure chance.  Vulnerable to cold attacks.";
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(myHost instanceof MOB))
			return super.okMessage(myHost, msg);
		MOB myChar = (MOB) myHost;
		if (!super.okMessage(myChar, msg))
			return false;

		if ((msg.amITarget(myChar)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_FIRE)) {
			int recovery = myChar.charStats().getClassLevel(this);
			msg.setValue(msg.value() - recovery);
		} else if ((msg.amITarget(myChar))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_COLD)) {
			int recovery = msg.value();
			msg.setValue(msg.value() + recovery);
		}
		return true;
	}

	public List<Item> outfit(MOB myChar) {
		if (outfitChoices == null) {
			outfitChoices = new Vector();
			Weapon w = CMClass.getWeapon("Shortsword");
			outfitChoices.add(w);
		}
		return outfitChoices;
	}

}
