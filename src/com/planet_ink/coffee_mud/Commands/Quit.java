package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Quit extends StdCommand {
	public Quit() {
	}

	private final String[] access = { "QUIT", "QUI", "Q" };

	public String[] getAccessWords() {
		return access;
	}

	public static void dispossess(MOB mob, boolean force) {
		if (mob.soulMate() == null) {
			mob.tell("Huh?");
			return;
		}
		CMMsg msg = CMClass
				.getMsg(mob, CMMsg.MSG_DISPOSSESS,
						"^H<S-YOUPOSS> spirit has returned to <S-YOUPOSS> body...\n\r\n\r^N");
		final Room room = mob.location();
		if ((room == null) || (room.okMessage(mob, msg)) || force) {
			if (room != null)
				room.send(mob, msg);
			mob.dispossess(true);
		}
	}

	public boolean execute(final MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (mob.soulMate() != null)
			dispossess(mob, CMParms.combine(commands).endsWith("!"));
		else if (!mob.isMonster()) {
			final Session session = mob.session();
			if (session != null) {
				if ((session.getLastPKFight() > 0)
						&& ((System.currentTimeMillis() - session
								.getLastPKFight()) < (5 * 60 * 1000))) {
					mob.tell("You must wait a few more minutes before you are allowed to quit.");
					return false;
				}
				session.prompt(new InputCallback(InputCallback.Type.CONFIRM,
						"N", 30000) {
					@Override
					public void showPrompt() {
						session.promptPrint("\n\rQuit -- are you sure (y/N)?");
					}

					@Override
					public void timedOut() {
					}

					@Override
					public void callBack() {
						if (this.confirmed) {
							CMMsg msg = CMClass.getMsg(mob, null,
									CMMsg.MSG_QUIT, null);
							Room R = mob.location();
							if ((R != null) && (R.okMessage(mob, msg))) {
								CMLib.map().sendGlobalMessage(
										mob,
										CMMsg.TYP_QUIT,
										CMClass.getMsg(mob, null,
												CMMsg.MSG_QUIT, null));
								session.stopSession(false, false, false);
								CMLib.commands().monitorGlobalMessage(R, msg);
							}
						}
					}
				});
			}
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
