package com.planet_ink.coffee_mud.Races;

import java.util.List;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;

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
public class Jelly extends Unique {
	public String ID() {
		return "Jelly";
	}

	public String name() {
		return "Jelly";
	}

	public int shortestMale() {
		return 24;
	}

	public int shortestFemale() {
		return 20;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 200;
	}

	public int weightVariance() {
		return 200;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Slime";
	}

	public boolean fertile() {
		return true;
	}

	public String arriveStr() {
		return "sloshes in";
	}

	public String leaveStr() {
		return "sloshes";
	}

	public Weapon myNaturalWeapon() {
		if (naturalWeapon == null) {
			naturalWeapon = CMClass.getWeapon("StdWeapon");
			naturalWeapon.setName("an appendange");
			naturalWeapon.setRanges(0, 2);
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BERRIES);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
		}
		return naturalWeapon;
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a palm-full of "
						+ name().toLowerCase(), RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
		case Race.AGE_CHILD:
			return name().toLowerCase() + " puddle";
		default:
			return super.makeMobName('N', age);
		}
	}
}
