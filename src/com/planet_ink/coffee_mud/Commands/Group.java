package com.planet_ink.coffee_mud.Commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;

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
public class Group extends StdCommand {
	public Group() {
	}

	private final String[] access = { "GROUP", "GR" };

	public String[] getAccessWords() {
		return access;
	}

	public static StringBuffer showWhoLong(MOB seer, MOB who) {

		StringBuffer msg = new StringBuffer("");
		msg.append("[");
		int[] cols = { ListingLibrary.ColFixer.fixColWidth(7, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(7, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(5, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(13, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(3, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(12, seer.session()) };
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.RACES)) {
			if (who.charStats().getCurrentClass().raceless())
				msg.append(CMStrings.padRight(" ", cols[0]) + " ");
			else
				msg.append(CMStrings.padRight(who.charStats().raceName(),
						cols[0]) + " ");
		}

		String levelStr = who.charStats().displayClassLevel(who, true).trim();
		int x = levelStr.lastIndexOf(' ');
		if (x >= 0)
			levelStr = levelStr.substring(x).trim();
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES)) {
			if (who.charStats().getMyRace().classless())
				msg.append(CMStrings.padRight(" ", cols[1]) + " ");
			else
				msg.append(CMStrings.padRight(who.charStats()
						.displayClassName(), cols[1])
						+ " ");
		}
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)) {
			if (who.charStats().getCurrentClass().leveless()
					|| who.charStats().getMyRace().leveless())
				msg.append(CMStrings.padRight(" ", cols[2]));
			else
				msg.append(CMStrings.padRight(levelStr, cols[2]));
		}
		msg.append("] " + CMStrings.padRight(who.name(), cols[3]) + " ");
		msg.append(CMStrings.padRightPreserve(
				"hp("
						+ CMStrings.padRightPreserve(""
								+ who.curState().getHitPoints(), cols[4])
						+ "/"
						+ CMStrings.padRightPreserve(""
								+ who.maxState().getHitPoints(), cols[4]) + ")",
				cols[5]));
		msg.append(CMStrings.padRightPreserve(
				"mn("
						+ CMStrings.padRightPreserve(""
								+ who.curState().getMana(), cols[4])
						+ "/"
						+ CMStrings.padRightPreserve(""
								+ who.maxState().getMana(), cols[4]) + ")",
				cols[5]));
		msg.append(CMStrings.padRightPreserve(
				"mv("
						+ CMStrings.padRightPreserve(""
								+ who.curState().getMovement(), cols[4])
						+ "/"
						+ CMStrings.padRightPreserve(""
								+ who.maxState().getMovement(), cols[4]) + ")",
				cols[5]));
		msg.append("\n\r");
		return msg;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		mob.tell(mob.name() + "'s group:\n\r");
		Set<MOB> group = mob.getGroupMembers(new HashSet<MOB>());
		StringBuffer msg = new StringBuffer("");
		for (Iterator e = group.iterator(); e.hasNext();) {
			MOB follower = (MOB) e.next();
			msg.append(showWhoLong(mob, follower));
		}
		mob.tell(msg.toString());
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
