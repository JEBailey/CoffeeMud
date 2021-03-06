package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Software;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Activate extends StdCommand {
	public Activate() {
	}

	private final String[] access = { "ACTIVATE", "ACT", "A", ">" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Room R = mob.location();
		if ((commands.size() < 2) || (R == null)) {
			mob.tell("Activate what?");
			return false;
		}
		commands.removeElementAt(0);
		String what = (String) commands.lastElement();
		String whole = CMParms.combine(commands, 0);
		Item item = null;
		Environmental E = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
				whole, Wearable.FILTER_ANY);
		if ((!(E instanceof Electronics)) || (E instanceof Software))
			E = null;
		if (E == null)
			for (int i = 0; i < R.numItems(); i++) {
				Item I = R.getItem(i);
				if ((I instanceof Electronics.ElecPanel)
						&& (((Electronics.ElecPanel) I).isOpen())) {
					E = R.fetchFromRoomFavorItems(I, whole);
					if ((E instanceof Electronics)
							&& (!(E instanceof Software)))
						break;
				}
			}
		if ((!(E instanceof Electronics)) || (E instanceof Software))
			E = null;
		else {
			item = (Item) E;
			commands.clear();
		}
		if (E == null) {
			E = mob.location().fetchFromMOBRoomFavorsItems(mob, null, what,
					Wearable.FILTER_ANY);
			if ((!(E instanceof Electronics)) || (E instanceof Software))
				E = null;
			if (E == null)
				for (int i = 0; i < R.numItems(); i++) {
					Item I = R.getItem(i);
					if ((I instanceof Electronics.ElecPanel)
							&& (((Electronics.ElecPanel) I).isOpen())) {
						E = R.fetchFromRoomFavorItems(I, what);
						if ((E instanceof Electronics)
								&& (!(E instanceof Software)))
							break;
					}
				}
			if ((!(E instanceof Electronics)) || (E instanceof Software))
				E = null;
			if ((E == null) && (mob.riding() instanceof Electronics.Computer)) {
				E = mob.riding();
				item = (Item) E;
			} else {
				item = (Item) E;
				commands.removeElementAt(commands.size() - 1);
			}
		}
		if (E == null) {
			mob.tell("You don't see anything called '" + what + "' or '"
					+ whole + "' here that you can activate.");
			return false;
		} else if (item == null) {
			mob.tell("You can't activate " + E.name() + ".");
			return false;
		}

		String rest = CMParms.combine(commands, 0);
		CMMsg newMsg = CMClass.getMsg(mob, item, null, CMMsg.MSG_ACTIVATE,
				null, CMMsg.MSG_ACTIVATE, (rest.length() == 0) ? null : rest,
				CMMsg.MSG_ACTIVATE, null);
		if (R.okMessage(mob, newMsg))
			R.send(mob, newMsg);
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return true;
	}
}
