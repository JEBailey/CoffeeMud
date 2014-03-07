package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class SysMsgs extends StdCommand {
	public SysMsgs() {
	}

	private final String[] access = { "SYSMSGS" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (CMath.bset(mob.getBitmap(), MOB.ATT_SYSOPMSGS))
			mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_SYSOPMSGS));
		else
			mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_SYSOPMSGS));
		mob.tell("Extended messages are now : "
				+ ((CMath.bset(mob.getBitmap(), MOB.ATT_SYSOPMSGS)) ? "ON"
						: "OFF"));
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowed(mob, mob.location(),
				CMSecurity.SecFlag.SYSMSGS);
	}

}