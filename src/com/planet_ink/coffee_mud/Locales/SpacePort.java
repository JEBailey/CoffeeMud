package com.planet_ink.coffee_mud.Locales;

import com.planet_ink.coffee_mud.Locales.interfaces.LocationRoom;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.SpaceObject;

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
public class SpacePort extends StdRoom implements LocationRoom {
	public String ID() {
		return "SpacePort";
	}

	protected double[] dirFromCore = new double[2];

	public SpacePort() {
		super();
		name = "the space port";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_SPACEPORT;
	}

	@Override
	public long[] coordinates() {
		SpaceObject planet = CMLib.map().getSpaceObject(this, true);
		if (planet != null)
			return CMLib.map().getLocation(planet.coordinates(), dirFromCore,
					planet.radius());
		return new long[] { 0, 0, 0 };
	}

	@Override
	public double[] getDirectionFromCore() {
		return dirFromCore;
	}

	@Override
	public void setDirectionFromCore(double[] dir) {
		if ((dir != null) && (dir.length == 2))
			dirFromCore = dir;
	}

	private final static String[] MYCODES = { "COREDIR" };

	public String getStat(String code) {
		switch (getLocCodeNum(code)) {
		case 0:
			return CMParms.toStringList(this.getDirectionFromCore());
		default:
			return super.getStat(code);
		}
	}

	public void setStat(String code, String val) {
		switch (getLocCodeNum(code)) {
		case 0:
			this.setDirectionFromCore(CMParms.toDoubleArray(CMParms
					.parseCommas(val, true)));
			break;
		default:
			super.setStat(code, val);
			break;
		}
	}

	protected int getLocCodeNum(String code) {
		for (int i = 0; i < MYCODES.length; i++)
			if (code.equalsIgnoreCase(MYCODES[i]))
				return i;
		return -1;
	}

	private static String[] codes = null;

	public String[] getStatCodes() {
		return (codes != null) ? codes : (codes = CMProps.getStatCodesList(
				CMParms.appendToArray(super.getStatCodes(), MYCODES), this));
	}
}