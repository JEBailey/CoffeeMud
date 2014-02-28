package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Spell_Ventriloquate extends Spell {
	public String ID() {
		return "Spell_Ventriloquate";
	}

	public String name() {
		return "Ventriloquate";
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_EXITS
				| Ability.CAN_ROOMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		if (commands.size() < 2) {
			mob.tell("You must specify who or what to cast this on, and what you want said.");
			return false;
		}
		Physical target = mob.location().fetchFromRoomFavorItems(null,
				(String) commands.elementAt(0));
		if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
			mob.tell("You don't see '" + ((String) commands.elementAt(0))
					+ "' here.");
			return false;
		}
		if (target == mob)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), null);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(
						mob,
						target,
						CMMsg.MSG_SPEAK,
						"^T<T-NAME> say(s) '" + CMParms.combine(commands, 1)
								+ "'^?");
			}

		} else
			beneficialWordsFizzle(
					mob,
					target,
					"<S-NAME> attempt(s) to ventriloquate through <T-NAMESELF>, but no one is fooled.");

		// return whether it worked
		return success;
	}
}
