package com.planet_ink.coffee_mud.Locales;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
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
public class UnderSaltWater extends UnderWater {
	public String ID() {
		return "UnderSaltWater";
	}

	public UnderSaltWater() {
		super();
		climask = Places.CLIMASK_WET;
		atmosphere = RawMaterial.RESOURCE_SALTWATER;
	}

	public int liquidType() {
		return RawMaterial.RESOURCE_SALTWATER;
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_SEAWEED),
			Integer.valueOf(RawMaterial.RESOURCE_FISH),
			Integer.valueOf(RawMaterial.RESOURCE_TUNA),
			Integer.valueOf(RawMaterial.RESOURCE_SHRIMP),
			Integer.valueOf(RawMaterial.RESOURCE_SAND),
			Integer.valueOf(RawMaterial.RESOURCE_CLAY),
			Integer.valueOf(RawMaterial.RESOURCE_PEARL),
			Integer.valueOf(RawMaterial.RESOURCE_LIMESTONE) };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return UnderWater.roomResources;
	}
}
