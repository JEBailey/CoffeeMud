package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;

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
public class AutoMelee extends StdCommand {
	public AutoMelee() {
	}

	private final String[] access = { "AUTOMELEE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!CMath.bset(mob.getBitmap(), MOB.ATT_AUTOMELEE)) {
			mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_AUTOMELEE));
			mob.tell("Automelee has been turned off.  You will no longer charge into melee combat from a ranged position.");
			if (mob.isMonster())
				CMLib.commands().postSay(mob, null,
						"I will no longer charge into melee.", false, false);
		} else {
			mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_AUTOMELEE));
			mob.tell("Automelee has been turned back on.  You will now enter melee combat normally.");
			if (mob.isMonster())
				CMLib.commands()
						.postSay(mob, null,
								"I will now enter melee combat normally.",
								false, false);
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}
}