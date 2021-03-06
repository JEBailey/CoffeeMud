package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

/* 
 Copyright 2004 Tim Kassebaum

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
public class MasterArmorsmithing extends Armorsmithing implements ItemCraftor {
	public String ID() {
		return "MasterArmorsmithing";
	}

	public String name() {
		return "Master Armorsmithing";
	}

	private static final String[] triggerStrings = { "MARMORSMITH",
			"MASTERARMORSMITHING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String parametersFile() {
		return "masterarmorsmith.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	protected boolean masterCraftCheck(final Item I) {
		if (I.basePhyStats().level() < 30) {
			Ability A;
			for (int i = 0; i < I.numEffects(); i++) {
				A = I.fetchEffect(i);
				if (A instanceof TriggeredAffect) {
					final long flags = A.flags();
					final int triggers = ((TriggeredAffect) A).triggerMask();
					if (CMath.bset(flags, Ability.FLAG_ADJUSTER)
							&& CMath.bset(triggers,
									TriggeredAffect.TRIGGER_WEAR_WIELD))
						return false;
				}
			}
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;

		CraftParms parsedVars = super.parseAutoGenerate(auto, givenTarget,
				commands);

		randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands,
				parsedVars.autoGenerate);
		if (commands.size() == 0) {
			commonTell(
					mob,
					"Make what? Enter \"marmorsmith list\" for a list,\"marmorsmith scan\", \"marmorsmith learn <item>\", \"marmorsmith mend <item>\", or \"marmorsmith stop\" to cancel.");
			return false;
		}
		if (parsedVars.autoGenerate > 0)
			commands.insertElementAt(Integer.valueOf(parsedVars.autoGenerate),
					0);
		return super.invoke(mob, commands, givenTarget, auto, asLevel);
	}
}
