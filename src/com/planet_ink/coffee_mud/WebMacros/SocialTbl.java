package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;

import com.planet_ink.coffee_mud.core.CMLib;
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
public class SocialTbl extends StdWebMacro {
	public String name() {
		return "SocialTbl";
	}

	protected static final int AT_MAX_COL = 6;

	public String runMacro(HTTPRequest httpReq, String parm) {
		StringBuffer TBL = (StringBuffer) Resources
				.getResource("WEB SOCIALS TBL");
		if (TBL != null)
			return TBL.toString();

		List<String> socialVec = CMLib.socials().getSocialsList();
		StringBuffer msg = new StringBuffer("\n\r");
		int col = 0;
		int percent = 100 / AT_MAX_COL;
		for (int i = 0; i < socialVec.size(); i++) {
			if (col == 0) {
				msg.append("<tr>");
				// the bottom elements can be full width if there's
				// not enough to fill one row
				// ie. -X- -X- -X-
				// -X- -X- -X-
				// -----X-----
				// -----X-----
				if (i > socialVec.size() - AT_MAX_COL)
					percent = 100;
			}

			msg.append("<td");

			if (percent == 100)
				msg.append(" colspan=\"" + AT_MAX_COL + "\""); // last element
																// is width of
																// remainder
			else
				msg.append(" width=\"" + percent + "%\"");

			msg.append(">");
			msg.append(socialVec.get(i));
			msg.append("</td>");
			// finish the row
			if ((percent == 100) || (++col) > (AT_MAX_COL - 1)) {
				msg.append("</tr>\n\r");
				col = 0;
			}
		}
		if (!msg.toString().endsWith("</tr>\n\r"))
			msg.append("</tr>\n\r");
		Resources.submitResource("WEB SOCIALS TBL", msg);
		return clearWebMacros(msg);
	}

}
