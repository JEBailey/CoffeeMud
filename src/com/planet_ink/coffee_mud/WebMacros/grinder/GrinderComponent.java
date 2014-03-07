package com.planet_ink.coffee_mud.WebMacros.grinder;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.AbilityComponent;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class GrinderComponent {
	public String name() {
		return "GrinderComponent";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		String last = httpReq.getUrlParameter("COMPONENT");
		if (last == null)
			return " @break@";
		if (last.length() > 0) {
			String fixedCompID = last.replace(' ', '_').toUpperCase();
			List<AbilityComponent> set = new Vector<AbilityComponent>();
			int posDex = 1;
			while (httpReq.isUrlParameter(fixedCompID + "_PIECE_CONNECTOR_"
					+ posDex)
					&& httpReq
							.getUrlParameter(
									fixedCompID + "_PIECE_CONNECTOR_" + posDex)
							.trim().length() > 0) {
				String mask = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_MASK_" + posDex);
				String str = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_STRING_" + posDex);
				String amt = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_AMOUNT_" + posDex);
				String conn = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_CONNECTOR_" + posDex);
				String loc = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_LOCATION_" + posDex);
				String type = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_TYPE_" + posDex);
				String consumed = httpReq.getUrlParameter(fixedCompID
						+ "_PIECE_CONSUMED_" + posDex);
				if (!conn.equalsIgnoreCase("DELETE")) {
					AbilityComponent able = (AbilityComponent) CMClass
							.getCommon("DefaultAbilityComponent");
					able.setAmount(CMath.s_int(amt));
					if (posDex == 1)
						able.setConnector(AbilityComponent.CompConnector.AND);
					else
						able.setConnector(AbilityComponent.CompConnector
								.valueOf(conn));
					able.setConsumed((consumed != null)
							&& (consumed.equalsIgnoreCase("on") || consumed
									.equalsIgnoreCase("checked")));
					able.setLocation(AbilityComponent.CompLocation.valueOf(loc));
					able.setMask(mask);
					able.setType(AbilityComponent.CompType.valueOf(type), str);
					set.add(able);
				}
				posDex++;
			}

			if (CMLib.ableMapper().getAbilityComponentMap()
					.containsKey(last.toUpperCase().trim())) {
				List<AbilityComponent> xset = CMLib.ableMapper()
						.getAbilityComponentMap()
						.get(last.toUpperCase().trim());
				xset.clear();
				xset.addAll(set);
			} else
				CMLib.ableMapper().getAbilityComponentMap()
						.put(last.toUpperCase().trim(), set);
			CMLib.ableMapper().alterAbilityComponentFile(
					last.toUpperCase().trim(), false);
		}
		return "";
	}
}