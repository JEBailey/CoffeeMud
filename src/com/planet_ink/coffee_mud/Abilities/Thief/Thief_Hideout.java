package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor.Expire;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor.Move;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Thief_Hideout extends ThiefSkill {
	public String ID() {
		return "Thief_Hideout";
	}

	public String name() {
		return "Hideout";
	}

	public String displayText() {
		return "(In your hideout)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "HIDEOUT" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STREETSMARTS;
	}

	public Room previousLocation = null;
	public Room shelter = null;

	public Room getPreviousLocation(MOB mob) {
		if (previousLocation == null) {
			if (text().length() > 0)
				previousLocation = CMLib.map().getRoom(text());
			while ((previousLocation == null)
					|| (!CMLib.flags().canAccess(mob, previousLocation)))
				previousLocation = CMLib.map().getRandomRoom();
		}
		return previousLocation;
	}

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB M = (MOB) affected;

		if (canBeUninvoked()) {
			if (shelter == null)
				shelter = M.location();
			Room backToRoom = M.getStartRoom();
			int i = 0;
			LinkedList<MOB> mobs = new LinkedList<MOB>();
			for (Enumeration<MOB> m = shelter.inhabitants(); m
					.hasMoreElements();)
				mobs.add(m.nextElement());
			for (MOB mob : mobs) {
				if (mob == null)
					break;
				mob.tell("You slip back onto the streets.");

				CMMsg enterMsg = CMClass.getMsg(mob, previousLocation, null,
						CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null,
						CMMsg.MSG_ENTER, "<S-NAME> walk(s) in out of nowhere.");
				backToRoom = getPreviousLocation(mob);
				if (backToRoom == null)
					backToRoom = mob.getStartRoom();
				backToRoom.bringMobHere(mob, false);
				backToRoom.send(mob, enterMsg);
				CMLib.commands().postLook(mob, true);
			}
			LinkedList<Item> items = new LinkedList<Item>();
			for (Enumeration<Item> e = shelter.items(); e.hasMoreElements();)
				items.add(e.nextElement());
			for (Item I : items) {
				if (I.container() == null)
					backToRoom
							.moveItemTo(I, Expire.Player_Drop, Move.Followers);
			}
			i = 0;
			while (i < shelter.numItems()) {
				Item I = shelter.getItem(i);
				backToRoom.moveItemTo(I, Expire.Player_Drop, Move.Followers);
				if (shelter.isContent(I))
					i++;
			}
			shelter = null;
			previousLocation = null;
		}
		super.unInvoke();
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (((msg.sourceMinor() == CMMsg.TYP_QUIT)
				|| (msg.sourceMinor() == CMMsg.TYP_SHUTDOWN)
				|| ((msg.targetMinor() == CMMsg.TYP_EXPIRE) && (msg.target() == shelter)) || (msg
				.sourceMinor() == CMMsg.TYP_ROOMRESET))
				&& (shelter != null)
				&& (shelter.isInhabitant(msg.source()))) {
			getPreviousLocation(msg.source()).bringMobHere(msg.source(), false);
			unInvoke();
		} else if (((msg.sourceMinor() == CMMsg.TYP_LEAVE) && (msg.target() == shelter))
				|| (msg.sourceMinor() == CMMsg.TYP_RECALL)) {
			getPreviousLocation(msg.source()).bringMobHere(msg.source(), false);
			unInvoke();
			return false;
		}
		return super.okMessage(host, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		if (mob.fetchEffect(ID()) != null) {
			mob.fetchEffect(ID()).unInvoke();
			return false;
		}

		Room thisRoom = mob.location();
		if (thisRoom.domainType() != Room.DOMAIN_OUTDOORS_CITY) {
			mob.tell("You must be on the streets to enter your hideout.");
			return false;
		}
		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags().plus(
				TrackingLibrary.TrackingFlag.NOEMPTYGRIDS).plus(
				TrackingLibrary.TrackingFlag.NOAIR);
		List<Room> nearbyRooms = CMLib.tracking().getRadiantRooms(thisRoom,
				flags, 2);
		for (Room room : nearbyRooms) {
			switch (room.domainType()) {
			case Room.DOMAIN_INDOORS_STONE:
			case Room.DOMAIN_INDOORS_METAL:
			case Room.DOMAIN_INDOORS_WOOD:
			case Room.DOMAIN_OUTDOORS_CITY:
				break;
			default:
				mob.tell("You must be deep in an urban area to enter your hideout.");
				return false;
			}
		}

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			previousLocation = thisRoom;
			shelter = CMClass.getLocale("HideoutShelter");
			Exit E = CMClass.getExit("OpenDescriptable");
			E.setDisplayText("The way back to " + thisRoom.displayText(mob));
			int dir = CMLib.dice().roll(1, 4, -1);
			shelter.setRawExit(dir, E);
			shelter.rawDoors()[dir] = thisRoom;
			Room newRoom = shelter;
			shelter.setArea(mob.location().getArea());
			miscText = CMLib.map().getExtendedRoomID(thisRoom);

			CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_THIEF_ACT,
					auto ? "" : "<S-NAME> slip(s) away.");
			CMMsg enterMsg = CMClass.getMsg(mob, newRoom, null,
					CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null,
					CMMsg.MSG_ENTER, "<S-NAME> duck(s) into the hideout.");
			if (thisRoom.okMessage(mob, msg)
					&& newRoom.okMessage(mob, enterMsg)) {
				if (mob.isInCombat()) {
					CMLib.commands().postFlee(mob, ("NOWHERE"));
					mob.makePeace();
				}
				thisRoom.send(mob, msg);
				newRoom.bringMobHere(mob, false);
				thisRoom.delInhabitant(mob);
				newRoom.send(mob, enterMsg);
				mob.tell("\n\r\n\r");
				CMLib.commands().postLook(mob, true);
				beneficialAffect(mob, mob, asLevel, 999999);
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> attemp(s) to slip away, and fail(s).");

		return success;
	}
}
