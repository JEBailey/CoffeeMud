package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Prop_HaveZapper extends Property implements TriggeredAffect {
	public String ID() {
		return "Prop_HaveZapper";
	}

	public String name() {
		return "Restrictions to ownership";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected boolean actual = false;
	protected int percent = 100;
	protected String msgStr = "";
	protected MaskingLibrary.CompiledZapperMask mask = null;

	protected String defaultMessage() {
		return "<O-NAME> flashes and flies out of <S-HIS-HER> hands!";
	}

	public long flags() {
		return Ability.FLAG_ZAPPER;
	}

	public int triggerMask() {
		return TriggeredAffect.TRIGGER_GET;
	}

	public void setMiscText(String text) {
		super.setMiscText(text);
		actual = (text.toUpperCase() + " ").startsWith("ACTUAL ");
		if (actual)
			text = text.substring(7);
		percent = 100;
		int x = text.indexOf('%');
		if (x > 0) {
			int mul = 1;
			int tot = 0;
			while ((--x) >= 0) {
				if (Character.isDigit(text.charAt(x)))
					tot += CMath.s_int("" + text.charAt(x)) * mul;
				else
					x = -1;
				mul = mul * 10;
			}
			percent = tot;
		}
		msgStr = CMParms.getParmStr(text, "MESSAGE", defaultMessage());
		mask = CMLib.masking().getPreCompiledMask(text);
	}

	public String accountForYourself() {
		return "Ownership restricted as follows: "
				+ CMLib.masking().maskDesc(text());
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected == null)
			return false;

		MOB mob = msg.source();
		if (mob.location() == null)
			return true;

		if (msg.amITarget(affected))
			switch (msg.targetMinor()) {
			case CMMsg.TYP_HOLD:
				break;
			case CMMsg.TYP_WEAR:
				break;
			case CMMsg.TYP_WIELD:
				break;
			case CMMsg.TYP_GET:
				if ((!CMLib.masking().maskCheck(mask, mob, actual))
						&& (CMLib.dice().rollPercentage() <= percent)) {
					mob.location().show(mob, null, affected,
							CMMsg.MSG_OK_ACTION, msgStr);
					return false;
				}
				break;
			case CMMsg.TYP_EAT:
			case CMMsg.TYP_DRINK:
				if ((!CMLib.masking().maskCheck(mask, mob, actual))
						&& (CMLib.dice().rollPercentage() <= percent)) {
					mob.location().show(mob, null, affected,
							CMMsg.MSG_OK_ACTION, msgStr);
					return false;
				}
				break;
			default:
				break;
			}
		return true;
	}
}
