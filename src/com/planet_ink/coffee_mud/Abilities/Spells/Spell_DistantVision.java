package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Spell_DistantVision extends Spell {
	public String ID() {
		return "Spell_DistantVision";
	}

	public String name() {
		return "Distant Vision";
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (commands.size() < 1) {
			mob.tell("Divine a vision of where?");
			return false;
		}
		String areaName = CMParms.combine(commands, 0).trim().toUpperCase();
		Room thisRoom = null;
		try {
			List<Room> rooms = CMLib.map().findRooms(CMLib.map().rooms(), mob,
					areaName, true, 10);
			if (rooms.size() > 0)
				thisRoom = rooms.get(CMLib.dice().roll(1, rooms.size(), -1));
		} catch (NoSuchElementException nse) {
		}

		if (thisRoom == null) {
			mob.tell("You can't seem to fixate on a place called '"
					+ CMParms.combine(commands, 0) + "'.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							null,
							this,
							verbalCastCode(mob, thisRoom, auto),
							auto ? ""
									: "^S<S-NAME> close(s) <S-HIS-HER> eyes, and invoke(s) a vision.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.tell("\n\r\n\r");
				CMMsg msg2 = CMClass
						.getMsg(mob, thisRoom, CMMsg.MSG_LOOK, null);
				thisRoom.executeMsg(mob, msg2);
			}

		} else
			beneficialWordsFizzle(
					mob,
					null,
					"<S-NAME> close(s) <S-HIS-HER> eyes, incanting, but then open(s) them in frustration.");

		// return whether it worked
		return success;
	}
}