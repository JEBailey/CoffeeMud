package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

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
public class GenRideable extends StdRideable {
	public String ID() {
		return "GenRideable";
	}

	public GenRideable() {
		super();
		username = "a generic horse";
		setDescription("");
		setDisplayText("A generic horse stands here.");
		basePhyStats().setAbility(11); // his only off-default

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

	public boolean isGeneric() {
		return true;
	}

	public String text() {
		if (CMProps.getBoolVar(CMProps.Bool.MOBCOMPRESS))
			miscText = CMLib.encoder().compressString(
					CMLib.coffeeMaker().getPropertiesStr(this, false));
		else
			miscText = CMLib.coffeeMaker().getPropertiesStr(this, false);
		return super.text();
	}

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		CMLib.coffeeMaker().resetGenMOB(this, newText);
	}

	private final static String[] MYCODES = { "RIDEBASIS", "MOBSHELD" };

	public String getStat(String code) {
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenMobStat(this, code);
		switch (getCodeNum(code)) {
		case 0:
			return "" + rideBasis();
		case 1:
			return "" + riderCapacity();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(),
					xtraValues, code);
		}
	}

	public void setStat(String code, String val) {
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenMobStat(this, code, val);
		else
			switch (getCodeNum(code)) {
			case 0:
				setRideBasis(CMath.s_parseListIntExpression(
						Rideable.RIDEABLE_DESCS, val));
				break;
			case 1:
				setRiderCapacity(CMath.s_parseIntExpression(val));
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
		String[] MYCODES = CMProps.getStatCodesList(GenRideable.MYCODES, this);
		String[] superCodes = GenericBuilder.GENMOBCODES;
		codes = new String[superCodes.length + MYCODES.length];
		int i = 0;
		for (; i < superCodes.length; i++)
			codes[i] = superCodes[i];
		for (int x = 0; x < MYCODES.length; i++, x++)
			codes[i] = MYCODES[x];
		return codes;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof GenRideable))
			return false;
		String[] codes = getStatCodes();
		for (int i = 0; i < codes.length; i++)
			if (!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}