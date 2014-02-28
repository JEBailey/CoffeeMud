package com.planet_ink.coffee_mud.Items.Weapons;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class WarHammer extends StdWeapon {
	public String ID() {
		return "WarHammer";
	}

	public WarHammer() {
		super();

		setName("a warhammer");
		setDisplayText("a brutal warhammer sits here");
		setDescription("It has a large wooden handle with a brutal blunt double-head.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		baseGoldValue = 25;
		wornLogicalAnd = true;
		properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
		recoverPhyStats();
		weaponType = Weapon.TYPE_BASHING;
		material = RawMaterial.RESOURCE_STEEL;
		weaponClassification = Weapon.CLASS_HAMMER;
	}

}
