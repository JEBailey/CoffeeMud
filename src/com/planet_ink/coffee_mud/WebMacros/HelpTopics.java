package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;

import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class HelpTopics extends StdWebMacro {
	public String name() {
		return "HelpTopics";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("HELPTOPIC");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("HELPTOPIC");
			httpReq.removeUrlParameter("HELPFIRSTLETTER");
			return "";
		} else if (parms.containsKey("DATA")) {
			int limit = 78;
			if (parms.containsKey("LIMIT"))
				limit = CMath.s_int(parms.get("LIMIT"));
			if ((last != null) && (last.length() > 0)) {
				StringBuilder s = CMLib.help().getHelpText(last, null,
						parms.containsKey("AHELP"));
				if (s != null)
					return clearWebMacros(helpHelp(s, limit).toString());
			}
			return "";
		} else if (parms.containsKey("NEXTLETTER")) {
			String fletter = httpReq.getUrlParameter("HELPFIRSTLETTER");
			if ((fletter == null) || (fletter.length() == 0))
				fletter = "A";
			else if (fletter.charAt(0) >= 'Z') {
				httpReq.addFakeUrlParameter("HELPFIRSTLETTER", "");
				return " @break@";
			} else
				fletter = Character.toString((char) (fletter.charAt(0) + 1));
			httpReq.addFakeUrlParameter("HELPFIRSTLETTER", fletter);
		} else if (parms.containsKey("NEXT")) {
			List<String> topics = null;
			if (parms.containsKey("ARCHON")) {
				topics = (List) httpReq.getRequestObjects().get(
						"HELP_ARCHONTOPICS");
				if (topics == null) {
					topics = CMLib.help().getTopics(true, false);
					httpReq.getRequestObjects()
							.put("HELP_ARCHONTOPICS", topics);
				}
			} else if (parms.containsKey("BOTH")) {
				topics = (List) httpReq.getRequestObjects().get(
						"HELP_BOTHTOPICS");
				if (topics == null) {
					topics = CMLib.help().getTopics(true, true);
					httpReq.getRequestObjects().put("HELP_BOTHTOPICS", topics);
				}
			} else {
				topics = (List) httpReq.getRequestObjects().get(
						"HELP_HELPTOPICS");
				if (topics == null) {
					topics = CMLib.help().getTopics(false, true);
					httpReq.getRequestObjects().put("HELP_HELPTOPICS", topics);
				}
			}

			boolean noables = parms.containsKey("SHORT");
			String fletter = parms.get("FIRSTLETTER");
			if (fletter == null)
				fletter = httpReq.getUrlParameter("FIRSTLETTER");
			if (fletter == null)
				fletter = "";

			String lastID = "";
			for (int h = 0; h < topics.size(); h++) {
				String topic = topics.get(h);
				if (noables && CMLib.help().isPlayerSkill(topic))
					continue;
				if (topic.startsWith(fletter) || (fletter.length() == 0))
					if ((last == null)
							|| ((last.length() > 0) && (last.equals(lastID)) && (!topic
									.equals(lastID)))) {
						httpReq.addFakeUrlParameter("HELPTOPIC", topic);
						return "";
					}
				lastID = topic;
			}
			httpReq.addFakeUrlParameter("HELPTOPIC", "");
			if (parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		} else if (last != null)
			return last;
		return "<!--EMPTY-->";
	}

}