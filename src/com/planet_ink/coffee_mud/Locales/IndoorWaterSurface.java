package com.planet_ink.coffee_mud.Locales;

import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Places;

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
public class IndoorWaterSurface extends WaterSurface implements Drink {
	public String ID() {
		return "IndoorWaterSurface";
	}

	public IndoorWaterSurface() {
		super();
		name = "the water";
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
	}

	public int domainType() {
		return Room.DOMAIN_INDOORS_WATERSURFACE;
	}

	protected String UnderWaterLocaleID() {
		return "IndoorUnderWaterGrid";
	}

	protected int UnderWaterDomainType() {
		return Room.DOMAIN_INDOORS_UNDERWATER;
	}

	protected boolean IsUnderWaterFatClass(Room thatSea) {
		return (thatSea instanceof IndoorUnderWaterGrid)
				|| (thatSea instanceof IndoorUnderWaterThinGrid);
	}
}
