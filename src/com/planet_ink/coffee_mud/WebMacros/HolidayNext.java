package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HolidayNext extends StdWebMacro {
	public String name() {
		return "HolidayNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("HOLIDAY");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("HOLIDAY");
			return "";
		}
		Object resp = CMLib.quests().getHolidayFile();
		List<String> steps = null;
		if (resp instanceof List)
			steps = (List<String>) resp;
		else if (resp instanceof String)
			return (String) resp;
		else
			return "[Unknown error.]";
		Vector holidays = new Vector();
		List<String> line = null;
		String var = null;
		List<String> V = null;
		for (int s = 1; s < steps.size(); s++) {
			String step = steps.get(s);
			V = Resources.getFileLineVector(new StringBuffer(step));
			List<List<String>> cmds = CMLib.quests().parseQuestCommandLines(V,
					"SET", 0);
			// Vector areaLine=null;
			List<String> nameLine = null;
			for (int v = 0; v < cmds.size(); v++) {
				line = cmds.get(v);
				if (line.size() > 1) {
					var = line.get(1).toUpperCase();
					// if(var.equals("AREAGROUP"))
					// { areaLine=line;}
					if (var.equals("NAME")) {
						nameLine = line;
					}
				}
			}
			if (nameLine != null) {
				/*
				 * String areaName=null; if(areaLine==null)
				 * areaName="*special*"; else
				 * areaName=CMParms.combineWithQuotes(areaLine,2);
				 */
				String name = CMParms.combine(nameLine, 2);
				holidays.addElement(name);
			}
		}
		String lastID = "";
		for (Enumeration q = holidays.elements(); q.hasMoreElements();) {
			String holidayID = (String) q.nextElement();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!holidayID
							.equalsIgnoreCase(lastID)))) {
				httpReq.addFakeUrlParameter("HOLIDAY", holidayID);
				return "";
			}
			lastID = holidayID;
		}
		httpReq.addFakeUrlParameter("HOLIDAY", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}