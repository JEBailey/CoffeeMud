package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
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
public class Trap_AcidSpray extends StdTrap {
	public String ID() {
		return "Trap_AcidSpray";
	}

	public String name() {
		return "acid spray";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS | Ability.CAN_EXITS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 15;
	}

	public String requiresToSet() {
		return "";
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (target.location() != null)) {
			if ((!invoker().mayIFight(target))
					|| (isLocalExempt(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)) || (target == invoker())
					|| (doesSaveVsTraps(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) setting off a acid trap!");
			else if (target.location().show(target, target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> set(s) off an acid spraying trap!")) {
				super.spring(target);
				CMLib.combat().postDamage(invoker(), target, null,
						CMLib.dice().roll(trapLevel() + abilityCode(), 6, 1),
						CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID,
						Weapon.TYPE_MELTING, "The acid <DAMAGE> <T-NAME>!");
				if ((canBeUninvoked()) && (affected instanceof Item))
					disable();
			}
		}
	}
}
