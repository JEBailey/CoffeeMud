package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Prop_NoPKill extends Property {
	public String ID() {
		return "Prop_NoPKill";
	}

	public String name() {
		return "No Player Killing";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS | Ability.CAN_AREAS;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (((CMath.bset(msg.sourceMajor(), CMMsg.MASK_MALICIOUS))
				|| (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS)) || (CMath
					.bset(msg.othersMajor(), CMMsg.MASK_MALICIOUS)))
				&& (msg.target() instanceof MOB)
				&& (msg.target() != msg.source())
				&& (!((MOB) msg.target()).isMonster())
				&& (!msg.source().isMonster())) {
			if (CMath.s_int(text()) == 0) {
				msg.source().tell("Player killing is forbidden here.");
				msg.source().setVictim(null);
				return false;
			}
			int levelDiff = msg.source().phyStats().level()
					- ((MOB) msg.target()).phyStats().level();
			if (levelDiff < 0)
				levelDiff = levelDiff * -1;
			if (levelDiff > CMath.s_int(text())) {
				msg.source()
						.tell("Player killing is forbidden for characters whose level difference is greater than "
								+ CMath.s_int(text()) + ".");
				msg.source().setVictim(null);
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}
}
