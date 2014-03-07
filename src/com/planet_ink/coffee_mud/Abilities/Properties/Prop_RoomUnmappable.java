package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Prop_RoomUnmappable extends Property {
	public String ID() {
		return "Prop_RoomUnmappable";
	}

	public String name() {
		return "Unmappable Room/Area";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS | Ability.CAN_AREAS;
	}

	private int bitStream = PhyStats.SENSE_ROOMUNMAPPABLE;

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		bitStream = 0;
		if (!CMParms.parse(newText.toUpperCase().trim()).contains("MAPOK"))
			bitStream = PhyStats.SENSE_ROOMUNMAPPABLE;
		if (CMParms.parse(newText.toUpperCase().trim()).contains("NOEXPLORE"))
			bitStream = bitStream | PhyStats.SENSE_ROOMUNEXPLORABLE;
	}

	public String accountForYourself() {
		return "Unmappable";
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		affectableStats.setSensesMask(affectableStats.sensesMask() | bitStream);
	}
}