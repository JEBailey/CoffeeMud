package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;
import java.util.Map;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class AllQualifyData extends StdWebMacro {
	public String name() {
		return "AllQualifyData";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ALLQUALID");
		String which = httpReq.getUrlParameter("ALLQUALWHICH");
		if (parms.containsKey("WHICH"))
			which = parms.get("WHICH");
		String origiWhich = which;
		if ((which == null) || (which.length() == 0))
			which = "ALL";
		Map<String, Map<String, AbilityMapper.AbilityMapping>> allQualMap = CMLib
				.ableMapper().getAllQualifiesMap(httpReq.getRequestObjects());
		Map<String, AbilityMapper.AbilityMapping> map = allQualMap.get(which
				.toUpperCase().trim());
		if (map == null)
			return "";

		AbilityMapper.AbilityMapping mapped = map.get(last);
		if (mapped == null) {
			if ((origiWhich == null) && (allQualMap.get("EACH") != null))
				mapped = allQualMap.get("EACH").get(last);
			if (mapped == null)
				return "";
		}
		StringBuilder str = new StringBuilder("");
		if (parms.containsKey("NAME")) {
			Ability A = CMClass.getAbility(last);
			if (A != null)
				str.append(A.name()).append(", ");
		}

		if (parms.containsKey("LEVEL")) {
			String lvl = httpReq.getUrlParameter("LEVEL");
			if (lvl == null)
				lvl = Integer.toString(mapped.qualLevel);
			else
				lvl = Integer.toString(CMath.s_int(lvl));
			str.append(lvl).append(", ");
		}

		if (parms.containsKey("PROF")) {
			String prof = httpReq.getUrlParameter("PROF");
			if (prof == null)
				prof = Integer.toString(mapped.defaultProficiency);
			else
				prof = Integer.toString(CMath.s_int(prof));
			str.append(prof).append(", ");
		}

		if (parms.containsKey("MASK")) {
			String s = httpReq.getUrlParameter("MASK");
			if (s == null)
				s = mapped.extraMask;
			str.append(s).append(", ");
		}

		if (parms.containsKey("AUTOGAIN")) {
			String s = httpReq.getUrlParameter("AUTOGAIN");
			if (s == null)
				s = mapped.autoGain ? "on" : "";
			str.append(s.equalsIgnoreCase("on") ? "true" : "false")
					.append(", ");
		}

		if (parms.containsKey("REQUIRES")) {
			if (!httpReq.isUrlParameter("REQABLE1")) {
				int pnum = 1;
				for (String s : CMParms.parseCommas(
						mapped.originalSkillPreReqList, true)) {
					String ableID = s;
					String lvl = "";
					int x = s.indexOf('(');
					if (s.endsWith(")") && (x > 1)) {
						ableID = s.substring(0, x);
						lvl = s.substring(x + 1, s.length() - 1).trim();
					}
					Ability A = CMClass.getAbility(ableID);
					if (A != null) {
						httpReq.addFakeUrlParameter("REQABLE" + pnum, ableID);
						httpReq.addFakeUrlParameter("REQLEVEL" + pnum, lvl);
						pnum++;
					}
				}
				httpReq.addFakeUrlParameter("REQABLE" + pnum, "");
				httpReq.addFakeUrlParameter("REQLEVEL" + pnum, "");
			} else {
				int curChkNum = 1;
				int curWriteNum = 1;
				while (httpReq.isUrlParameter("REQABLE" + curChkNum)) {
					String curVal = httpReq.getUrlParameter("REQABLE"
							+ curChkNum);
					if (curVal.equals("DEL") || curVal.equals("DELETE")
							|| curVal.trim().length() == 0) {
						curChkNum++;
						continue;
					}
					httpReq.addFakeUrlParameter("REQABLE" + curWriteNum, curVal);
					httpReq.addFakeUrlParameter("REQLEVEL" + curWriteNum,
							httpReq.getUrlParameter("REQLEVEL" + curChkNum));
					curChkNum++;
					curWriteNum++;
				}
				httpReq.removeUrlParameter("REQABLE" + curWriteNum);
				httpReq.removeUrlParameter("REQLEVEL" + curWriteNum);
			}
			if (parms.containsKey("RESET")) {
				httpReq.removeUrlParameter("REQUIRESNUM");
				httpReq.removeUrlParameter("REQUIRESNAME1");
				httpReq.removeUrlParameter("REQUIRESNAME2");
				return "";
			}
			if (parms.containsKey("NEXT")) {
				String lastR = httpReq.getUrlParameter("REQUIRESNUM");
				String lastID = "";
				int curChkNum = 1;
				int curWriteNum = 1;
				while (httpReq.isUrlParameter("REQABLE" + curChkNum)) {
					String thisName = Integer.toString(curChkNum);
					if ((lastR == null)
							|| ((lastR.length() > 0) && (lastR.equals(lastID)) && (!thisName
									.equals(lastID)))) {
						httpReq.addFakeUrlParameter("REQUIRESNUM", thisName);
						lastR = thisName;
						httpReq.addFakeUrlParameter("REQUIRESNAME1", "REQABLE"
								+ curWriteNum);
						httpReq.addFakeUrlParameter("REQUIRESNAME2", "REQLEVEL"
								+ curWriteNum);
						return "";
					}
					curChkNum++;
					curWriteNum++;
					lastID = thisName;
				}
				httpReq.addFakeUrlParameter("REQUIRESNUM", "");
				httpReq.addFakeUrlParameter("REQUIRESNAME1", "REQABLE"
						+ curWriteNum);
				httpReq.addFakeUrlParameter("REQUIRESNAME2", "REQLEVEL"
						+ curWriteNum);
				if (parms.containsKey("EMPTYOK"))
					return "<!--EMPTY-->";
				return " @break@";
			}
			if (parms.containsKey("ABLEEDIT")) {
				String lastR = httpReq.getUrlParameter("REQUIRESNUM");
				String ableID = httpReq.getUrlParameter("REQABLE" + lastR);
				if ((ableID != null) && (ableID.length() > 0)) {
					str.append("<OPTION VALUE=\"DEL\">Delete!");
					final Ability A = CMClass.getAbility(ableID);
					if (A != null)
						str.append("<OPTION VALUE=\"" + A.ID() + "\" SELECTED>"
								+ A.ID());
				} else {
					str.append("<OPTION VALUE=\"\">Add New");
					for (Enumeration<Ability> a = CMClass.abilities(); a
							.hasMoreElements();) {
						final Ability A = a.nextElement();
						if ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ARCHON)
							continue;
						str.append("<OPTION VALUE=\"" + A.ID() + "\">" + A.ID());
					}
				}
				str.append(", ");
			}

		}

		String strstr = str.toString();
		if (strstr.endsWith(", "))
			strstr = strstr.substring(0, strstr.length() - 2);
		return clearWebMacros(strstr);
	}
}