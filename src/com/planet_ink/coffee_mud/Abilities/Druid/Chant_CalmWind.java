package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Climate;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Chant_CalmWind extends Chant {
	public String ID() {
		return "Chant_CalmWind";
	}

	public String name() {
		return "Calm Wind";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public long flags() {
		return Ability.FLAG_WEATHERAFFECTING;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
	}

	public static void xpWorthyChange(MOB mob, Climate oldC, Climate newC) {
		if ((oldC.nextWeatherType(null) != Climate.WEATHER_CLEAR)
				&& (oldC.nextWeatherType(null) != Climate.WEATHER_CLOUDY)
				&& ((newC.nextWeatherType(null) == Climate.WEATHER_CLEAR) || (newC
						.nextWeatherType(null) == Climate.WEATHER_CLOUDY))
				&& ((newC.weatherType(null) == Climate.WEATHER_CLEAR) || (newC
						.weatherType(null) == Climate.WEATHER_CLOUDY))) {
			mob.tell("^YYou have restored balance to the weather!^N");
			CMLib.leveler().postExperience(mob, null, null, 25, false);
		}
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Room R = mob.location();
			if (R != null) {
				if (CMath.bset(weatherQue(R), WEATHERQUE_CALM))
					return super.castingQuality(mob, target,
							Ability.QUALITY_BENEFICIAL_SELF);
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (((mob.location().domainType() & Room.INDOORS) > 0) && (!auto)) {
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		switch (mob.location().getArea().getClimateObj()
				.weatherType(mob.location())) {
		case Climate.WEATHER_WINDY:
		case Climate.WEATHER_THUNDERSTORM:
		case Climate.WEATHER_BLIZZARD:
		case Climate.WEATHER_DUSTSTORM:
			break;
		case Climate.WEATHER_HAIL:
		case Climate.WEATHER_SLEET:
		case Climate.WEATHER_SNOW:
		case Climate.WEATHER_RAIN:
			mob.tell("The weather is nasty, but not especially windy any more.");
			return false;
		default:
			mob.tell("If doesn't seem especially windy right now.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int size = mob.location().getArea().numberOfProperIDedRooms();
		size = size
				/ (mob.phyStats().level() + (2 * super.getXLEVELLevel(mob)));
		if (size < 0)
			size = 0;
		boolean success = proficiencyCheck(mob, -size, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto),
					auto ? "^JThe swirling sky changes color!^?"
							: "^S<S-NAME> chant(s) into the swirling sky!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Climate C = mob.location().getArea().getClimateObj();
				Climate oldC = (Climate) C.copyOf();
				switch (C.weatherType(mob.location())) {
				case Climate.WEATHER_WINDY:
					C.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				case Climate.WEATHER_THUNDERSTORM:
					C.setNextWeatherType(Climate.WEATHER_RAIN);
					break;
				case Climate.WEATHER_BLIZZARD:
					C.setNextWeatherType(Climate.WEATHER_SNOW);
					break;
				case Climate.WEATHER_DUSTSTORM:
					C.setNextWeatherType(Climate.WEATHER_CLEAR);
					break;
				default:
					break;
				}
				C.forceWeatherTick(mob.location().getArea());
				Chant_CalmWeather.xpWorthyChange(mob, mob.location().getArea(),
						oldC, C);
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> chant(s) into the sky, but the magic fizzles.");

		return success;
	}
}
