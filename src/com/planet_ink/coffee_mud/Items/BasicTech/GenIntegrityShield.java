package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMStrings;

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
public class GenIntegrityShield extends GenPersonalShield {
	public String ID() {
		return "GenIntegrityShield";
	}

	public GenIntegrityShield() {
		super();
		setName("an integrity shield generator");
		setDisplayText("an integrity shield generator sits here.");
		setDescription("The integrity shield generator is worn about the body and activated to use. It protects against disruption and disintegration beams. ");
	}

	@Override
	protected String fieldOnStr(MOB viewerM) {
		return (owner() instanceof MOB) ? "An integrity field surrounds <O-NAME>."
				: "An integrity field surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM) {
		return (owner() instanceof MOB) ? "The integrity field around <O-NAME> flickers and dies out."
				: "The integrity field around <T-NAME> flickers and dies out.";
	}

	@Override
	protected boolean doShield(MOB mob, CMMsg msg, double successFactor) {
		if (mob.location() != null) {
			if (msg.tool() instanceof Weapon) {
				String s = "^F" + ((Weapon) msg.tool()).hitString(0) + "^N";
				if (s.indexOf("<DAMAGE> <T-HIM-HER>") > 0)
					mob.location().show(
							msg.source(),
							msg.target(),
							msg.tool(),
							CMMsg.MSG_OK_VISUAL,
							CMStrings.replaceAll(s, "<DAMAGE>",
									"it is neutralized by the shield around"));
				else if (s.indexOf("<DAMAGES> <T-HIM-HER>") > 0)
					mob.location().show(
							msg.source(),
							msg.target(),
							msg.tool(),
							CMMsg.MSG_OK_VISUAL,
							CMStrings.replaceAll(s, "<DAMAGES>",
									"is neutralized by the shield around"));
				else
					mob.location()
							.show(mob, msg.source(), msg.tool(),
									CMMsg.MSG_OK_VISUAL,
									"The field around <S-NAME> neutralizes the <O-NAMENOART> damage.");
			} else
				mob.location()
						.show(mob, msg.source(), msg.tool(),
								CMMsg.MSG_OK_VISUAL,
								"The field around <S-NAME> neutralizes the <O-NAMENOART> damage.");
		}
		return false;
	}

	@Override
	protected boolean doesShield(MOB mob, CMMsg msg, double successFactor) {
		if (!activated())
			return false;
		if ((msg.tool() instanceof Electronics)
				&& (msg.tool() instanceof Weapon)
				&& (Math.random() >= successFactor)
				&& ((((Weapon) msg.tool()).weaponType() == Weapon.TYPE_MELTING) || (((Weapon) msg
						.tool()).weaponType() == Weapon.TYPE_SLASHING))) {
			return true;
		}
		return false;
	}
}
