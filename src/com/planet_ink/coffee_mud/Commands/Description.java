package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

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
public class Description extends StdCommand {
	public Description() {
	}

	private final String[] access = { "DESCRIPTION" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("^xYour current description:^?\n\r" + mob.description());
			mob.tell("\n\rEnter DESCRIPTION [NEW TEXT] to change.");
			return false;
		}
		String s = CMParms.combine(commands, 1);
		if (s.length() > 255)
			mob.tell("Your description exceeds 255 characters in length.  Please re-enter a shorter one.");
		else {
			mob.setDescription(s);
			mob.tell("Your description has been changed.");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
