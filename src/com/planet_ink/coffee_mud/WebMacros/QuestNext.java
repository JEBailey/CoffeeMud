package com.planet_ink.coffee_mud.WebMacros;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Quest;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class QuestNext extends StdWebMacro {
	public String name() {
		return "QuestNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("QUEST");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("QUEST");
			return "";
		}
		if (last != null)
			last = CMStrings.replaceAll(last, "*", "@");
		String lastID = "";

		Vector V = new Vector();
		for (int q = 0; q < CMLib.quests().numQuests(); q++)
			V.addElement(CMLib.quests().fetchQuest(q));
		Vector sortedV = new Vector();
		while (V.size() > 0) {
			Quest lowQ = (Quest) V.firstElement();
			for (int v = 1; v < V.size(); v++)
				if (((Quest) V.elementAt(v)).name().toUpperCase()
						.compareTo(lowQ.name().toUpperCase()) < 0)
					lowQ = (Quest) V.elementAt(v);
			V.remove(lowQ);
			sortedV.addElement(lowQ);
		}

		for (int q = 0; q < sortedV.size(); q++) {
			Quest Q = (Quest) sortedV.elementAt(q);
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!("" + Q)
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("QUEST",
						CMStrings.replaceAll("" + Q, "@", "*"));
				return "";
			}
			lastID = "" + Q;
		}
		httpReq.addFakeUrlParameter("QUEST", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
