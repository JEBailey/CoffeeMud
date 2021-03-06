package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class MOBHelper extends StdBehavior {
	public String ID() {
		return "MOBHelper";
	}

	public String accountForYourself() {
		return "friend protecting";
	}

	public void executeMsg(Environmental affecting, CMMsg msg) {
		super.executeMsg(affecting, msg);
		if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
			return;
		MOB attacker = msg.source();
		MOB monster = (MOB) affecting;
		MOB victim = (MOB) msg.target();

		if ((attacker != monster) && (victim != monster)
				&& (attacker != victim) && (!monster.isInCombat())
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				&& (CMLib.flags().canBeSeenBy(attacker, monster))
				&& (CMLib.flags().canBeSeenBy(victim, monster))
				&& (victim.isMonster()))
			Aggressive.startFight(monster, attacker, true, false, null);
	}
}
