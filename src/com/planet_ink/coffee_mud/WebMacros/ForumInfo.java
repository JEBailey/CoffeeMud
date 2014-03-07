package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary.ForumJournalFlags;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.miniweb.interfaces.HTTPRequest;
import com.planet_ink.miniweb.util.MWThread;

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
public class ForumInfo extends StdWebMacro {
	public String name() {
		return "ForumInfo";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("JOURNAL");
		if (last == null)
			return " @break@";
		boolean securityOverride = false;
		if ((Thread.currentThread() instanceof MWThread)
				&& CMath.s_bool(((MWThread) Thread.currentThread()).getConfig()
						.getMiscProp("ADMIN"))
				&& parms.containsKey("ALLFORUMJOURNALS"))
			securityOverride = true;

		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if ((!securityOverride) && (CMLib.journals().isArchonJournalName(last))
				&& ((M == null) || (!CMSecurity.isASysOp(M))))
			return " @break@";
		JournalsLibrary.ForumJournal journal = CMLib.journals()
				.getForumJournal(last);
		if (journal == null)
			return " @break@";

		StringBuffer str = new StringBuffer("");
		if (parms.containsKey("ISSMTPFORWARD")) {
			@SuppressWarnings("unchecked")
			TreeMap<String, JournalsLibrary.SMTPJournal> set = (TreeMap<String, JournalsLibrary.SMTPJournal>) Resources
					.getResource("SYSTEM_SMTP_JOURNALS");
			final JournalsLibrary.SMTPJournal entry = (set != null) ? set
					.get(last.toUpperCase().trim()) : null;
			final String email = ((M != null) && (M.playerStats() != null) && (M
					.playerStats().getEmail() != null)) ? M.playerStats()
					.getEmail() : "";
			str.append(
					((entry != null) && (email.length() > 0)) ? Boolean
							.toString(entry.forward) : "false").append(", ");
		}

		if (parms.containsKey("ISSMTPSUBSCRIBER")) {
			final Map<String, List<String>> lists = Resources
					.getCachedMultiLists("mailinglists.txt", true);
			final List<String> mylist = lists.get(last);
			str.append(
					((mylist != null) && (M != null)) ? Boolean.toString(mylist
							.contains(M.Name())) : "false").append(", ");
		}

		if (parms.containsKey("SMTPADDRESS")) {
			@SuppressWarnings("unchecked")
			TreeMap<String, JournalsLibrary.SMTPJournal> set = (TreeMap<String, JournalsLibrary.SMTPJournal>) Resources
					.getResource("SYSTEM_SMTP_JOURNALS");
			final JournalsLibrary.SMTPJournal entry = (set != null) ? set
					.get(last.toUpperCase().trim()) : null;
			if ((entry != null) && (entry.forward)) {
				str.append(
						entry.name.replace(' ', '_') + "@"
								+ CMProps.getVar(CMProps.Str.MUDDOMAIN))
						.append(", ");
			}
		}

		if (parms.containsKey("CANADMIN") || parms.containsKey("ISADMIN"))
			str.append(
					"" + journal.authorizationCheck(M, ForumJournalFlags.ADMIN))
					.append(", ");

		if (parms.containsKey("CANPOST"))
			str.append(
					"" + journal.authorizationCheck(M, ForumJournalFlags.POST))
					.append(", ");

		if (parms.containsKey("CANREAD"))
			str.append(
					"" + journal.authorizationCheck(M, ForumJournalFlags.READ))
					.append(", ");

		if (parms.containsKey("CANREPLY"))
			str.append(
					"" + journal.authorizationCheck(M, ForumJournalFlags.REPLY))
					.append(", ");

		if (parms.containsKey("ADMINMASK"))
			str.append("" + journal.adminMask()).append(", ");

		if (parms.containsKey("READMASK"))
			str.append("" + journal.readMask()).append(", ");

		if (parms.containsKey("POSTMASK"))
			str.append("" + journal.postMask()).append(", ");

		if (parms.containsKey("REPLYMASK"))
			str.append("" + journal.replyMask()).append(", ");

		if (parms.containsKey("ID"))
			str.append("" + journal.NAME()).append(", ");

		if (parms.containsKey("NAME"))
			str.append("" + journal.NAME()).append(", ");

		if (parms.containsKey("EXPIRE"))
			str.append("").append(", ");

		JournalsLibrary.JournalSummaryStats stats = CMLib.journals()
				.getJournalStats(last);
		if (stats == null)
			return " @break@";

		if (parms.containsKey("POSTS"))
			str.append("" + stats.posts).append(", ");

		if (parms.containsKey("THREADS"))
			str.append("" + stats.threads).append(", ");

		if (parms.containsKey("SHORTDESC"))
			str.append("" + stats.shortIntro).append(", ");

		if (parms.containsKey("LONGDESC"))
			str.append("" + stats.longIntro).append(", ");

		if (parms.containsKey("IMAGEPATH")) {
			if ((stats.imagePath == null)
					|| (stats.imagePath.trim().length() == 0))
				str.append("images/lilcm.jpg").append(", ");
			else
				str.append("" + stats.threads).append(", ");
		}

		String strstr = str.toString();
		if (strstr.endsWith(", "))
			strstr = strstr.substring(0, strstr.length() - 2);
		return clearWebMacros(strstr);
	}
}