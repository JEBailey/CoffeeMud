package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
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
@SuppressWarnings("rawtypes")
public class Eat extends StdCommand {
	public Eat() {
	}

	private final String[] access = { "EAT" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Eat what?");
			return false;
		}
		commands.removeElementAt(0);

		Environmental thisThang = null;
		thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
				CMParms.combine(commands, 0), Wearable.FILTER_ANY);
		if ((thisThang == null) || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
			mob.tell("You don't see '" + CMParms.combine(commands, 0)
					+ "' here.");
			return false;
		}
		boolean hasHands = mob.charStats().getBodyPart(Race.BODY_HAND) > 0;
		if ((thisThang instanceof Food) && (!mob.isMine(thisThang))
				&& (hasHands)) {
			mob.tell("You don't seem to have '" + CMParms.combine(commands, 0)
					+ "'.");
			return false;
		}
		String eatSound = CMLib.protocol().msp("gulp.wav", 10);
		String eatMsg = "<S-NAME> eat(s) <T-NAMESELF>." + eatSound;
		CMMsg newMsg = CMClass.getMsg(mob, thisThang, null,
				hasHands ? CMMsg.MSG_EAT : CMMsg.MSG_EAT_GROUND, eatMsg);
		if (mob.location().okMessage(mob, newMsg)) {
			if ((thisThang instanceof Food)
					&& (newMsg.value() > 0)
					&& (newMsg.value() < ((Food) thisThang).nourishment())
					&& (newMsg.othersMessage() != null)
					&& (newMsg.othersMessage().startsWith(eatMsg))
					&& (newMsg.sourceMessage().equalsIgnoreCase(newMsg
							.othersMessage()))
					&& (newMsg.targetMessage().equalsIgnoreCase(newMsg
							.othersMessage()))) {
				String biteMsg = "<S-NAME> take(s) a bite of <T-NAMESELF>."
						+ eatSound;
				newMsg.setSourceMessage(biteMsg);
				newMsg.setTargetMessage(biteMsg);
				newMsg.setOthersMessage(biteMsg);
			}
			mob.location().send(mob, newMsg);
		}
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return true;
	}

}
