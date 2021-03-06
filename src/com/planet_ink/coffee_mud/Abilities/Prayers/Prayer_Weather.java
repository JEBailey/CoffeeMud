package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Climate;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Prayer_Weather extends Prayer {
	public String ID() {
		return "Prayer_Weather";
	}

	public String name() {
		return "Change Weather";
	}

	protected int canAffectCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int size = mob.location().getArea().numberOfProperIDedRooms();
		size = size
				- ((mob.phyStats().level() + (2 * super.getXLEVELLevel(mob))) * 20);
		if (size < 0)
			size = 0;
		boolean success = proficiencyCheck(mob, -size, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto),
					auto ? "The sky changes color!" : "^S<S-NAME> "
							+ prayWord(mob) + " for a change in weather!^?");
			if (mob.location().okMessage(mob, msg)) {
				int switcher = CMLib.dice().roll(1, 3, 0);
				mob.location().send(mob, msg);
				switch (mob.location().getArea().getClimateObj()
						.weatherType(mob.location())) {
				case Climate.WEATHER_BLIZZARD:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_BLIZZARD);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_BLIZZARD);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SNOW);
					break;
				case Climate.WEATHER_CLEAR:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_WINDY);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_RAIN);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					break;
				case Climate.WEATHER_CLOUDY:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_WINDY);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_RAIN);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_DROUGHT:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_DUSTSTORM);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_HEAT_WAVE);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_DUSTSTORM:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_DUSTSTORM);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_HAIL:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_HAIL);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SLEET);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					break;
				case Climate.WEATHER_HEAT_WAVE:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_DUSTSTORM);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_RAIN);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_RAIN:
					if (switcher == 1)
						mob.location()
								.getArea()
								.getClimateObj()
								.setNextWeatherType(
										Climate.WEATHER_THUNDERSTORM);
					else if (switcher == 2)
						mob.location()
								.getArea()
								.getClimateObj()
								.setNextWeatherType(
										Climate.WEATHER_THUNDERSTORM);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					break;
				case Climate.WEATHER_SLEET:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SLEET);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SLEET);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					break;
				case Climate.WEATHER_SNOW:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_BLIZZARD);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SLEET);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLOUDY);
					break;
				case Climate.WEATHER_THUNDERSTORM:
					if (switcher == 1)
						mob.location()
								.getArea()
								.getClimateObj()
								.setNextWeatherType(
										Climate.WEATHER_THUNDERSTORM);
					else if (switcher == 2)
						mob.location()
								.getArea()
								.getClimateObj()
								.setNextWeatherType(
										Climate.WEATHER_THUNDERSTORM);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_RAIN);
					break;
				case Climate.WEATHER_WINDY:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_WINDY);
					else if (switcher == 2)
						mob.location()
								.getArea()
								.getClimateObj()
								.setNextWeatherType(
										Climate.WEATHER_THUNDERSTORM);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_WINTER_COLD:
					if (switcher == 1)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_WINDY);
					else if (switcher == 2)
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_SNOW);
					else
						mob.location().getArea().getClimateObj()
								.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				default:
					break;
				}
				mob.location().getArea().getClimateObj()
						.forceWeatherTick(mob.location().getArea());
			}
		} else
			beneficialVisualFizzle(mob, null, "<S-NAME> " + prayWord(mob)
					+ ", but nothing happens.");

		return success;
	}
}
