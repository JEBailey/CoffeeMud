package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Replay extends StdCommand {
	public Replay() {
	}

	private final String[] access = { "REPLAY" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!mob.isMonster()) {
			Session S = mob.session();
			int num = Session.MAX_PREVMSGS;
			if (commands.size() > 1)
				num = CMath.s_int(CMParms.combine(commands, 1));
			if (num <= 0)
				return false;
			java.util.List<String> last = S.getLastMsgs();
			if (num > last.size())
				num = last.size();
			for (int v = last.size() - num; v < last.size(); v++)
				S.onlyPrint((last.get(v)) + "\n\r", true);
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}