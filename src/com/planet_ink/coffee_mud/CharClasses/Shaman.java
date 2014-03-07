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
public class Shaman extends Cleric {
	public String ID() {
		return "Shaman";
	}

	public String name() {
		return "Shaman";
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

	public Shaman() {
		super();
		maxStatAdj[CharStats.STAT_WISDOM] = 4;
		maxStatAdj[CharStats.STAT_CONSTITUTION] = 4;
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
				"Specialization_BluntWeapon", true);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "Prayer_Marry", false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 1, "Prayer_Annul", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Prayer_RestoreSmell", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_CureLight",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_CauseLight",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseEvil",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseGood",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseLife",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_Bury", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 3,
				"Prayer_MinorInfusion", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_FortifyFood",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtEvil",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtGood",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 5,
				"Prayer_CureDeafness", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_Deafness",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_CreateFood",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_CreateWater",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_EarthMud",
				false);

		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 7, "Prayer_Curse", false);
		CMLib.ableMapper()
				.addCharAbilityMapping(ID(), 7, "Prayer_Bless", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_Paralyze",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 8,
				"Prayer_ProtParalyzation", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Prayer_Earthshield",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 9,
				"Prayer_ModerateInfusion", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 10,
				"Prayer_RestoreVoice", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 10,
				"Prayer_SenseInvisible", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 11,
				"Prayer_RemovePoison", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_Poison",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_ProtPoison",
				true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 12,
				"Prayer_ProtDisease", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_Sober",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_Sanctuary",
				true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_Sanctum",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_Fertilize",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 14,
				"Prayer_Cleanliness", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_Rockskin",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 15,
				"Prayer_GuardianHearth", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_Tremor",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 16,
				"Prayer_InfuseBalance", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_AttackHalf",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17,
				"Prayer_MajorInfusion", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 17,
				"Prayer_CureBlindness", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindsight",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Prayer_Retribution", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Prayer_ProtectElements", true);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_RockFlesh",
				false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_FleshRock",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 20,
				"Prayer_MassMobility", true,
				CMParms.parseSemicolons("Prayer_ProtParalyzation", true));
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_SelfResurrection",false);
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_CommuneWithDead",false);
		// // (MudChat with corpses + Mood)
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_SpeakWithAnimal",false);
		// // (MudChat with animals + Mood)
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_SpiritHelper",false);
		// // (Always same spirit animal, gives power)
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_Awaken",false);
		// // (Cancels sleep spells and wakes the sleeping)
		// CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Prayer_SummonAnimalSpirits",false);
		// // (Summons animal spirits to fight)

		CMLib.ableMapper().addCharAbilityMapping(ID(), 21,
				"Prayer_DrunkenStupor", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21,
				"Prayer_MoralBalance", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_CurseItem",
				true, CMParms.parseSemicolons("Prayer_Curse", true));
		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_Disenchant",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 23,
				"Prayer_LinkedHealth", true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Skill_Meditation",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Prayer_Nullification", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Prayer_NeutralizeLand", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_SummonElemental", 0, "EARTH", false);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 25,
				"Prayer_AcidHealing", false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Prayer_HolyDay",
				true);
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY;
	}

	public boolean tick(Tickable myChar, int tickID) {
		if (tickID == Tickable.TICKID_MOB) {
		}
		return true;
	}

	public String[] getRequiredRaceList() {
		return super.getRequiredRaceList();
	}

	private final Pair<String, Integer>[] minimumStatRequirements = new Pair[] {
			new Pair<String, Integer>("Wisdom", Integer.valueOf(9)),
			new Pair<String, Integer>("Constitution", Integer.valueOf(9)) };

	public Pair<String, Integer>[] getMinimumStatRequirements() {
		return minimumStatRequirements;
	}

	public String getOtherBonusDesc() {
		return "Never fumbles neutral prayers, receives smallest prayer fumble chance, and receives 1pt/level of acid damage reduction.";
	}

	public String getOtherLimitsDesc() {
		return "Using non-neutral prayers introduces small failure chance.  Vulnerable to electric attacks.";
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(myHost instanceof MOB))
			return super.okMessage(myHost, msg);
		MOB myChar = (MOB) myHost;
		if (!super.okMessage(myChar, msg))
			return false;

		if ((msg.amITarget(myChar)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_ACID)) {
			int recovery = myChar.charStats().getClassLevel(this);
			msg.setValue(msg.value() - recovery);
		} else if ((msg.amITarget(myChar))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_ELECTRIC)) {
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