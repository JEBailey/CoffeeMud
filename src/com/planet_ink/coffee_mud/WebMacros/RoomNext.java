package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.core.CMLib;
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
@SuppressWarnings("rawtypes")
public class RoomNext extends StdWebMacro {
	public String name() {
		return "RoomNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String area = httpReq.getUrlParameter("AREA");
		if ((area == null) || (CMLib.map().getArea(area) == null))
			return " @break@";
		Area A = CMLib.map().getArea(area);
		String last = httpReq.getUrlParameter("ROOM");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("ROOM");
			return "";
		}
		String lastID = "";

		for (Enumeration d = A.getProperRoomnumbers().getRoomIDs(); d
				.hasMoreElements();) {
			String roomid = (String) d.nextElement();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!roomid
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("ROOM", roomid);
				return "";
			}
			lastID = roomid;
		}
		httpReq.addFakeUrlParameter("ROOM", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
