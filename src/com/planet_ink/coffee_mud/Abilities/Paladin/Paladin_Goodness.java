package com.planet_ink.coffee_mud.Abilities.Paladin;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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

@SuppressWarnings("rawtypes")
public class Paladin_Goodness extends PaladinSkill {
	public String ID() {
		return "Paladin_Goodness";
	}

	public String name() {
		return "Paladin`s Goodness";
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_HOLYPROTECTION;
	}

	protected boolean tickTock = false;

	public Paladin_Goodness() {
		super();
		paladinsGroup = new Vector();
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		tickTock = !tickTock;
		if (tickTock) {
			MOB mob = invoker;
			Room R = (mob != null) ? mob.location() : null;
			if (R != null)
				for (int m = 0; m < R.numInhabitants(); m++) {
					MOB target = R.fetchInhabitant(m);
					if ((target != null)
							&& (CMLib.flags().isEvil(target))
							&& ((paladinsGroup != null)
									&& (paladinsGroup.contains(target)) || ((target
									.getVictim() == invoker) && (target
									.rangeToTarget() == 0)))
							&& ((invoker == null)
									|| (invoker.fetchAbility(ID()) == null) || proficiencyCheck(
										null, 0, false))) {

						int harming = CMLib.dice().roll(
								1,
								(invoker != null) ? adjustedLevel(invoker, 0)
										: 15, 0);
						if (CMLib.flags().isEvil(target))
							CMLib.combat()
									.postDamage(
											invoker,
											target,
											this,
											harming,
											CMMsg.MASK_MALICIOUS
													| CMMsg.MASK_ALWAYS
													| CMMsg.TYP_UNDEAD,
											Weapon.TYPE_BURSTING,
											"^SThe aura of goodness around <S-NAME> <DAMAGES> <T-NAME>!^?");
					}
				}
		}
		return true;
	}
}
