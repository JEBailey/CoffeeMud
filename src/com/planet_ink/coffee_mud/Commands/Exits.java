package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;

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
public class Exits extends StdCommand {
	public Exits() {
	}

	private final String[] access = { "EXITS", "EX" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		final Room R = mob.location();
		if (R != null) {
			CMMsg exitMsg = CMClass.getMsg(mob, R, null, CMMsg.MSG_LOOK_EXITS,
					null);
			if ((commands != null)
					&& (commands.size() > 1)
					&& (commands.lastElement() instanceof String)
					&& (((String) commands.lastElement())
							.equalsIgnoreCase("SHORT")))
				exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
			if (R.okMessage(mob, exitMsg))
				R.send(mob, exitMsg);
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}