package com.planet_ink.coffee_mud.Behaviors;

import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class ObjectGuardian extends StdBehavior {
	public String ID() {
		return "ObjectGuardian";
	}

	protected boolean sentinal = false;

	public void setParms(String parameters) {
		super.setParms(parameters);
		List<String> parts = CMParms.parse(parameters.toUpperCase());
		sentinal = parts.contains("SENTINAL") || parts.contains("SENTINEL");
	}

	public String accountForYourself() {
		return "valuable object guarding";
	}

	public boolean okMessage(Environmental oking, CMMsg msg) {
		if (!super.okMessage(oking, msg))
			return false;
		MOB mob = msg.source();
		MOB monster = (MOB) oking;
		if (sentinal) {
			if (!canActAtAll(monster))
				return true;
			if (monster.amFollowing() != null)
				return true;
			if (monster.curState().getHitPoints() < ((int) Math.round(monster
					.maxState().getHitPoints() / 4.0)))
				return true;
		} else if (!canFreelyBehaveNormal(oking))
			return true;

		if ((mob != monster)
				&& (((msg.sourceMinor() == CMMsg.TYP_THROW) && (monster
						.location() == CMLib.map().roomLocation(msg.target()))) || (msg
						.sourceMinor() == CMMsg.TYP_DROP))) {
			CMMsg msgs = CMClass.getMsg(monster, mob, CMMsg.MSG_NOISYMOVEMENT,
					"<S-NAME> won't let <T-NAME> drop that.");
			if (monster.location().okMessage(monster, msgs)) {
				monster.location().send(monster, msgs);
				return false;
			}
		} else if ((mob != monster)
				&& ((msg.sourceMinor() == CMMsg.TYP_GET)
						|| (msg.targetMinor() == CMMsg.TYP_PUSH) || (msg
						.targetMinor() == CMMsg.TYP_PULL))) {
			CMMsg msgs = CMClass.getMsg(monster, mob, CMMsg.MSG_NOISYMOVEMENT,
					"<S-NAME> won't let <T-NAME> touch that.");
			if (monster.location().okMessage(monster, msgs)) {
				monster.location().send(monster, msgs);
				return false;
			}
		}
		return true;
	}
}
