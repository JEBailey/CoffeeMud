package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.Climate;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
public class Prop_Weather extends Property {
	public String ID() {
		return "Prop_Weather";
	}

	public String name() {
		return "Weather Setter";
	}

	protected int canAffectCode() {
		return Ability.CAN_AREAS;
	}

	int code = -1;

	public void affectPhyStats(Physical host, PhyStats stats) {
		super.affectPhyStats(host, stats);
		if ((code < 0) && (text().length() > 0)) {
			for (int i = 0; i < Climate.WEATHER_DESCS.length; i++)
				if (Climate.WEATHER_DESCS[i].equalsIgnoreCase(text()))
					code = i;
		}
		if (code >= 0) {
			if (affected instanceof Room) {
				((Room) affected).getArea().getClimateObj()
						.setCurrentWeatherType(code);
				((Room) affected).getArea().getClimateObj()
						.setNextWeatherType(code);
			} else if (affected instanceof Area) {
				((Area) affected).getClimateObj().setCurrentWeatherType(code);
				((Area) affected).getClimateObj().setNextWeatherType(code);
			}
		}
	}

}
