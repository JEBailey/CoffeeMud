package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class LinkedWeather extends StdBehavior {
	public String ID() {
		return "LinkedWeather";
	}

	protected int canImproveCode() {
		return Behavior.CAN_AREAS;
	}

	protected long lastWeather = -1;
	protected long lastPending = -1;
	protected String areaName = null;
	protected boolean rolling = false;

	public String accountForYourself() {
		return "weather event linking";
	}

	public boolean tick(Tickable ticking, int tickID) {
		super.tick(ticking, tickID);
		if (tickID != Tickable.TICKID_AREA)
			return true;
		if (!(ticking instanceof Area))
			return true;
		if (areaName == null) {
			if (getParms().length() == 0)
				return true;
			String s = getParms();
			int x = s.indexOf(';');
			rolling = false;
			if (x >= 0) {
				if (s.indexOf("ROLL", x + 1) >= 0)
					rolling = true;
				s = s.substring(0, x);
			}
			Area A = CMLib.map().getArea(s);
			if (A != null)
				areaName = A.Name();
		}

		Area A = (Area) ticking;
		Area linkedA = CMLib.map().getArea(areaName);
		if (linkedA != null) {
			if (rolling)
				A.getClimateObj().setNextWeatherType(
						linkedA.getClimateObj().weatherType(null));
			else {
				A.getClimateObj().setCurrentWeatherType(
						linkedA.getClimateObj().weatherType(null));
				A.getClimateObj().setNextWeatherType(
						linkedA.getClimateObj().nextWeatherType(null));
			}
		}
		return true;
	}
}