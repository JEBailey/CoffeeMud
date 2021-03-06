package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class TypeCmd extends Go {
	public TypeCmd() {
	}

	private final String[] access = { "TYPE", "=" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		final Room R = mob.location();
		boolean consoleMode = (mob.riding() instanceof Electronics.Computer);
		if ((commands.size() <= 1) || (R == null)) {
			if (consoleMode)
				mob.tell("Type what into this console?  Have you read the screen?");
			else
				mob.tell("Type what into what?");
			return false;
		}
		Environmental typeIntoThis = (consoleMode) ? mob.riding() : null;
		if (typeIntoThis == null) {
			int x = 1;
			while ((x < commands.size())
					&& (!commands.get(x).toString().equalsIgnoreCase("into")))
				x++;
			if (x < commands.size() - 1) {
				String typeWhere = CMParms.combine(commands, x + 1);
				typeIntoThis = mob.location().fetchFromMOBRoomFavorsItems(mob,
						null, typeWhere, Wearable.FILTER_ANY);
				if (typeIntoThis == null)
					for (int i = 0; i < R.numItems(); i++) {
						Item I = R.getItem(i);
						if ((I instanceof Electronics.ElecPanel)
								&& (((Electronics.ElecPanel) I).isOpen())) {
							typeIntoThis = R.fetchFromRoomFavorItems(I,
									typeWhere);
							if (typeIntoThis != null)
								break;
						}
					}
				if (typeIntoThis != null) {
					while (commands.size() > x)
						commands.remove(commands.size() - 1);
				} else {
					mob.tell("You don't see '" + typeWhere.toLowerCase()
							+ "' here.");
				}
			}
		}

		String enterWhat = CMParms.combine(commands, 1);
		if (typeIntoThis != null) {
			String enterStr = "^W<S-NAME> enter(s) '" + enterWhat
					+ "' into <T-NAME>.^?";
			CMMsg msg = CMClass.getMsg(mob, typeIntoThis, null,
					CMMsg.MSG_WRITE, enterStr, CMMsg.MSG_WRITE, enterWhat,
					CMMsg.MSG_WRITE, null);
			if (mob.location().okMessage(mob, msg))
				mob.location().send(mob, msg);
			return true;
		} else {
			mob.tell("You don't see '" + enterWhat.toLowerCase() + "' here.");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}
}
