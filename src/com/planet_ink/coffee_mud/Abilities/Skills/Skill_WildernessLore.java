package com.planet_ink.coffee_mud.Abilities.Skills;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Places;

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
public class Skill_WildernessLore extends StdSkill {
	public String ID() {
		return "Skill_WildernessLore";
	}

	public String name() {
		return "Wilderness Lore";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "WILDERNESSLORE", "WLORE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_NATURELORE;
	}

	public int usageType() {
		return USAGE_MANA;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (!success) {
			mob.location()
					.show(mob, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> take(s) a quick look at the terrain and feel(s) quite confused.");
			return false;
		}
		final Room room = mob.location();
		CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_HANDS,
				"<S-NAME> take(s) a quick look at the terrain.");
		if (room.okMessage(mob, msg)) {
			room.send(mob, msg);
			switch (room.domainType()) {
			case Room.DOMAIN_INDOORS_METAL:
				mob.tell("You are in a metal structure.");
				break;
			case Room.DOMAIN_OUTDOORS_SPACEPORT:
				mob.tell("You are at a space port.");
				break;
			case Room.DOMAIN_OUTDOORS_CITY:
				mob.tell("You are on a city street.");
				break;
			case Room.DOMAIN_OUTDOORS_WOODS:
				mob.tell("You are in a forest.");
				break;
			case Room.DOMAIN_OUTDOORS_ROCKS:
				mob.tell("You are on a rocky plain.");
				break;
			case Room.DOMAIN_OUTDOORS_PLAINS:
				mob.tell("You are on the plains.");
				break;
			case Room.DOMAIN_OUTDOORS_UNDERWATER:
				mob.tell("You are under the water.");
				break;
			case Room.DOMAIN_OUTDOORS_AIR:
				mob.tell("You are up in the air.");
				break;
			case Room.DOMAIN_OUTDOORS_WATERSURFACE:
				mob.tell("You are on the surface of the water.");
				break;
			case Room.DOMAIN_OUTDOORS_JUNGLE:
				mob.tell("You are in a jungle.");
				break;
			case Room.DOMAIN_OUTDOORS_SWAMP:
				mob.tell("You are in a swamp.");
				break;
			case Room.DOMAIN_OUTDOORS_DESERT:
				mob.tell("You are in a desert.");
				break;
			case Room.DOMAIN_OUTDOORS_HILLS:
				mob.tell("You are in the hills.");
				break;
			case Room.DOMAIN_OUTDOORS_MOUNTAINS:
				mob.tell("You are on a mountain.");
				break;
			case Room.DOMAIN_INDOORS_STONE:
				mob.tell("You are in a stone structure.");
				break;
			case Room.DOMAIN_INDOORS_WOOD:
				mob.tell("You are in a wooden structure.");
				break;
			case Room.DOMAIN_INDOORS_CAVE:
				mob.tell("You are in a cave.");
				break;
			case Room.DOMAIN_INDOORS_MAGIC:
				mob.tell("You are in a magical place.");
				break;
			case Room.DOMAIN_INDOORS_UNDERWATER:
				mob.tell("You are under the water.");
				break;
			case Room.DOMAIN_INDOORS_AIR:
				mob.tell("You are up in a large indoor space.");
				break;
			case Room.DOMAIN_INDOORS_WATERSURFACE:
				mob.tell("You are inside, on the surface of the water.");
				break;
			}
			final int derivedClimate = room.getClimateType();
			if (derivedClimate != Places.CLIMASK_NORMAL) {
				StringBuffer str = new StringBuffer("It is unusually ");
				List<String> conditions = new Vector<String>();
				if (CMath.bset(derivedClimate, Places.CLIMASK_WET))
					conditions.add("wet");
				if (CMath.bset(derivedClimate, Places.CLIMASK_HOT))
					conditions.add("hot");
				if (CMath.bset(derivedClimate, Places.CLIMASK_DRY))
					conditions.add("dry");
				if (CMath.bset(derivedClimate, Places.CLIMASK_COLD))
					conditions.add("cold");
				if (CMath.bset(derivedClimate, Places.CLIMASK_WINDY))
					conditions.add("windy");
				str.append(CMLib.english().toEnglishStringList(
						conditions.toArray(new String[0])));
				str.append(" here.");
				mob.tell(str.toString());
			}
		} else
			mob.location()
					.show(mob, null, this, CMMsg.MSG_HANDS,
							"<S-NAME> take(s) a quick look around, but get(s) confused.");
		return success;
	}

}
