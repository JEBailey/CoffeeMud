package com.planet_ink.coffee_mud.WebMacros.grinder;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerAccount;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class GrinderAccounts {
	public String name() {
		return "GrinderAccounts";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		String last = httpReq.getUrlParameter("ACCOUNT");
		if (last == null)
			return " @break@";
		if (last.length() > 0) {
			PlayerAccount A = CMLib.players().getLoadAccount(last);
			if (A != null) {
				String newName = A.accountName();
				String str = null;
				String err = "";
				str = httpReq.getUrlParameter("NAME");
				if ((str != null) && (!str.equalsIgnoreCase(A.accountName()))) {
					str = CMStrings.capitalizeAndLower(str);
					if (CMLib.players().getLoadAccount(str) == null)
						newName = str;
					else
						err = "Account name '" + str + "' already exists";
				}
				str = httpReq.getUrlParameter("EMAIL");
				if (str != null)
					A.setEmail(str);
				str = httpReq.getUrlParameter("NOTES");
				if (str != null)
					A.setNotes(str);
				str = httpReq.getUrlParameter("EXPIRATION");
				if (str != null) {
					if (!CMLib.time().isValidDateString(str))
						err = "Invalid date string given.";
					else {
						Calendar C = CMLib.time().string2Date(str);
						A.setAccountExpiration(C.getTimeInMillis());
					}
				}
				String id = "";
				StringBuffer flags = new StringBuffer("");
				for (int i = 0; httpReq.isUrlParameter("FLAG" + id); id = ""
						+ (++i))
					flags.append(httpReq.getUrlParameter("FLAG" + id) + ",");
				A.setStat("FLAGS", flags.toString());
				if (err.length() > 0)
					return err;
				else if (!newName.equalsIgnoreCase(A.accountName())) {
					Vector<MOB> V = new Vector<MOB>();
					for (Enumeration<String> es = A.getPlayers(); es
							.hasMoreElements();) {
						String playerName = es.nextElement();
						MOB playerM = CMLib.players().getLoadPlayer(playerName);
						if ((playerM != null)
								&& (!CMLib.flags().isInTheGame(playerM, true)))
							V.addElement(playerM);
					}
					CMLib.database().DBDeleteAccount(A);
					A.setAccountName(newName);
					CMLib.database().DBCreateAccount(A);
					for (MOB playerM : V)
						CMLib.database().DBUpdatePlayerPlayerStats(playerM);
					httpReq.addFakeUrlParameter("ACCOUNT", newName);
				} else {
					CMLib.database().DBUpdateAccount(A);
				}
			}
		}
		return "";
	}
}
