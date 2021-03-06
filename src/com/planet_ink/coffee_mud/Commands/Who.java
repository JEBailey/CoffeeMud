package com.planet_ink.coffee_mud.Commands;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Who extends StdCommand {
	public Who() {
	}

	private final String[] access = { "WHO", "WH" };

	public String[] getAccessWords() {
		return access;
	}

	public int[] getShortColWidths(MOB seer) {
		return new int[] {
				ListingLibrary.ColFixer.fixColWidth(12, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(12, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(7, seer.session()),
				ListingLibrary.ColFixer.fixColWidth(40, seer.session()) };
	}

	public String getHead(int[] colWidths) {
		StringBuilder head = new StringBuilder("");
		head.append("^x[");
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.RACES))
			head.append(CMStrings.padRight("Race", colWidths[0]) + " ");
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES))
			head.append(CMStrings.padRight("Class", colWidths[1]) + " ");
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
			head.append(CMStrings.padRight("Level", colWidths[2]));
		head.append("] Character name^.^N\n\r");
		return head.toString();
	}

	public StringBuffer showWhoShort(MOB who, int[] colWidths) {
		StringBuffer msg = new StringBuffer("");
		msg.append("[");
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.RACES)) {
			if (who.charStats().getCurrentClass().raceless())
				msg.append(CMStrings.padRight(" ", colWidths[0]) + " ");
			else
				msg.append(CMStrings.padRight(who.charStats().raceName(),
						colWidths[0]) + " ");
		}
		String levelStr = who.charStats().displayClassLevel(who, true).trim();
		int x = levelStr.lastIndexOf(' ');
		if (x >= 0)
			levelStr = levelStr.substring(x).trim();
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES)) {
			if (who.charStats().getMyRace().classless())
				msg.append(CMStrings.padRight(" ", colWidths[1]) + " ");
			else
				msg.append(CMStrings.padRight(who.charStats()
						.displayClassName(), colWidths[1])
						+ " ");
		}
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)) {
			if (who.charStats().getMyRace().leveless()
					|| who.charStats().getCurrentClass().leveless())
				msg.append(CMStrings.padRight(" ", colWidths[2]));
			else
				msg.append(CMStrings.padRight(levelStr, colWidths[2]));
		}
		String name = null;
		if (CMath.bset(who.phyStats().disposition(), PhyStats.IS_CLOAKED))
			name = "("
					+ (who.Name().equals(who.name()) ? who.titledName() : who
							.name()) + ")";
		else
			name = (who.Name().equals(who.name()) ? who.titledName() : who
					.name());
		if ((who.session() != null) && (who.session().isAfk())) {
			long t = (who.session().getIdleMillis() / 1000);
			String s = t + "s";
			if (t > 600) {
				t = t / 60;
				s = t + "m";
				if (t > 120) {
					t = t / 60;
					s = t + "h";
					if (t > 48) {
						t = t / 24;
						s = t + "d";
					}
				}
			}
			name = name + (" (idle: " + s + ")");
		}
		msg.append("] " + CMStrings.padRight(name, colWidths[3]));
		msg.append("\n\r");
		return msg;
	}

	public String getWho(MOB mob, Set<String> friends, String mobName) {
		StringBuffer msg = new StringBuffer("");
		int[] colWidths = getShortColWidths(mob);
		for (Session S : CMLib.sessions().localOnlineIterable()) {
			MOB mob2 = S.mob();
			if ((mob2 != null) && (mob2.soulMate() != null))
				mob2 = mob2.soulMate();

			if ((mob2 != null)
					&& ((((mob2.phyStats().disposition() & PhyStats.IS_CLOAKED) == 0) || ((CMSecurity
							.isAllowedAnywhere(mob, CMSecurity.SecFlag.CLOAK) || CMSecurity
							.isAllowedAnywhere(mob, CMSecurity.SecFlag.WIZINV)) && (mob
							.phyStats().level() >= mob2.phyStats().level()))))
					&& ((friends == null) || (friends.contains(mob2.Name()) || (friends
							.contains("All"))))
					&& (mob2.phyStats().level() > 0))
				msg.append(showWhoShort(mob2, colWidths));
		}
		if ((mobName != null) && (msg.length() == 0))
			return "";
		else {
			StringBuffer head = new StringBuffer(getHead(colWidths));
			head.append(msg.toString());
			return head.toString();
		}
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String mobName = CMParms.combine(commands, 1);
		if ((mobName != null) && (mob != null) && (mobName.startsWith("@"))) {
			if ((!(CMLib.intermud().i3online()))
					&& (!CMLib.intermud().imc2online()))
				mob.tell("Intermud is unavailable.");
			else
				CMLib.intermud().i3who(mob, mobName.substring(1));
			return false;
		}
		Set<String> friends = null;
		if ((mobName != null) && (mob != null)
				&& (mobName.equalsIgnoreCase("friends"))
				&& (mob.playerStats() != null)) {
			friends = mob.playerStats().getFriends();
			mobName = null;
		}

		if ((mobName != null)
				&& (mob != null)
				&& (mobName.equalsIgnoreCase("pk")
						|| mobName.equalsIgnoreCase("pkill") || mobName
							.equalsIgnoreCase("playerkill"))) {
			friends = new HashSet();
			for (Session S : CMLib.sessions().allIterable()) {
				MOB mob2 = S.mob();
				if ((mob2 != null)
						&& (CMath.bset(mob2.getBitmap(), MOB.ATT_PLAYERKILL)))
					friends.add(mob2.Name());
			}
		}

		String msg = getWho(mob, friends, mobName);
		if ((mobName != null) && (msg.length() == 0))
			mob.tell("That person doesn't appear to be online.\n\r");
		else
			mob.tell(msg);
		return false;
	}

	public Object executeInternal(MOB mob, int metaFlags, Object... args)
			throws java.io.IOException {
		return getWho(mob, null, null);
	}

	public boolean canBeOrdered() {
		return true;
	}
}
