package com.planet_ink.coffee_mud.Libraries.interfaces;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.Law;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.Deity;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.LandTitle;
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
public interface LegalLibrary extends CMLibrary {
	public Law getTheLaw(Room R, MOB mob);

	public LegalBehavior getLegalBehavior(Area A);

	public LegalBehavior getLegalBehavior(Room R);

	public Area getLegalObject(Area A);

	public Area getLegalObject(Room R);

	public String getLandOwnerName(Room room);

	public LandTitle getLandTitle(Area area);

	public LandTitle getLandTitle(Room room);

	public boolean isRoomSimilarlyTitled(LandTitle title, Room R);

	public Set<Room> getHomePeersOnThisFloor(Room room, Set<Room> doneRooms);

	public boolean isHomeRoomDownstairs(Room room);

	public boolean isHomeRoomUpstairs(Room room);

	public boolean doesHavePriviledgesHere(MOB mob, Room room);

	public boolean doesAnyoneHavePrivilegesHere(MOB mob, String overrideID,
			Room R);

	public boolean doesHavePriviledgesInThisDirection(MOB mob, Room room,
			Exit exit);

	public boolean doesOwnThisProperty(String name, Room room);

	public boolean doesOwnThisProperty(MOB mob, Room room);

	public List<LandTitle> getAllUniqueTitles(Enumeration<Room> e,
			String owner, boolean includeRentals);

	public Ability getClericInfusion(Physical room);

	public Deity getClericInfused(Room room);

	public boolean isLegalOfficerHere(MOB mob);

	public boolean isLegalJudgeHere(MOB mob);

	public boolean isLegalOfficialHere(MOB mob);

	public boolean isACity(Area A);
}
