package com.planet_ink.coffee_mud.Items.Basic;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.collections.EmptyIterator;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
import com.planet_ink.coffee_mud.core.interfaces.Rider;

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
@SuppressWarnings("unchecked")
public class StdPortal extends StdContainer implements Rideable, Exit {
	public String ID() {
		return "StdPortal";
	}

	public StdPortal() {
		super();
		setName("a portal");
		setDisplayText("a portal is here.");
		setDescription("It's difficult to see where it leads.  Try ENTER and find out!");
		basePhyStats().setWeight(10000);
		recoverPhyStats();
		capacity = 10000;
		material = RawMaterial.RESOURCE_NOTHING;
	}

	// common item/mob stuff
	public boolean isMobileRideBasis() {
		return false;
	}

	public int rideBasis() {
		return Rideable.RIDEABLE_ENTERIN;
	}

	public void setRideBasis(int basis) {
	}

	public int riderCapacity() {
		return 1;
	}

	public void setRiderCapacity(int newCapacity) {
	}

	public int numRiders() {
		return 0;
	}

	public Iterator<Rider> riders() {
		return EmptyIterator.INSTANCE;
	}

	public Rider fetchRider(int which) {
		return null;
	}

	public void addRider(Rider mob) {
	}

	public void delRider(Rider mob) {
	}

	public void recoverPhyStats() {
		CMLib.flags().setReadable(this, false);
		super.recoverPhyStats();
	}

	public Set<MOB> getRideBuddies(Set<MOB> list) {
		return list;
	}

	public boolean mobileRideBasis() {
		return false;
	}

	public String stateString(Rider R) {
		return "in";
	}

	public String putString(Rider R) {
		return "in";
	}

	public String mountString(int commandType, Rider R) {
		return "enter(s)";
	}

	public String dismountString(Rider R) {
		return "emerge(s) from";
	}

	public String stateStringSubject(Rider R) {
		return "occupied by";
	}

	public short exitUsage(short change) {
		return 0;
	}

	public String displayText() {
		return displayText;
	}

	public boolean amRiding(Rider mob) {
		return false;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		switch (msg.targetMinor()) {
		case CMMsg.TYP_DISMOUNT:
			if (msg.amITarget(this)) {
				// protects from standard item rejection
				return true;
			}
			break;
		case CMMsg.TYP_SIT:
			if (msg.amITarget(this)) {
				if (msg.sourceMessage().indexOf(
						mountString(CMMsg.TYP_SIT, msg.source())) > 0) {
					if (getDestinationRoom() == null) {
						msg.source().tell(
								"This portal is broken.. nowhere to go!");
						return false;
					}
					if (hasALid() && (!isOpen())) {
						msg.source().tell(name() + " is closed.");
						return false;
					}
					msg.modify(msg.source(), msg.target(), msg.tool(),
							msg.sourceMajor() | CMMsg.TYP_ENTER,
							msg.sourceMessage(), msg.targetMajor()
									| CMMsg.TYP_ENTER, msg.targetMessage(),
							msg.othersMajor() | CMMsg.TYP_ENTER, null);
					return true;
				}
				msg.source().tell("You cannot sit on " + name() + ".");
				return false;
			}
			break;
		case CMMsg.TYP_SLEEP:
			if (msg.amITarget(this)) {
				msg.source().tell("You cannot sleep on " + name() + ".");
				return false;
			}
			break;
		case CMMsg.TYP_MOUNT:
			if (msg.amITarget(this)) {
				msg.source()
						.tell("You cannot mount " + name() + ", try Enter.");
				return false;
			}
			break;
		}
		return true;
	}

	protected Room getDestinationRoom() {
		Room R = null;
		List<String> V = CMParms.parseSemicolons(readableText(), true);
		if (V.size() > 0)
			R = CMLib.map().getRoom(V.get(CMLib.dice().roll(1, V.size(), -1)));
		return R;
	}

	public Room lastRoomUsedFrom() {
		return getDestinationRoom();
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		switch (msg.targetMinor()) {
		case CMMsg.TYP_DISMOUNT:
			break;
		case CMMsg.TYP_ENTER:
			if (msg.amITarget(this)) {
				if (msg.sourceMessage().indexOf(
						mountString(CMMsg.TYP_SIT, msg.source())) > 0) {
					Room thisRoom = msg.source().location();
					Room R = getDestinationRoom();
					if (R == null)
						R = thisRoom;
					Exit E = CMClass.getExit("OpenNameable");
					E.setMiscText(name());
					synchronized (("GATE_" + CMLib.map()
							.getExtendedTwinRoomIDs(thisRoom, R)).intern()) {
						Exit oldE = thisRoom.getRawExit(Directions.GATE);
						Room oldR = thisRoom.rawDoors()[Directions.GATE];
						Exit oldE2 = R.getRawExit(Directions.GATE);
						thisRoom.rawDoors()[Directions.GATE] = R;
						thisRoom.setRawExit(Directions.GATE, E);
						Exit E2 = CMClass.getExit("OpenNameable");
						E2.basePhyStats().setDisposition(PhyStats.IS_NOT_SEEN);
						R.setRawExit(Directions.GATE, E2);
						CMLib.tracking().walk(msg.source(), Directions.GATE,
								false, false, false);
						thisRoom.rawDoors()[Directions.GATE] = oldR;
						thisRoom.setRawExit(Directions.GATE, oldE);
						R.setRawExit(Directions.GATE, oldE2);
						E.destroy();
						E2.destroy();
					}
				}
			}
			break;
		case CMMsg.TYP_LEAVE:
		case CMMsg.TYP_FLEE:
		case CMMsg.TYP_SLEEP:
		case CMMsg.TYP_MOUNT:
		case CMMsg.TYP_SIT:
			break;
		}
	}

	public boolean hasADoor() {
		return super.hasALid();
	}

	public boolean defaultsLocked() {
		return super.hasALock();
	}

	public boolean defaultsClosed() {
		return super.hasALid();
	}

	public void setDoorsNLocks(boolean hasADoor, boolean isOpen,
			boolean defaultsClosed, boolean hasALock, boolean isLocked,
			boolean defaultsLocked) {
		super.setLidsNLocks(hasADoor, isOpen, hasALock, isLocked);
	}

	public boolean isReadable() {
		return false;
	}

	public void setReadable(boolean isTrue) {
	}

	private static final StringBuilder empty = new StringBuilder("");

	public StringBuilder viewableText(MOB mob, Room myRoom) {
		List<String> V = CMParms.parseSemicolons(readableText(), true);
		Room room = myRoom;
		if (V.size() > 0)
			room = CMLib.map().getRoom(
					V.get(CMLib.dice().roll(1, V.size(), -1)));
		if (room == null)
			return empty;
		StringBuilder Say = new StringBuilder("");
		if (CMath.bset(mob.getBitmap(), MOB.ATT_SYSOPMSGS)) {
			Say.append("^H(" + CMLib.map().getExtendedRoomID(room) + ")^? "
					+ room.displayText(mob)
					+ CMLib.flags().colorCodes(room, mob) + " ");
			Say.append("via ^H(" + ID() + ")^? "
					+ (isOpen() ? name() : closedText()));
		} else if (((CMLib.flags().canBeSeenBy(this, mob)) || (isOpen() && hasADoor()))
				&& (CMLib.flags().isSeen(this)))
			if (isOpen()) {
				if (!CMLib.flags().canBeSeenBy(room, mob))
					Say.append("darkness");
				else
					Say.append(name() + CMLib.flags().colorCodes(this, mob));
			} else if ((CMLib.flags().canBeSeenBy(this, mob))
					&& (closedText().trim().length() > 0))
				Say.append(closedText() + CMLib.flags().colorCodes(this, mob));
		return Say;
	}

	protected String doorName = "";

	public String doorName() {
		return doorName;
	}

	protected String closedText = "";

	public String closedText() {
		return closedText;
	}

	public String closeWord() {
		return "close";
	}

	public String openWord() {
		return "open";
	}

	public void setExitParams(String newDoorName, String newCloseWord,
			String newOpenWord, String newClosedText) {
		doorName = newDoorName;
		closedText = newClosedText;
	}

	public int openDelayTicks() {
		return 0;
	}

	public void setOpenDelayTicks(int numTicks) {
	}

	public String temporaryDoorLink() {
		return "";
	}

	public void setTemporaryDoorLink(String link) {
	}
}
