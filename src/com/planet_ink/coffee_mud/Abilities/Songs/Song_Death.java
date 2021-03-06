package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Song_Death extends Song {
	public String ID() {
		return "Song_Death";
	}

	public String name() {
		return "Death";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int getXMAXRANGELevel(MOB mob) {
		return 0;
	} // people are complaining about multi-room death

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;

		MOB mob = (MOB) affected;
		if (mob == null)
			return false;
		if (mob == invoker)
			return true;
		if (invoker == null)
			return false;

		int hpLoss = (int) Math.round(Math.floor(mob.curState().getHitPoints()
				* (0.07 + (0.02 * (1 + super.getXLEVELLevel(invoker()))))));
		CMLib.combat().postDamage(invoker, mob, this, hpLoss,
				CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_BURSTING,
				"^SThe painful song <DAMAGE> <T-NAME>!^?");
		return true;
	}

}
