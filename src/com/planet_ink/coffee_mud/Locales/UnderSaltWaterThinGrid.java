package com.planet_ink.coffee_mud.Locales;

import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
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
@SuppressWarnings("unchecked")
public class UnderSaltWaterThinGrid extends UnderWaterThinGrid {
	public String ID() {
		return "UnderSaltWaterThinGrid";
	}

	public UnderSaltWaterThinGrid() {
		super();
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_SWIMMING);
		basePhyStats.setWeight(3);
		setDisplayText("Under the water");
		setDescription("");
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
		atmosphere = RawMaterial.RESOURCE_SALTWATER;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_UNDERWATER;
	}

	protected int baseThirst() {
		return 0;
	}

	public CMObject newInstance() {
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new UnderSaltWaterGrid().newInstance();
	}

	public String getGridChildLocaleID() {
		return "UnderSaltWater";
	}

	public List<Integer> resourceChoices() {
		return UnderSaltWater.roomResources;
	}
}