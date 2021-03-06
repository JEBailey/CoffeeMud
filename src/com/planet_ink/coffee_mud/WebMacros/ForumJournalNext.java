package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ForumJournalNext extends StdWebMacro {
	public String name() {
		return "ForumJournalNext";
	}

	public static MOB guestM = null;

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("JOURNAL");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("JOURNAL");
			httpReq.getRequestObjects().remove("JOURNALLIST");
			return "";
		}
		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if (M == null) {
			if (guestM == null) {
				guestM = CMClass.getFactoryMOB();
				guestM.basePhyStats().setLevel(0);
				guestM.phyStats().setLevel(0);
				guestM.setName("guest");
			}
			M = guestM;
		}

		List<String> journals = (List<String>) httpReq.getRequestObjects().get(
				"JOURNALLIST");
		if (journals == null) {
			journals = new Vector();
			for (Enumeration e = CMLib.journals().forumJournals(); e
					.hasMoreElements();) {
				JournalsLibrary.ForumJournal CJ = (JournalsLibrary.ForumJournal) e
						.nextElement();
				if ((!journals.contains(CJ.NAME().toUpperCase()))
						&& (CMLib.masking().maskCheck(CJ.readMask(), M, true)))
					journals.add(CJ.NAME());
			}
			httpReq.getRequestObjects().put("JOURNALLIST", journals);
		}
		String lastID = "";
		HashSet<String> H = CMLib.journals().getArchonJournalNames();
		boolean allForumJournals = false;
		if ((Thread.currentThread() instanceof MWThread)
				&& CMath.s_bool(((MWThread) Thread.currentThread()).getConfig()
						.getMiscProp("ADMIN"))
				&& parms.containsKey("ALLFORUMJOURNALS"))
			allForumJournals = true;

		for (int j = 0; j < journals.size(); j++) {
			String B = journals.get(j);
			if ((!allForumJournals) && (H.contains(B.toUpperCase().trim()))
					&& ((M == null) || (!CMSecurity.isASysOp(M))))
				continue;
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!B
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("JOURNAL", B);
				return "";
			}
			lastID = B;
		}
		httpReq.addFakeUrlParameter("JOURNAL", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
