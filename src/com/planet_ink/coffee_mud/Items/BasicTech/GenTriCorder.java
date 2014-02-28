package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class GenTriCorder extends StdTriCorder {
	public String ID() {
		return "GenTriCorder";
	}

	public GenTriCorder() {
		super();
	}

	public boolean isGeneric() {
		return true;
	}

	public String text() {
		return CMLib.coffeeMaker().getPropertiesStr(this, false);
	}

	public void setMiscText(String newText) {
		miscText = "";
		CMLib.coffeeMaker().setPropertiesStr(this, newText, false);
		recoverPhyStats();
	}

	private final static String[] MYCODES = { "POWERCAP", "ACTIVATED",
			"POWERREM", "MANUFACTURER" };

	public String getStat(String code) {
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenItemStat(this, code);
		switch (getCodeNum(code)) {
		case 0:
			return "" + powerCapacity();
		case 1:
			return "" + activated();
		case 2:
			return "" + powerRemaining();
		case 3:
			return "" + getManufacturerName();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(),
					xtraValues, code);
		}
	}

	public void setStat(String code, String val) {
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenItemStat(this, code, val);
		else
			switch (getCodeNum(code)) {
			case 0:
				setPowerCapacity(CMath.s_parseLongExpression(val));
				break;
			case 1:
				activate(CMath.s_bool(val));
				break;
			case 2:
				setPowerRemaining(CMath.s_parseLongExpression(val));
				break;
			case 3:
				setManufacturerName(val);
				break;
			default:
				CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues,
						code, val);
				break;
			}
	}

	protected int getCodeNum(String code) {
		for (int i = 0; i < MYCODES.length; i++)
			if (code.equalsIgnoreCase(MYCODES[i]))
				return i;
		return -1;
	}

	private static String[] codes = null;

	public String[] getStatCodes() {
		if (codes != null)
			return codes;
		String[] MYCODES = CMProps.getStatCodesList(GenTriCorder.MYCODES, this);
		String[] superCodes = GenericBuilder.GENITEMCODES;
		codes = new String[superCodes.length + MYCODES.length];
		int i = 0;
		for (; i < superCodes.length; i++)
			codes[i] = superCodes[i];
		for (int x = 0; x < MYCODES.length; i++, x++)
			codes[i] = MYCODES[x];
		return codes;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof GenTriCorder))
			return false;
		String[] theCodes = getStatCodes();
		for (int i = 0; i < theCodes.length; i++)
			if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
				return false;
		return true;
	}
}
