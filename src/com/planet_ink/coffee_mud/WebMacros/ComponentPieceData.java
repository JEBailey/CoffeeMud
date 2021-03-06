package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Common.interfaces.AbilityComponent;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMStrings;
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
public class ComponentPieceData extends StdWebMacro {
	public String name() {
		return "ComponentPieceData";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String compID = httpReq.getUrlParameter("COMPONENT");
		if (compID == null)
			return " @break@";
		String last = httpReq.getUrlParameter("COMPONENTPIECE");
		if (last == null)
			return " @break@";
		if (last.length() > 0) {
			String fixedCompID = compID.replace(' ', '_').toUpperCase();
			StringBuilder str = new StringBuilder("");
			if (parms.containsKey("MASK") || parms.containsKey("MASKEDIT")) {
				String s = httpReq.getUrlParameter(fixedCompID + "_PIECE_MASK_"
						+ last);
				if (s == null)
					s = "";
				str.append(s);
			}
			if (parms.containsKey("STRING")) {
				String type = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_TYPE_" + last);
				String strType = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_STRING_" + last);
				AbilityComponent.CompType C = (AbilityComponent.CompType) CMath
						.s_valueOf(AbilityComponent.CompType.values(), type);
				if ((C == null) || (C == AbilityComponent.CompType.STRING)
						|| (!CMath.isNumber(strType)))
					str.append(strType);
				else
					str.append("TODO");
			}
			if (parms.containsKey("STRINGEDIT")) {
				String type = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_TYPE_" + last);
				String strType = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_STRING_" + last);
				if (strType == null)
					strType = "item name";
				AbilityComponent.CompType C = (AbilityComponent.CompType) CMath
						.s_valueOf(AbilityComponent.CompType.values(), type);
				if ((C == null) || (C == AbilityComponent.CompType.STRING)) {
					str.append("<INPUT TYPE=TEXT NAME=\"" + fixedCompID
							+ "_PIECE_STRING_" + last + "\" VALUE=\"");
					str.append(strType);
					str.append("\">");
				} else {
					str.append("<SELECT NAME=\"" + fixedCompID
							+ "_PIECE_STRING_" + last + "\">");
					if (C == AbilityComponent.CompType.MATERIAL) {
						for (RawMaterial.Material m : RawMaterial.Material
								.values()) {
							str.append("<OPTION VALUE=" + m.desc());
							if (m.mask() == CMath.s_long(strType))
								str.append(" SELECTED");
							str.append(">" + m.noun());
						}
					} else if (C == AbilityComponent.CompType.RESOURCE) {
						for (int i = 0; i < RawMaterial.CODES.TOTAL(); i++) {
							str.append("<OPTION VALUE="
									+ RawMaterial.CODES.GET(i));
							if (RawMaterial.CODES.GET(i) == CMath
									.s_long(strType))
								str.append(" SELECTED");
							str.append(">" + RawMaterial.CODES.NAME(i));
						}
					}
					str.append("</SELECT>");
				}
			}
			if (parms.containsKey("AMOUNT") || parms.containsKey("AMOUNTEDIT")) {
				String s = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_AMOUNT_" + last);
				if (s == null)
					s = "1";
				str.append(s);
			}
			if (parms.containsKey("CONSUMED")) {
				String consumed = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_CONSUMED_" + last);
				if ((consumed != null)
						&& (consumed.equalsIgnoreCase("on") || consumed
								.equalsIgnoreCase("checked")))
					str.append("consumed");
				else
					str.append("kept");
			}
			if (parms.containsKey("CONSUMEDEDIT")) {
				String consumed = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_CONSUMED_" + last);
				if ((consumed != null)
						&& (consumed.equalsIgnoreCase("on") || consumed
								.equalsIgnoreCase("checked")))
					str.append("checked");
				else
					str.append("");
			}
			if (parms.containsKey("CONNECTOR"))
				str.append(httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_CONNECTOR_" + last));
			if (parms.containsKey("CONNECTOREDIT")) {
				str.append("<OPTION VALUE=\"DELETE\">Delete Component");
				for (AbilityComponent.CompConnector conn : AbilityComponent.CompConnector
						.values()) {
					str.append("<OPTION VALUE=\"" + conn.toString() + "\" ");
					if (conn.toString().equalsIgnoreCase(
							httpReq.getUrlParameter(fixedCompID
									+ "_PIECE_CONNECTOR_" + last)))
						str.append("SELECTED ");
					str.append(">"
							+ CMStrings.capitalizeAndLower(conn.toString()));
				}
			}
			if (parms.containsKey("TYPE"))
				str.append(httpReq.getUrlParameter(fixedCompID + "_PIECE_TYPE_"
						+ last));
			if (parms.containsKey("TYPEEDIT")) {
				for (AbilityComponent.CompType conn : AbilityComponent.CompType
						.values()) {
					str.append("<OPTION VALUE=\"" + conn.toString() + "\" ");
					if (conn.toString().equalsIgnoreCase(
							httpReq.getUrlParameter(fixedCompID
									+ "_PIECE_TYPE_" + last)))
						str.append("SELECTED ");
					str.append(">"
							+ CMStrings.capitalizeAndLower(conn.toString()));
				}
			}
			if (parms.containsKey("LOCATION"))
				str.append(httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_LOCATION_" + last));
			if (parms.containsKey("LOCATIONEDIT")) {
				for (AbilityComponent.CompLocation conn : AbilityComponent.CompLocation
						.values()) {
					str.append("<OPTION VALUE=\"" + conn.toString() + "\" ");
					if (conn.toString().equalsIgnoreCase(
							httpReq.getUrlParameter(fixedCompID
									+ "_PIECE_LOCATION_" + last)))
						str.append("SELECTED ");
					str.append(">"
							+ CMStrings.capitalizeAndLower(conn.toString()));
				}
			}
			String strstr = str.toString();
			if (strstr.endsWith(", "))
				strstr = strstr.substring(0, strstr.length() - 2);
			return clearWebMacros(strstr);
		}
		return "";
	}
}
