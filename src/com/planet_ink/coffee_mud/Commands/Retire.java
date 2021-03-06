package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Log;

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
public class Retire extends StdCommand {
	public Retire() {
	}

	private final String[] access = { "RETIRE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(final MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		final Session session = mob.session();
		if (session == null)
			return false;
		final PlayerStats pstats = mob.playerStats();
		if (pstats == null)
			return false;

		mob.tell("^HThis will delete your player from the system FOREVER!");
		session.prompt(new InputCallback(InputCallback.Type.PROMPT, "", 120000) {
			@Override
			public void showPrompt() {
				session.promptPrint("If that's what you want, re-enter your password: ");
			}

			@Override
			public void timedOut() {
			}

			@Override
			public void callBack() {
				if (input.trim().length() == 0)
					return;
				if (!pstats.matchesPassword(input.trim()))
					mob.tell("Password incorrect.");
				else {
					if (CMSecurity.isDisabled(CMSecurity.DisFlag.RETIREREASON)) {
						Log.sysOut("Retire", "Retired: " + mob.Name());
						CMLib.players().obliteratePlayer(mob, true, false);
						session.logout(true);
					} else
						session.prompt(new InputCallback(
								InputCallback.Type.PROMPT, "") {
							@Override
							public void showPrompt() {
								session.promptPrint("OK.  Please leave us a short message as to why you are deleting this"
										+ " character.  Your answers will be kept confidential, "
										+ "and are for administrative purposes only.\n\r: ");
							}

							@Override
							public void timedOut() {
							}

							@Override
							public void callBack() {
								Log.sysOut("Retire", "Retired: " + mob.Name()
										+ ": " + this.input);
								CMLib.players().obliteratePlayer(mob, true,
										false);
								session.logout(true);
							}
						});
				}
			}
		});
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return false;
	}

}
