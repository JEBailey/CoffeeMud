package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerAccount;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class AccountData extends StdWebMacro {
	public String name() {
		return "AccountData";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ACCOUNT");
		if (last == null)
			return "";
		if (last.length() > 0) {
			PlayerAccount A = CMLib.players().getLoadAccount(last);
			if (A == null)
				return "";
			if (parms.containsKey("NAME") || parms.containsKey("ACCOUNT"))
				return clearWebMacros(A.accountName());
			if (parms.containsKey("CLASS"))
				return clearWebMacros(A.ID());
			if (parms.containsKey("LASTIP"))
				return "" + A.lastIP();
			if (parms.containsKey("LASTDATETIME"))
				return "" + CMLib.time().date2String(A.lastDateTime());
			if (parms.containsKey("EMAIL"))
				return "" + A.getEmail();
			if (parms.containsKey("NOTES"))
				return "" + A.notes();
			if (parms.containsKey("ACCTEXPIRATION"))
				return "" + CMLib.time().date2String(A.getAccountExpiration());
			for (String flag : PlayerAccount.FLAG_DESCS)
				if (parms.containsKey("IS" + flag))
					return "" + A.isSet(flag);
			if (parms.containsKey("FLAGS")) {
				String old = httpReq.getUrlParameter("FLAGS");
				List<String> set = null;
				if (old == null) {
					String matList = A.getStat("FLAG");
					set = CMParms.parseCommas(matList, true);
				} else {
					String id = "";
					set = new Vector();
					for (int i = 0; httpReq.isUrlParameter("FLAG" + id); id = ""
							+ (++i))
						set.add(httpReq.getUrlParameter("FLAG" + id));
				}
				StringBuffer str = new StringBuffer("");
				for (int i = 0; i < PlayerAccount.FLAG_DESCS.length; i++) {
					str.append("<OPTION VALUE=\"" + PlayerAccount.FLAG_DESCS[i]
							+ "\"");
					if (set.contains(PlayerAccount.FLAG_DESCS[i]))
						str.append(" SELECTED");
					str.append(">"
							+ CMStrings
									.capitalizeAndLower(PlayerAccount.FLAG_DESCS[i]));
				}
				str.append(", ");
			}
			if (parms.containsKey("IGNORE"))
				return "" + CMParms.toStringList(A.getIgnored());
		}
		return "";
	}
}
