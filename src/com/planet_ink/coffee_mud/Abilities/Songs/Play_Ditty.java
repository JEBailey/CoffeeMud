package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class Play_Ditty extends Play {
	public String ID() {
		return "Play_Ditty";
	}

	public String name() {
		return "Ditty";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	protected String songOf() {
		return "a " + name();
	}

	public long flags() {
		return Ability.FLAG_HEALINGMAGIC;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((affected != null) && (affected instanceof MOB)
				&& (invoker() != null)) {
			MOB mob = (MOB) affected;
			int healing = 4 + (int) Math.round(CMath.mul(
					adjustedLevel(invoker(), 0), 0.25));
			CMLib.combat().postHealing(invoker(), mob, this,
					CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, healing, null);
		}
		return true;
	}
}
