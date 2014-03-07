package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Prop_NoDamage extends Property {
	public String ID() {
		return "Prop_NoDamage";
	}

	public String name() {
		return "No Damage";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS;
	}

	public String accountForYourself() {
		return "Harmless";
	}

	public long flags() {
		return Ability.FLAG_IMMUNER;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((msg.targetMinor() == CMMsg.TYP_DAMAGE) && (affected != null)
				&& ((msg.source() == affected) || (msg.tool() == affected)))
			msg.setValue(0);
		return true;
	}
}