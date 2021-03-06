package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Prop_SafePet extends Property {
	public String ID() {
		return "Prop_SafePet";
	}

	public String name() {
		return "Unattackable Pets";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected boolean disabled = false;
	protected String displayMessage = "Awww, leave <T-NAME> alone.";

	public String accountForYourself() {
		return "Unattackable";
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_UNATTACKABLE);
	}

	public void setMiscText(String newMiscText) {
		super.setMiscText(newMiscText);
		String newDisplayMsg = CMParms.getParmStr(newMiscText, "MSG", "");
		if (newDisplayMsg.trim().length() > 0) {
			displayMessage = newDisplayMsg.trim();
		}
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected instanceof MOB) {
			if ((msg.amISource((MOB) affected))
					&& (msg.sourceMinor() == CMMsg.TYP_DEATH)
					&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))) {
				msg.source().tell("You are safe from death.");
				return false;
			} else if (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS)) {
				if ((msg.amITarget(affected)) && (!disabled)) {
					if (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
						msg.source().tell(msg.source(), affected, null,
								displayMessage);
					((MOB) affected).makePeace();
					return false;
				} else if (msg.amISource((MOB) affected))
					disabled = true;
			} else if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
					&& msg.amITarget(affected))
				msg.setValue(0);
			else if (!((MOB) affected).isInCombat())
				disabled = false;
		} else if (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS)
				&& msg.amITarget(affected)) {
			if (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
				msg.source().tell(msg.source(), affected, null, displayMessage);
			return false;
		}
		return super.okMessage(myHost, msg);
	}
}
