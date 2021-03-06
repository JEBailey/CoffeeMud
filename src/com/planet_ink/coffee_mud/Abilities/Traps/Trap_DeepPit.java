package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;

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
public class Trap_DeepPit extends Trap_RoomPit {
	public String ID() {
		return "Trap_DeepPit";
	}

	public String name() {
		return "deep pit";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 14;
	}

	public String requiresToSet() {
		return "";
	}

	public void finishSpringing(MOB target) {
		if ((!invoker().mayIFight(target)) || (target.phyStats().weight() < 5))
			target.location().show(target, null, CMMsg.MSG_OK_ACTION,
					"<S-NAME> float(s) gently into the pit!");
		else {
			target.location().show(target, null, CMMsg.MSG_OK_ACTION,
					"<S-NAME> hit(s) the pit floor with a THUMP!");
			int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 15, 1);
			int maxDamage = (int) Math.round(CMath.mul(target.baseState()
					.getHitPoints(), .95));
			if (damage >= maxDamage)
				damage = maxDamage;
			CMLib.combat().postDamage(
					invoker(),
					target,
					this,
					damage,
					CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS
							| CMMsg.TYP_JUSTICE, -1, null);
		}
		CMLib.commands().postLook(target, true);
	}
}
