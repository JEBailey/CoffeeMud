package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Libraries.interfaces.PlayerLibrary;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
@SuppressWarnings("rawtypes")
public class ThinPlayerData extends StdWebMacro {

	public String name() {
		return "ThinPlayerData";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("PLAYER");
		if (last == null)
			return " @break@";
		StringBuffer str = new StringBuffer("");
		if (last.length() > 0) {
			String sort = httpReq.getUrlParameter("SORTBY");
			if (sort == null)
				sort = "";
			PlayerLibrary.ThinPlayer player = null;
			Enumeration pe = CMLib.players().thinPlayers(sort,
					httpReq.getRequestObjects());
			for (; pe.hasMoreElements();) {
				PlayerLibrary.ThinPlayer TP = (PlayerLibrary.ThinPlayer) pe
						.nextElement();
				if (TP.name.equalsIgnoreCase(last)) {
					player = TP;
					break;
				}
			}
			if (player == null)
				return " @break@";
			for (String key : parms.keySet()) {
				int x = CMLib.players().getCharThinSortCode(
						key.toUpperCase().trim(), false);
				if (x >= 0) {
					String value = CMLib.players().getThinSortValue(player, x);
					if (PlayerLibrary.CHAR_THIN_SORT_CODES[x].equals("LAST"))
						value = CMLib.time().date2String(CMath.s_long(value));
					str.append(value + ", ");
				}
			}
		}
		String strstr = str.toString();
		if (strstr.endsWith(", "))
			strstr = strstr.substring(0, strstr.length() - 2);
		return clearWebMacros(strstr);
	}

}