package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;

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
public class Pose extends StdCommand {
	public Pose() {
	}

	private final String[] access = { "POSE", "NOPOSE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if ((commands.size() > 0)
				&& (commands.firstElement().toString()
						.equalsIgnoreCase("NOPOSE"))) {
			PlayerStats pstats = mob.playerStats();
			if (pstats != null) {
				if ((pstats.getSavedPose() == null)
						|| (pstats.getSavedPose().length() == 0)) {
					mob.tell("You are not currently posing.");
					return false;
				}
				pstats.setSavedPose("");
				mob.setDisplayText("");
				mob.tell("You stop posing.");
			}
			return false;
		}
		if (commands.size() < 2) {
			if (mob.displayText().length() == 0)
				mob.tell("POSE how?");
			else
				mob.tell("Your current pose is: " + mob.displayText(mob));
			return false;
		}
		String combinedCommands = CMParms.combine(commands, 1);
		combinedCommands = CMProps.applyINIFilter(combinedCommands,
				CMProps.Str.POSEFILTER);
		if (combinedCommands.trim().startsWith("'")
				|| combinedCommands.trim().startsWith("`"))
			combinedCommands = combinedCommands.trim();
		else
			combinedCommands = " " + combinedCommands.trim();
		String emote = "^E<S-NAME>" + combinedCommands + " ^?";
		CMMsg msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_EMOTE
				| CMMsg.MASK_ALWAYS, "^E" + mob.name() + combinedCommands
				+ " ^?", emote, emote);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			mob.setDisplayText(mob.Name() + combinedCommands);
			PlayerStats pstats = mob.playerStats();
			if (pstats != null)
				pstats.setSavedPose(mob.Name() + combinedCommands);
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
