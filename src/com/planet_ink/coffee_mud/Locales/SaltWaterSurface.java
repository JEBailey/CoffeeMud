package com.planet_ink.coffee_mud.Locales;

import java.util.List;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;

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
@SuppressWarnings("unchecked")
public class SaltWaterSurface extends WaterSurface {
	public String ID() {
		return "SaltWaterSurface";
	}

	public SaltWaterSurface() {
		super();
	}

	protected String UnderWaterLocaleID() {
		return "UnderSaltWaterGrid";
	}

	public int liquidType() {
		return RawMaterial.RESOURCE_SALTWATER;
	}

	protected int UnderWaterDomainType() {
		return Room.DOMAIN_OUTDOORS_UNDERWATER;
	}

	protected boolean IsUnderWaterFatClass(Room thatSea) {
		return (thatSea instanceof UnderSaltWaterGrid)
				|| (thatSea instanceof UnderSaltWaterThinGrid);
	}

	public List<Integer> resourceChoices() {
		return UnderSaltWater.roomResources;
	}

}