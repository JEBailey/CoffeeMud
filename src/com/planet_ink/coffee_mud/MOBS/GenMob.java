package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class GenMob extends StdMOB {
	public String ID() {
		return "GenMob";
	}

	public GenMob() {
		super();
		username = "a generic mob";
		setDescription("");
		setDisplayText("A generic mob stands here.");

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

	public String getStat(String code) {
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenMobStat(this, code);
		return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues,
				code);
	}

	public void setStat(String code, String val) {
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenMobStat(this, code, val);
		CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
	}

	private static String[] codes = null;

	public String[] getStatCodes() {
		if (codes == null)
			codes = CMProps.getStatCodesList(GenericBuilder.GENMOBCODES, this);
		return codes;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof GenMob))
			return false;
		String[] theCodes = getStatCodes();
		for (int i = 0; i < theCodes.length; i++)
			if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
				return false;
		return true;
	}
}
