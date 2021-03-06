package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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

@SuppressWarnings("rawtypes")
public class Distilling extends Cooking {
	public String ID() {
		return "Distilling";
	}

	public String name() {
		return "Distilling";
	}

	private static final String[] triggerStrings = { "DISTILLING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String cookWordShort() {
		return "distill";
	}

	public String cookWord() {
		return "distilling";
	}

	public boolean honorHerbs() {
		return false;
	}

	public String supportedResourceString() {
		return "MISC";
	}

	public String parametersFile() {
		return "liquors.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	public Distilling() {
		super();

		defaultFoodSound = "hotspring.wav";
		defaultDrinkSound = "hotspring.wav";
	}

	public boolean mayICraft(final Item I) {
		if (I == null)
			return false;
		if (!super.mayBeCrafted(I))
			return false;
		if (I instanceof Drink) {
			Drink D = (Drink) I;
			if (D.liquidType() != RawMaterial.RESOURCE_LIQUOR)
				return false;
			if (CMLib.flags().flaggedAffects(D, Ability.FLAG_INTOXICATING)
					.size() > 0)
				return true;
		}
		return false;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((!super.invoke(mob, commands, givenTarget, auto, asLevel))
				|| (buildingI == null))
			return false;
		Ability A2 = buildingI.fetchEffect(0);
		if ((A2 != null) && (buildingI instanceof Drink)) {
			((Drink) buildingI).setLiquidType(RawMaterial.RESOURCE_LIQUOR);
			buildingI.setMaterial(RawMaterial.RESOURCE_LIQUOR);
		}
		return true;
	}
}
