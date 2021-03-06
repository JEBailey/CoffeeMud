package com.planet_ink.coffee_mud.CharClasses;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;

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
public class Diviner extends SpecialistMage {
	public String ID() {
		return "Diviner";
	}

	public String name() {
		return "Diviner";
	}

	public int domain() {
		return Ability.DOMAIN_DIVINATION;
	}

	public int opposed() {
		return Ability.DOMAIN_ILLUSION;
	}

	public int availabilityCode() {
		return Area.THEME_FANTASY;
	}

	public void initializeClass() {
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Skill_Spellcraft",
				false);

		CMLib.ableMapper().addCharAbilityMapping(ID(), 1,
				"Spell_AnalyzeDweomer", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Spell_SolveMaze",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Spell_GroupStatus",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 5,
				"Spell_DetectWeaknesses", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Spell_PryingEye",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Spell_Telepathy",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 8,
				"Spell_NaturalCommunion", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Spell_DetectTraps",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Spell_ArmsLength",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Spell_SpyingStone",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 13,
				"Spell_DetectScrying", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 14,
				"Spell_HearThoughts", 25, "", true, false,
				CMParms.parseSemicolons("Spell_Telepathy", true), "");
		CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Spell_KnowOrigin",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Spell_KnowFate",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Spell_DiviningEye",
				25, "", true, false,
				CMParms.parseSemicolons("Spell_PryingEye", true), "");
		CMLib.ableMapper().addCharAbilityMapping(ID(), 18,
				"Spell_SpottersOrders", 25, "", true, false,
				CMParms.parseSemicolons("Spell_DetectWeaknesses", true), "");
		CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Spell_Breadcrumbs",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 20,
				"Spell_FindDirections", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Spell_KnowPain",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Spell_KnowBliss",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 23,
				"Spell_DeathWarning", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 24,
				"Spell_DetectAmbush", 25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Spell_TrueSight",
				25, true);
		CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Spell_FutureDeath",
				25, true);
	}
}
