package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Dance_CanCan extends Dance {
	public String ID() {
		return "Dance_CanCan";
	}

	public String name() {
		return "Can-Can";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	public static Ability kick = null;

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;

		MOB mob = (MOB) affected;
		if (mob == null)
			return false;

		if (mob.isInCombat()) {
			if (kick == null) {
				kick = CMClass.getAbility("Fighter_Kick");
				kick.setProficiency(100);
			}
			int oldMana = mob.curState().getMana();
			kick.invoke(mob, mob.getVictim(), false,
					adjustedLevel(invoker(), 0));
			mob.curState().setMana(oldMana);
		}
		return true;
	}

}
