package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.Vector;

import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class MasterCostuming extends Costuming {
	public String ID() {
		return "MasterCostuming";
	}

	public String name() {
		return "Master Costuming";
	}

	private static final String[] triggerStrings = { "MASTERCOSTUME",
			"MCOSTUME", "MCOSTUMING", "MASTERCOSTUMING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String parametersFile() {
		return "mastercostume.txt";
	}

	protected boolean masterCraftCheck(final Item I) {
		if (I.name().toUpperCase().startsWith("DESIGNER")
				|| (I.name().toUpperCase().indexOf(" DESIGNER ") > 0))
			return true;
		if (I.basePhyStats().level() < 31)
			return false;
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
					"Make what? Enter \"mcostume list\" for a list, \"mcostume scan\", \"mcostume refit\", \"mcostume learn <item>\", \"mcostume mend <item>\", or \"mcostume stop\" to cancel.");
			return false;
		}
		if (parsedVars.autoGenerate > 0)
			commands.insertElementAt(Integer.valueOf(parsedVars.autoGenerate),
					0);
		return super.invoke(mob, commands, givenTarget, auto, asLevel);
	}
}
