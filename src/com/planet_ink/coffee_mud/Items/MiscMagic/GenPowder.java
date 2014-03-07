package com.planet_ink.coffee_mud.Items.MiscMagic;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

/**
 * <p>
 * Title: False Realities Flavored CoffeeMUD
 * </p>
 * <p>
 * Description: The False Realities Version of CoffeeMUD
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004 Jeremy Vyska
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License.
 * <p>
 * You may obtain a copy of the License at
 * 
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * <p>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * <p>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * <p>
 * limitations under the License.
 * <p>
 * Company: http://www.falserealities.com
 * </p>
 * 
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */

public class GenPowder extends StdPowder {
	public String ID() {
		return "GenPowder";
	}

	public GenPowder() {
		super();
		setName("a generic powder");
		basePhyStats.setWeight(1);
		setDisplayText("a generic powder sits here.");
		setDescription("");
		baseGoldValue = 1;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_ASH);
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

	public String getStat(String code) {
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenItemStat(this, code);
		return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues,
				code);
	}

	public void setStat(String code, String val) {
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenItemStat(this, code, val);
		CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
	}

	private static String[] codes = null;

	public String[] getStatCodes() {
		if (codes == null)
			codes = CMProps.getStatCodesList(GenericBuilder.GENITEMCODES, this);
		return codes;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof GenPowder))
			return false;
		for (int i = 0; i < getStatCodes().length; i++)
			if (!E.getStat(getStatCodes()[i])
					.equals(getStat(getStatCodes()[i])))
				return false;
		return true;
	}
}