package com.planet_ink.coffee_mud.CharClasses;

import java.util.Enumeration;
import java.util.List;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;

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
public class Qualifier extends StdCharClass {
	public String ID() {
		return "Qualifier";
	}

	public String name() {
		return "Qualifier";
	}

	public String baseClass() {
		return ID();
	}

	private static boolean abilitiesLoaded = false;

	public boolean loaded() {
		return abilitiesLoaded;
	}

	public void setLoaded(boolean truefalse) {
		abilitiesLoaded = truefalse;
	}

	public Qualifier() {
		super();
		for (int i : CharStats.CODES.BASE())
			maxStatAdj[i] = 7;
	}

	public int availabilityCode() {
		return 0;
	}

	public String getStatQualDesc() {
		return "Must be granted by an Archon.";
	}

	public boolean qualifiesForThisClass(MOB mob, boolean quiet) {
		if (!quiet)
			mob.tell("This class cannot be learned.");
		return false;
	}

	public void startCharacter(MOB mob, boolean isBorrowedClass,
			boolean verifyOnly) {
		if (!loaded()) {
			setLoaded(true);
			for (Enumeration<Ability> a = CMClass.abilities(); a
					.hasMoreElements();) {
				Ability A = a.nextElement();
				int lvl = CMLib.ableMapper().lowestQualifyingLevel(A.ID());
				if ((lvl > 0)
						&& (!CMLib.ableMapper().classOnly("Archon", A.ID())))
					CMLib.ableMapper().addCharAbilityMapping(ID(), lvl, A.ID(),
							false);
			}
		}
		super.startCharacter(mob, false, verifyOnly);
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

}
