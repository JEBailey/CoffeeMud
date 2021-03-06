package com.planet_ink.coffee_mud.Commands;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;

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
public class Transfer extends At {
	public Transfer() {
	}

	private final String[] access = { "TRANSFER" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Room room = null;
		if (commands.size() < 3) {
			mob.tell("Transfer whom where? Try all or a mob name, followerd by a Room ID, target player name, area name, or room text!");
			return false;
		}
		commands.removeElementAt(0);
		String mobname = (String) commands.elementAt(0);
		Room curRoom = mob.location();
		Vector V = new Vector();
		boolean allFlag = false;
		if (mobname.equalsIgnoreCase("ALL")) {
			allFlag = true;
			if (commands.size() > 2) {
				commands.removeElementAt(0);
				mobname = (String) commands.elementAt(0);
			} else
				mobname = "";
		}
		boolean itemFlag = false;
		if ((mobname.equalsIgnoreCase("item") || (mobname
				.equalsIgnoreCase("items")))) {
			itemFlag = true;
			if (commands.size() > 2) {
				commands.removeElementAt(0);
				mobname = (String) commands.elementAt(0);
			} else
				mobname = "";
		}
		if ((mobname.length() == 0) && (allFlag)) {
			if (itemFlag)
				for (int i = 0; i < curRoom.numItems(); i++)
					V.addElement(curRoom.getItem(i));
			else
				for (int i = 0; i < curRoom.numInhabitants(); i++) {
					MOB M = curRoom.fetchInhabitant(i);
					if (M != null)
						V.addElement(M);
				}
		} else if (itemFlag) {
			if (!allFlag) {
				Environmental E = curRoom.fetchFromMOBRoomFavorsItems(mob,
						null, mobname, Wearable.FILTER_UNWORNONLY);
				if (E instanceof Item)
					V.addElement(E);
			} else if (mobname.length() > 0) {
				for (int i = 0; i < curRoom.numItems(); i++) {
					Item I = curRoom.getItem(i);
					if ((I != null)
							&& (CMLib.english().containsString(I.name(),
									mobname)))
						V.addElement(I);
				}
			}
		} else {
			if (!allFlag) {
				MOB M = CMLib.sessions().findPlayerOnline(mobname, true);
				if (M != null)
					V.add(M);
			}
			if (V.size() == 0)
				for (Enumeration<Room> r = mob.location().getArea()
						.getProperMap(); r.hasMoreElements();) {
					Room R = r.nextElement();
					MOB M = null;
					int num = 1;
					while ((num <= 1) || (M != null)) {
						M = R.fetchInhabitant(mobname + "." + num);
						if ((M != null) && (!V.contains(M)))
							V.addElement(M);
						num++;
						if ((!allFlag) && (V.size() > 0))
							break;
					}
					if ((!allFlag) && (V.size() > 0))
						break;
				}
			if (V.size() == 0) {
				try {
					for (Enumeration r = CMLib.map().rooms(); r
							.hasMoreElements();) {
						Room R = (Room) r.nextElement();
						MOB M = null;
						int num = 1;
						while ((num <= 1) || (M != null)) {
							M = R.fetchInhabitant(mobname + "." + num);
							if ((M != null) && (!V.contains(M)))
								V.addElement(M);
							num++;
							if ((!allFlag) && (V.size() > 0))
								break;
						}
						if ((!allFlag) && (V.size() > 0))
							break;
					}
				} catch (NoSuchElementException nse) {
				}
			}
		}

		if (V.size() == 0) {
			mob.tell("Transfer what?  '" + mobname + "' is unknown to you.");
			return false;
		}

		StringBuffer cmd = new StringBuffer(CMParms.combine(commands, 1));
		if (cmd.toString().equalsIgnoreCase("here")
				|| cmd.toString().equalsIgnoreCase("."))
			room = mob.location();
		else if (Directions.getDirectionCode(cmd.toString()) >= 0)
			room = mob.location().getRoomInDir(
					Directions.getDirectionCode(cmd.toString()));
		else
			room = CMLib.map().findWorldRoomLiberally(mob, cmd.toString(),
					"RIPME", 100, 120000);

		if (room == null) {
			mob.tell("Transfer where? '"
					+ cmd.toString()
					+ "' is unknown.  Enter a Room ID, player name, area name, or room text!");
			return false;
		}
		for (int i = 0; i < V.size(); i++)
			if (V.elementAt(i) instanceof Item) {
				Item I = (Item) V.elementAt(i);
				Room itemRoom = CMLib.map().roomLocation(I);
				if ((itemRoom != null)
						&& (!room.isContent(I))
						&& (CMSecurity.isAllowed(mob, itemRoom,
								CMSecurity.SecFlag.TRANSFER))
						&& (CMSecurity.isAllowed(mob, room,
								CMSecurity.SecFlag.TRANSFER)))
					room.moveItemTo(I, ItemPossessor.Expire.Never,
							ItemPossessor.Move.Followers);
			} else if (V.elementAt(i) instanceof MOB) {
				MOB M = (MOB) V.elementAt(i);
				Room mobRoom = CMLib.map().roomLocation(M);
				if ((mobRoom != null)
						&& (!room.isInhabitant(M))
						&& (CMSecurity.isAllowed(mob, mobRoom,
								CMSecurity.SecFlag.TRANSFER))
						&& (CMSecurity.isAllowed(mob, room,
								CMSecurity.SecFlag.TRANSFER))) {
					if ((mob.playerStats().tranPoofOut().length() > 0)
							&& (mob.location() != null))
						M.location().show(mob, M, CMMsg.MSG_OK_VISUAL,
								mob.playerStats().tranPoofOut());
					room.bringMobHere(M, true);
					if (mob.playerStats().tranPoofIn().length() > 0)
						room.showOthers(mob, M, CMMsg.MSG_OK_VISUAL, mob
								.playerStats().tranPoofIn());
					if (!M.isMonster())
						CMLib.commands().postLook(M, true);
				}
			}
		if (mob.playerStats().tranPoofOut().length() == 0)
			mob.tell("Done.");
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowed(mob, mob.location(),
				CMSecurity.SecFlag.TRANSFER);
	}

}
