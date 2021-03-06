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
public class Shore extends StdRoom {
	public String ID() {
		return "Shore";
	}

	public Shore() {
		super();
		name = "the shore";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
	}

	protected int baseThirst() {
		return 1;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_DESERT;
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_FISH),
			Integer.valueOf(RawMaterial.RESOURCE_SAND) };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return Shore.roomResources;
	}
}
