package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Common.interfaces.Quest;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

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
public class QuestData extends StdWebMacro {
	public String name() {
		return "QuestData";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("QUEST");
		if (last == null)
			return "";
		if (last.length() > 0) {
			Quest Q = CMLib.quests().fetchQuest(last);
			if (Q == null) {
				String newLast = CMStrings.replaceAll(last, "*", "@");
				for (int q = 0; q < CMLib.quests().numQuests(); q++)
					if (("" + CMLib.quests().fetchQuest(q)).equals(newLast)) {
						Q = CMLib.quests().fetchQuest(q);
						break;
					}
			}
			if (Q == null)
				return "";
			if (parms.containsKey("NAME"))
				return clearWebMacros(Q.name());
			if (parms.containsKey("ID"))
				return clearWebMacros(CMStrings.replaceAll("" + Q, "@", "*"));
			if (parms.containsKey("DURATION"))
				return "" + Q.duration();
			if (parms.containsKey("WAIT"))
				return "" + Q.minWait();
			if (parms.containsKey("INTERVAL"))
				return "" + Q.waitInterval();
			if (parms.containsKey("RUNNING"))
				return "" + Q.running();
			if (parms.containsKey("WAITING"))
				return "" + Q.waiting();
			if (parms.containsKey("SUSPENDED"))
				return "" + Q.suspended();
			if (parms.containsKey("REMAINING"))
				return "" + Q.minsRemaining();
			if (parms.containsKey("REMAININGLEFT")) {
				if (Q.duration() == 0)
					return "eternity";
				return Q.minsRemaining() + " minutes";
			}
			if (parms.containsKey("WAITLEFT"))
				return "" + Q.waitRemaining();
			if (parms.containsKey("WAITMINSLEFT")) {
				long min = Q.waitRemaining();
				if (min > 0) {
					min = min * CMProps.getTickMillis();
					if (min > 60000)
						return (min / 60000) + " minutes";
					return (min / 1000) + " seconds";
				}
				return min + " minutes";
			}
			if (parms.containsKey("WINNERS"))
				return "" + Q.getWinnerStr();
			if (parms.containsKey("RAWTEXT")) {
				StringBuffer script = new StringBuffer(Q.script());
				if ((parms.containsKey("REDIRECT"))
						&& (script.toString().toUpperCase().trim()
								.startsWith("LOAD="))) {
					String fileName = script.toString().trim().substring(5);
					CMFile F = new CMFile(
							Resources.makeFileResourceName(fileName), null,
							CMFile.FLAG_LOGERRORS);
					if ((F.exists()) && (F.canRead()))
						script = F.text();
					script = new StringBuffer(CMStrings.replaceAll(
							script.toString(), "\n\r", "\n"));
				}
				script = new StringBuffer(CMStrings.replaceAll(
						script.toString(), "&", "&amp;"));
				String postFix = "";
				int limit = script.toString().toUpperCase().indexOf("<?XML");
				if (limit >= 0) {
					postFix = script.toString().substring(limit);
					script = new StringBuffer(script.toString().substring(0,
							limit));
				}
				for (int i = 0; i < script.length(); i++)
					if ((script.charAt(i) == ';')
							&& ((i == 0) || (script.charAt(i - 1) != '\\')))
						script.setCharAt(i, '\n');
				script = new StringBuffer(CMStrings.replaceAll(
						script.toString(), "\\;", ";"));
				return clearWebMacros(script + postFix);
			}
		}
		return "";
	}
}
