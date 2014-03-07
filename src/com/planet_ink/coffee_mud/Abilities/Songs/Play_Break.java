package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
@SuppressWarnings("rawtypes")
public class Play_Break extends Play {
	public String ID() {
		return "Play_Break";
	}

	public String name() {
		return "Break";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected boolean skipStandardSongInvoke() {
		return true;
	}

	public Play_Break() {
		super();
		setProficiency(100);
	}

	public void setProficiency(int newProficiency) {
		super.setProficiency(100);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		boolean foundOne = false;
		for (int a = 0; a < mob.numEffects(); a++) // personal affects
		{
			Ability A = mob.fetchEffect(a);
			if ((A != null) && (A instanceof Play))
				foundOne = true;
		}
		if (!foundOne) {
			mob.tell(auto ? "There is noone playing."
					: "You aren't playing anything.");
			return true;
		}
		unplayAll(mob, mob);
		mob.location().show(mob, null, CMMsg.MSG_NOISE,
				auto ? "Silence." : "<S-NAME> stop(s) playing.");
		mob.location().recoverRoomStats();
		return true;
	}
}