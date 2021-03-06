package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.TimeClock;
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
public class Time extends StdCommand {
	public Time() {
	}

	private final String[] access = { "TIME", "DATE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Room room = mob.location();
		if (room == null)
			return false;
		mob.tell(room.getArea().getTimeObj().timeDescription(mob, room));
		if ((mob.playerStats() != null)
				&& (mob.playerStats().getBirthday() != null)) {
			TimeClock C = CMLib.time().globalClock();
			int day = C.getDayOfMonth();
			int month = C.getMonth();
			int year = C.getYear();
			int bday = mob.playerStats().getBirthday()[0];
			int bmonth = mob.playerStats().getBirthday()[1];
			if ((month > bmonth) || ((month == bmonth) && (day > bday)))
				year++;

			StringBuffer timeDesc = new StringBuffer("");
			if (C.getDaysInWeek() > 0) {
				long x = ((long) year) * ((long) C.getMonthsInYear())
						* C.getDaysInMonth();
				x = x + ((long) (bmonth - 1)) * ((long) C.getDaysInMonth());
				x = x + bmonth;
				timeDesc.append(C.getWeekNames()[(int) (x % C.getDaysInWeek())]
						+ ", ");
			}
			timeDesc.append("the " + bday + CMath.numAppendage(bday));
			timeDesc.append(" day of " + C.getMonthNames()[bmonth - 1]);
			if (C.getYearNames().length > 0)
				timeDesc.append(", "
						+ CMStrings.replaceAll(
								C.getYearNames()[year % C.getYearNames().length],
								"#", "" + year));
			mob.tell("Your next birthday is " + timeDesc.toString() + ".");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
