package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Trap_Get extends Trap_Trap {
	public String ID() {
		return "Trap_Get";
	}

	public String name() {
		return "Get Trap";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (sprung) {
			if (msg.source().isMine(affected))
				unInvoke();
			else
				super.executeMsg(myHost, msg);
			return;
		}

		super.executeMsg(myHost, msg);

		if (msg.amITarget(affected)) {
			if ((msg.targetMinor() == CMMsg.TYP_GET)
					|| (msg.targetMinor() == CMMsg.TYP_PUSH)
					|| (msg.targetMinor() == CMMsg.TYP_PULL)) {
				spring(msg.source());
			}
		}
	}
}
