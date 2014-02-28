package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Trap_Boomerang extends StdTrap {
	public String ID() {
		return "Trap_Boomerang";
	}

	public String name() {
		return "boomerang";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 24;
	}

	public String requiresToSet() {
		return "";
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		boolean wasSprung = sprung;
		super.executeMsg(myHost, msg);
		if ((!wasSprung) && (sprung)) {
			msg.setSourceCode(CMMsg.NO_EFFECT);
			msg.setTargetCode(CMMsg.NO_EFFECT);
			msg.setOthersCode(CMMsg.NO_EFFECT);
		}
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (target.location() != null)) {
			boolean ok = ((invoker() != null) && (invoker().location() != null));
			if ((!ok) || (doesSaveVsTraps(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> foil(s) a trap on " + affected.name() + "!");
			else if (target.location().show(target, target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> set(s) off a trap!")) {
				if (affected instanceof Item) {
					((Item) affected).unWear();
					((Item) affected).removeFromOwnerContainer();
					invoker().addItem((Item) affected);
					invoker().tell(invoker(), affected, null,
							"Magically, <T-NAME> appear(s) in your inventory.");
				}
				super.spring(target);
				if ((canBeUninvoked()) && (affected instanceof Item))
					disable();
			}
		}
	}
}
