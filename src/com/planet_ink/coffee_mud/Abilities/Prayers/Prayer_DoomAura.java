package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;

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

public class Prayer_DoomAura extends Prayer_BladeBarrier {
	public String ID() {
		return "Prayer_DoomAura";
	}

	public String name() {
		return "Doom Aura";
	}

	public String displayText() {
		return "(Doom Aura)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	protected String startStr() {
		return "An aura of doom appears around <T-NAME>!^?";
	}

	protected void doDamage(MOB srcM, MOB targetM, int damage) {
		CMLib.combat().postDamage(srcM, targetM, this, damage,
				CMMsg.TYP_UNDEAD | CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS,
				Weapon.TYPE_BURNING,
				"The aura of doom around <S-NAME> <DAMAGE> <T-NAME>.");
	}
}
