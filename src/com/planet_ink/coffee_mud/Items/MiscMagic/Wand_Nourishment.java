package com.planet_ink.coffee_mud.Items.MiscMagic;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Wand_Nourishment extends StdWand {
	public String ID() {
		return "Wand_Nourishment";
	}

	public Wand_Nourishment() {
		super();

		setName("a wooden wand");
		setDisplayText("a small wooden wand is here.");
		setDescription("A wand made out of wood");
		secretIdentity = "The wand of nourishment.  Hold the wand say \\`shazam\\` to it.";
		baseGoldValue = 200;
		material = RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
		secretWord = "SHAZAM";
	}

	public void setSpell(Ability theSpell) {
		super.setSpell(theSpell);
		secretWord = "SHAZAM";
	}

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		secretWord = "SHAZAM";
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (msg.amITarget(this)) {
			MOB mob = msg.source();
			switch (msg.targetMinor()) {
			case CMMsg.TYP_WAND_USE:
				if ((mob.isMine(this)) && (!amWearingAt(Wearable.IN_INVENTORY))
						&& (msg.targetMessage() != null))
					if (msg.targetMessage().toUpperCase().indexOf("SHAZAM") >= 0)
						if (mob.curState().adjHunger(50,
								mob.maxState().maxHunger(mob.baseWeight())))
							mob.tell("You are full.");
						else
							mob.tell("You feel nourished.");
				return;
			default:
				break;
			}
		}
		super.executeMsg(myHost, msg);
	}
}