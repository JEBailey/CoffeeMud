package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;

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
public class Prompt extends StdCommand {
	public Prompt() {
	}

	private final String[] access = { "PROMPT" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (mob.session() == null)
			return false;
		PlayerStats pstats = mob.playerStats();
		Session sess = mob.session();
		if (pstats == null)
			return false;

		if (commands.size() == 1)
			sess.rawPrintln("Your prompt is currently set at:\n\r"
					+ pstats.getPrompt());
		else {
			String str = CMParms.combine(commands, 1);
			if (("DEFAULT").startsWith(str.toUpperCase()))
				pstats.setPrompt("");
			else if (sess.confirm("Change your prompt to: " + str
					+ ", are you sure (Y/n)?", "Y")) {
				pstats.setPrompt(str);
				sess.rawPrintln("Your prompt is currently now set at:\n\r"
						+ pstats.getPrompt());
			}
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
