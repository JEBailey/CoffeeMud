package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.PlayerLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;

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
public class WizList extends StdCommand {
	public WizList() {
	}

	private final String[] access = { "WIZLIST" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		StringBuffer head = new StringBuffer("");
		boolean isArchonLooker = CMSecurity.isASysOp(mob);
		head.append("^x[");
		head.append(CMStrings.padRight("Class", 16) + " ");
		head.append(CMStrings.padRight("Race", 8) + " ");
		head.append(CMStrings.padRight("Lvl", 4) + " ");
		if (isArchonLooker)
			head.append(CMStrings.padRight("Last", 18) + " ");
		head.append("] Character Name^.^?\n\r");
		mob.tell("^x["
				+ CMStrings.centerPreserve(
						"The Administrators of "
								+ CMProps.getVar(CMProps.Str.MUDNAME),
						head.length() - 10) + "]^.^?");
		java.util.List<PlayerLibrary.ThinPlayer> allUsers = CMLib.database()
				.getExtendedUserList();
		String mask = CMProps.getVar(CMProps.Str.WIZLISTMASK);
		if (mask.length() == 0)
			mask = "-ANYCLASS +Archon";
		MaskingLibrary.CompiledZapperMask compiledMask = CMLib.masking()
				.maskCompile(mask);
		for (PlayerLibrary.ThinPlayer U : allUsers) {
			CharClass C;
			MOB player = CMLib.players().getPlayer(U.name);
			if (player != null)
				C = player.charStats().getCurrentClass();
			else
				C = CMClass.getCharClass(U.charClass);
			if (C == null)
				C = CMClass.findCharClass(U.charClass);
			if (((player != null) && (CMLib.masking().maskCheck(compiledMask,
					player, true)))
					|| (CMLib.masking().maskCheck(compiledMask, U))) {
				head.append("[");
				if (C != null)
					head.append(CMStrings.padRight(C.name(), 16) + " ");
				else
					head.append(CMStrings.padRight("Unknown", 16) + " ");
				head.append(CMStrings.padRight(U.race, 8) + " ");
				if ((C == null) || (!C.leveless()))
					head.append(CMStrings.padRight("" + U.level, 4) + " ");
				else
					head.append(CMStrings.padRight("    ", 4) + " ");
				if (isArchonLooker)
					head.append(CMStrings.padRight(
							CMLib.time().date2String(U.last), 18)
							+ " ");
				head.append("] " + U.name);
				head.append("\n\r");
			}
		}
		mob.tell(head.toString());
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
