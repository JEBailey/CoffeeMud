package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Quiet extends StdCommand {
	public Quiet() {
	}

	private final String[] access = { "QUIET" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!CMath.bset(mob.getBitmap(), MOB.ATT_QUIET)) {
			mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_QUIET));
			mob.tell("Quiet mode is now on.  You will no longer receive channel messages or tells.");
		} else {
			mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_QUIET));
			mob.tell("Quiet mode is now off.  You may now receive channel messages and tells.");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
