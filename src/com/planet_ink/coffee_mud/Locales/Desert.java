package com.planet_ink.coffee_mud.Locales;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Desert extends StdRoom {
	public String ID() {
		return "Desert";
	}

	public Desert() {
		super();
		name = "the desert";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask = Places.CLIMASK_HOT | CLIMASK_DRY;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_DESERT;
	}

	protected int baseThirst() {
		return 4;
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_CACTUS),
			Integer.valueOf(RawMaterial.RESOURCE_SAND),
			Integer.valueOf(RawMaterial.RESOURCE_LAMPOIL),
			Integer.valueOf(RawMaterial.RESOURCE_PEPPERS),
			Integer.valueOf(RawMaterial.RESOURCE_SCALES),
			Integer.valueOf(RawMaterial.RESOURCE_DATES) };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return Desert.roomResources;
	}
}
