package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

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
public class PageBreak extends StdCommand {
	public PageBreak() {
	}

	private final String[] access = { "PAGEBREAK" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if ((mob == null) || (mob.playerStats() == null))
			return false;

		if (commands.size() < 2) {
			String pageBreak = (mob.playerStats().getPageBreak() != 0) ? ("" + mob
					.playerStats().getPageBreak()) : "Disabled";
			mob.tell("Change your page break to what? Your current page break setting is: "
					+ pageBreak
					+ ". Enter a number larger than 0 or 'disable'.");
			return false;
		}
		String newBreak = CMParms.combine(commands, 1);
		int newVal = mob.playerStats().getWrap();
		if ((CMath.isInteger(newBreak)) && (CMath.s_int(newBreak) > 0))
			newVal = CMath.s_int(newBreak);
		else if ("DISABLED".startsWith(newBreak.toUpperCase()))
			newVal = 0;
		else {
			mob.tell("'"
					+ newBreak
					+ "' is not a valid setting. Enter a number larger than 0 or 'disable'.");
			return false;
		}
		mob.playerStats().setPageBreak(newVal);
		String pageBreak = (mob.playerStats().getPageBreak() != 0) ? ("" + mob
				.playerStats().getPageBreak()) : "Disabled";
		mob.tell("Your new page break setting is: " + pageBreak + ".");
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
