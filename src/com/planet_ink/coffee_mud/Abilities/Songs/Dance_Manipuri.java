package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
public class Dance_Manipuri extends Dance {
	public String ID() {
		return "Dance_Manipuri";
	}

	public String name() {
		return "Manipuri";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected String danceOf() {
		return name() + " Dance";
	}

	protected Room lastRoom = null;
	protected int count = 3;

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (!(affected instanceof MOB))
			return true;
		MOB mob = (MOB) affected;
		if (mob.location() != lastRoom) {
			count = 3 + getXLEVELLevel(invoker());
			lastRoom = mob.location();
		} else
			count--;
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		MOB mob = (MOB) affected;
		if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
				&& (mob.location() != null)
				&& ((msg.amITarget(mob)))
				&& ((count > 0) || (lastRoom == null) || (lastRoom != mob
						.location()))) {
			MOB target = (MOB) msg.target();
			if ((!target.isInCombat()) && (msg.source().getVictim() != target)) {
				msg.source().tell(
						"You feel like letting " + target.name(msg.source())
								+ " be for awhile.");
				if (target.getVictim() == msg.source()) {
					target.makePeace();
					target.setVictim(null);
				}
				return false;
			}

		}
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		count = 3;
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		count = 3;
		return true;
	}

}
