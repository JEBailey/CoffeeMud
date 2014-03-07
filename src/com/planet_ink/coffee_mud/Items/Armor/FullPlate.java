package com.planet_ink.coffee_mud.Items.Armor;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;

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
public class FullPlate extends StdArmor {
	public String ID() {
		return "FullPlate";
	}

	public FullPlate() {
		super();

		setName("suit of Full Plate");
		setDisplayText("a suit of Full Plate Armor.");
		setDescription("A suit of Full Plate Armor including everything from head to toe.  Fine workmanship make this both very decorative and functional.");
		properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS
				| Wearable.WORN_LEGS | Wearable.WORN_FEET | Wearable.WORN_HEAD
				| Wearable.WORN_HANDS;
		wornLogicalAnd = true;
		basePhyStats().setArmor(90);
		basePhyStats().setWeight(90);
		basePhyStats().setAbility(0);
		baseGoldValue = 20000;
		recoverPhyStats();
		material = RawMaterial.RESOURCE_STEEL;
	}

}