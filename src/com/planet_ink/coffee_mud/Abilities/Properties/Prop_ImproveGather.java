package com.planet_ink.coffee_mud.Abilities.Properties;

import java.util.List;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Prop_ImproveGather extends Property {
	public String ID() {
		return "Prop_ImproveGather";
	}

	public String name() {
		return "Improve Gathering Skills";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_AREAS
				| Ability.CAN_ROOMS;
	}

	protected MaskingLibrary.CompiledZapperMask mask = null;
	protected String[] improves = new String[] { "ALL" };
	protected int improvement = 2;

	public String accountForYourself() {
		return "Improves common skills " + CMParms.toStringList(improves)
				+ ". Gain: " + improvement;
	}

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		this.improvement = CMParms.getParmInt(newText, "AMT", improvement);
		String maskStr = CMParms.getParmStr(newText, "MASK", "");
		if ((maskStr == null) || (maskStr.length() == 0))
			mask = null;
		else
			mask = CMLib.masking().maskCompile(maskStr);
		String skillStr = CMParms.getParmStr(newText, "SKILLS", "ALL");
		List<String> skills = CMParms.parseCommas(
				skillStr.toUpperCase().trim(), true);
		improves = skills.toArray(new String[0]);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if ((msg.tool() instanceof Ability)
				&& (CMath.bset(((Ability) msg.tool()).classificationCode(),
						Ability.DOMAIN_GATHERINGSKILL)
						&& (improvement != ((Ability) msg.tool()).abilityCode())
						&& (msg.source().location() != null)
						&& (msg.source() == affected)
						|| (msg.source().location() == affected)
						|| (msg.source().location().getArea() == affected) || ((affected instanceof Item)
						&& (((Item) affected).owner() == msg.source()) && (!((Item) affected)
							.amWearingAt(Item.IN_INVENTORY))))
				&& (msg.source().fetchEffect(msg.tool().ID()) == msg.tool())
				&& (CMParms.contains(improves, "ALL") || CMParms.contains(
						improves, msg.tool().ID().toUpperCase()))
				&& ((mask == null) || (CMLib.masking().maskCheck(mask,
						msg.source(), true)))) {
			((Ability) msg.tool()).setAbilityCode(improvement);
		}
		super.executeMsg(myHost, msg);
	}
}
