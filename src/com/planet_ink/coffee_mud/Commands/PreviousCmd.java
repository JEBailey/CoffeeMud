package com.planet_ink.coffee_mud.Commands;

import java.util.List;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PreviousCmd extends StdCommand {
	public PreviousCmd() {
	}

	private final String[] access = { "!" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!mob.isMonster()) {
			mob.enqueCommand((List) CMParms.copyFlattenVector(mob.session()
					.getPreviousCMD()), metaFlags, 0);
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
