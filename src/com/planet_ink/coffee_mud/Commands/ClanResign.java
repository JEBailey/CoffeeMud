package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.ScriptingEngine;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class ClanResign extends StdCommand {
	public ClanResign() {
	}

	private final String[] access = { "CLANRESIGN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(final MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String clanName = (commands.size() > 1) ? CMParms.combine(commands, 1,
				commands.size()) : "";

		Clan chkC = null;
		for (Pair<Clan, Integer> c : mob.clans())
			if ((clanName.length() == 0)
					|| (CMLib.english().containsString(c.first.getName(),
							clanName))) {
				chkC = c.first;
				break;
			}

		final Session S = mob.session();
		final Clan C = chkC;
		if (C == null) {
			mob.tell("You can't resign from "
					+ ((clanName.length() == 0) ? "anything" : clanName) + ".");
		} else if (S != null) {
			S.prompt(new InputCallback(InputCallback.Type.CHOOSE, "N", "YN", 0) {
				@Override
				public void showPrompt() {
					S.promptPrint("Resign from " + C.getName()
							+ ".  Are you absolutely SURE (y/N)?");
				}

				@Override
				public void timedOut() {
				}

				@Override
				public void callBack() {
					String check = this.input;
					if (check.equalsIgnoreCase("Y")) {
						if (C.getGovernment().getExitScript().trim().length() > 0) {
							Pair<Clan, Integer> curClanRole = mob.getClanRole(C
									.clanID());
							if (curClanRole != null)
								mob.setClan(C.clanID(),
										curClanRole.second.intValue());
							ScriptingEngine S = (ScriptingEngine) CMClass
									.getCommon("DefaultScriptingEngine");
							S.setSavable(false);
							S.setVarScope("*");
							S.setScript(C.getGovernment().getExitScript());
							CMMsg msg2 = CMClass
									.getMsg(mob, mob, null,
											CMMsg.MSG_OK_VISUAL, null, null,
											"CLANEXIT");
							S.executeMsg(mob, msg2);
							S.dequeResponses();
							S.tick(mob, Tickable.TICKID_MOB);
						}
						CMLib.clans().clanAnnounce(
								mob,
								"Member resigned from " + C.getGovernmentName()
										+ " " + C.name() + ": " + mob.Name());
						C.delMember(mob);
					}
				}
			});
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}