package com.planet_ink.coffee_mud.Locales;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Swamp extends StdRoom {
	public String ID() {
		return "Swamp";
	}

	public Swamp() {
		super();
		name = "the swamp";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_SWAMP;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if ((msg.amITarget(this) || (msg.targetMinor() == CMMsg.TYP_ADVANCE) || (msg
				.targetMinor() == CMMsg.TYP_RETREAT))
				&& (!msg.source().isMonster())
				&& (msg.source().curState().getHitPoints() < msg.source()
						.maxState().getHitPoints())
				&& (CMLib.dice().rollPercentage() == 1)
				&& (CMLib.dice().rollPercentage() == 1)
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
			Ability A = null;
			if (CMLib.dice().rollPercentage() > 50)
				A = CMClass.getAbility("Disease_Chlamydia");
			else
				A = CMClass.getAbility("Disease_Malaria");
			if ((A != null) && (msg.source().fetchEffect(A.ID()) == null))
				A.invoke(msg.source(), msg.source(), true, 0);
		}
		super.executeMsg(myHost, msg);
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_JADE),
			Integer.valueOf(RawMaterial.RESOURCE_SCALES),
			Integer.valueOf(RawMaterial.RESOURCE_COCOA),
			Integer.valueOf(RawMaterial.RESOURCE_COAL),
			Integer.valueOf(RawMaterial.RESOURCE_PIPEWEED),
			Integer.valueOf(RawMaterial.RESOURCE_BAMBOO),
			Integer.valueOf(RawMaterial.RESOURCE_REED),
			Integer.valueOf(RawMaterial.RESOURCE_SUGAR),
			Integer.valueOf(RawMaterial.RESOURCE_CLAY), };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return Swamp.roomResources;
	}
}