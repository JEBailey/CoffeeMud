package com.planet_ink.coffee_mud.Locales;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
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
public class CaveRoom extends StdRoom {
	public String ID() {
		return "CaveRoom";
	}

	public CaveRoom() {
		super();
		name = "the cave";
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_DARK);
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask = Places.CLIMASK_NORMAL;
	}

	public int domainType() {
		return Room.DOMAIN_INDOORS_CAVE;
	}

	public int maxRange() {
		return 5;
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
			Ability A = CMClass.getAbility("Disease_Syphilis");
			if ((A != null) && (msg.source().fetchEffect(A.ID()) == null))
				A.invoke(msg.source(), msg.source(), true, 0);
		}
		super.executeMsg(myHost, msg);
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_GRANITE),
			Integer.valueOf(RawMaterial.RESOURCE_OBSIDIAN),
			Integer.valueOf(RawMaterial.RESOURCE_MARBLE),
			Integer.valueOf(RawMaterial.RESOURCE_STONE),
			Integer.valueOf(RawMaterial.RESOURCE_ALABASTER),
			Integer.valueOf(RawMaterial.RESOURCE_IRON),
			Integer.valueOf(RawMaterial.RESOURCE_LEAD),
			Integer.valueOf(RawMaterial.RESOURCE_GOLD),
			Integer.valueOf(RawMaterial.RESOURCE_WHITE_GOLD),
			Integer.valueOf(RawMaterial.RESOURCE_CHROMIUM),
			Integer.valueOf(RawMaterial.RESOURCE_SILVER),
			Integer.valueOf(RawMaterial.RESOURCE_ZINC),
			Integer.valueOf(RawMaterial.RESOURCE_COPPER),
			Integer.valueOf(RawMaterial.RESOURCE_TIN),
			Integer.valueOf(RawMaterial.RESOURCE_MITHRIL),
			Integer.valueOf(RawMaterial.RESOURCE_MUSHROOMS),
			Integer.valueOf(RawMaterial.RESOURCE_GEM),
			Integer.valueOf(RawMaterial.RESOURCE_PERIDOT),
			Integer.valueOf(RawMaterial.RESOURCE_DIAMOND),
			Integer.valueOf(RawMaterial.RESOURCE_LAPIS),
			Integer.valueOf(RawMaterial.RESOURCE_BLOODSTONE),
			Integer.valueOf(RawMaterial.RESOURCE_MOONSTONE),
			Integer.valueOf(RawMaterial.RESOURCE_ALEXANDRITE),
			Integer.valueOf(RawMaterial.RESOURCE_GEM),
			Integer.valueOf(RawMaterial.RESOURCE_SCALES),
			Integer.valueOf(RawMaterial.RESOURCE_CRYSTAL),
			Integer.valueOf(RawMaterial.RESOURCE_RUBY),
			Integer.valueOf(RawMaterial.RESOURCE_EMERALD),
			Integer.valueOf(RawMaterial.RESOURCE_SAPPHIRE),
			Integer.valueOf(RawMaterial.RESOURCE_AGATE),
			Integer.valueOf(RawMaterial.RESOURCE_CITRINE),
			Integer.valueOf(RawMaterial.RESOURCE_PLATINUM) };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return CaveRoom.roomResources;
	}
}
