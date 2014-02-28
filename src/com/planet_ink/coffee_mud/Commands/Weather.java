package com.planet_ink.coffee_mud.Commands;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;

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
public class Weather extends StdCommand {
	public Weather() {
	}

	private final String[] access = { "WEATHER" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Room room = mob.location();
		if (room == null)
			return false;
		if ((commands.size() > 1) && ((room.domainType() & Room.INDOORS) == 0)
				&& (((String) commands.elementAt(1)).equalsIgnoreCase("WORLD"))) {
			StringBuffer tellMe = new StringBuffer("");
			for (Enumeration a = CMLib.map().areas(); a.hasMoreElements();) {
				Area A = (Area) a.nextElement();
				if ((CMLib.flags().canAccess(mob, A))
						&& (!CMath.bset(A.flags(), Area.FLAG_INSTANCE_CHILD)))
					tellMe.append(CMStrings.padRight(A.name(), 20) + ": "
							+ A.getClimateObj().weatherDescription(room)
							+ "\n\r");
			}
			mob.tell(tellMe.toString());
			return false;
		}
		mob.tell(room.getArea().getClimateObj().weatherDescription(room));
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
