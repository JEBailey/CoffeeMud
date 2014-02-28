package com.planet_ink.coffee_mud.Items.Armor;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class GenCloak extends GenArmor {
	public String ID() {
		return "GenCloak";
	}

	public GenCloak() {
		super();

		setName("a hooded cloak");
		setDisplayText("a hooded cloak is here");
		setDescription("");
		properWornBitmap = Wearable.WORN_ABOUT_BODY;
		wornLogicalAnd = false;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue = 1;
		recoverPhyStats();
		material = RawMaterial.RESOURCE_COTTON;
		readableText = "a hooded figure";
	}

	public void affectPhyStats(Physical host, PhyStats stats) {
		if (!amWearingAt(Wearable.IN_INVENTORY))
			stats.setName(readableText());
	}
}
