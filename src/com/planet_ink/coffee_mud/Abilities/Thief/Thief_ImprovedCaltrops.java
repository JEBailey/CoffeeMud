package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.HashSet;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Thief_ImprovedCaltrops extends Thief_Caltrops {
	public String ID() {
		return "Thief_ImprovedCaltrops";
	}

	public String name() {
		return "Improved Caltrops";
	}

	private static final String[] triggerStrings = { "IMPROVEDCALTROPS" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String caltropTypeName() {
		return "improved ";
	}

	public void spring(MOB mob) {
		if ((!invoker().mayIFight(mob))
				|| (invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
				|| ((CMLib.dice().rollPercentage() - getXLEVELLevel(invoker())) < mob
						.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(
					mob,
					affected,
					this,
					CMMsg.MSG_OK_ACTION,
					"<S-NAME> avoid(s) some " + caltropTypeName()
							+ "caltrops on the floor.");
		else {
			CMLib.combat().postDamage(
					invoker(),
					mob,
					null,
					CMLib.dice().roll(8, 6, 5 * adjustedLevel(invoker(), 0)),
					CMMsg.MASK_MALICIOUS | CMMsg.TYP_JUSTICE,
					Weapon.TYPE_PIERCING,
					"The " + caltropTypeName()
							+ "caltrops on the ground <DAMAGE> <T-NAME>.");
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}