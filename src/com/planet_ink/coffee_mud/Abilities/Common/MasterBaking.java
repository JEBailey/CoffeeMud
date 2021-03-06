package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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

public class MasterBaking extends Baking {
	private String cookingID = "";

	public String ID() {
		return "MasterBaking" + cookingID;
	}

	public String name() {
		return "Master Baking" + cookingID;
	}

	private static final String[] triggerStrings = { "MBAKE", "MBAKING",
			"MASTERBAKE", "MASTERBAKING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected List<String> noUninvokes = new ArrayList<String>(0);

	protected List<String> getUninvokeException() {
		return noUninvokes;
	}

	protected int getDuration(MOB mob, int level) {
		return getDuration(60, mob, 1, 8);
	}

	protected int baseYield() {
		return 2;
	}

	@SuppressWarnings("rawtypes")
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		try {
			cookingID = "";
			int num = 1;
			while (mob.fetchEffect("MasterBaking" + cookingID) != null)
				cookingID = Integer.toString(++num);
			List<String> noUninvokes = new Vector<String>(1);
			for (int i = 0; i < mob.numEffects(); i++) {
				Ability A = mob.fetchEffect(i);
				if (((A instanceof MasterBaking) || A.ID().equals("Baking"))
						&& (noUninvokes.size() < 5))
					noUninvokes.add(A.ID());
			}
			this.noUninvokes = noUninvokes;
			return super.invoke(mob, commands, givenTarget, auto, asLevel);
		} finally {
			cookingID = "";
		}
	}
}
