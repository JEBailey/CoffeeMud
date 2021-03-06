package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
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
@SuppressWarnings("rawtypes")
public class NoFollow extends Follow {
	public NoFollow() {
	}

	private final String[] access = { "NOFOLLOW", "NOFOL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if ((commands.size() > 1) && (commands.elementAt(0) instanceof String)) {
			if (((String) commands.elementAt(0)).equalsIgnoreCase("UNFOLLOW")) {
				unfollow(
						mob,
						((commands.size() > 1)
								&& (commands.elementAt(1) instanceof String) && (((String) commands
								.elementAt(1)).equalsIgnoreCase("QUIETLY"))));
				return false;
			}
			MOB M = mob.fetchFollower(CMParms.combine(commands, 1));
			if ((M == null) && (mob.location() != null)) {
				M = mob.location()
						.fetchInhabitant(CMParms.combine(commands, 1));
				if (M != null)
					mob.tell(M.name(mob) + " is not following you!");
				else
					mob.tell("There is noone here called '"
							+ CMParms.combine(commands, 1) + "' following you!");
				return false;
			}
			if ((mob.location() != null) && (M != null)
					&& (M.amFollowing() == mob)) {
				nofollow(M, true, false);
				return true;
			}
			mob.tell("There is noone called '" + CMParms.combine(commands, 1)
					+ "' following you!");
			return false;
		}
		if (!CMath.bset(mob.getBitmap(), MOB.ATT_NOFOLLOW)) {
			mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_NOFOLLOW));
			// unfollow(mob,false);
			mob.tell("You are no longer accepting new followers.");
		} else {
			mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_NOFOLLOW));
			mob.tell("You are now accepting new followers.");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
