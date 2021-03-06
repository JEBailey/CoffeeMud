package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;

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

public class Chant_SenseGems extends Chant_SensePlants {
	public String ID() {
		return "Chant_SenseGems";
	}

	public String name() {
		return "Sense Gems";
	}

	public String displayText() {
		return "(Sensing Gems)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public long flags() {
		return Ability.FLAG_TRACKING;
	}

	protected String word() {
		return "gems";
	}

	private int[] myMats = { RawMaterial.MATERIAL_PRECIOUS,
			RawMaterial.MATERIAL_GLASS };

	protected int[] okMaterials() {
		return myMats;
	}

	protected int[] okResources() {
		return null;
	}
}
