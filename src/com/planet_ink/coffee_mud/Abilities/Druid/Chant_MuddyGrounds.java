package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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

@SuppressWarnings("rawtypes")
public class Chant_MuddyGrounds extends Chant {
	public String ID() {
		return "Chant_MuddyGrounds";
	}

	public String name() {
		return "Muddy Grounds";
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	public void unInvoke() {
		if ((canBeUninvoked()) && (affected != null)
				&& (affected instanceof Room))
			((Room) affected).showHappens(CMMsg.MSG_OK_VISUAL, "The mud in '"
					+ ((Room) affected).displayText() + "' dries up.");
		super.unInvoke();
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if ((affected != null) && (affected instanceof Room))
			affectableStats.setWeight((affectableStats.weight() * 2) + 1);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof Room)) {
			Room R = (Room) affected;
			for (int m = 0; m < R.numInhabitants(); m++) {
				MOB M = R.fetchInhabitant(m);
				if ((M != null) && (M.isInCombat()))
					M.curState().adjMovement(-1, M.maxState());
			}
		}
		return super.tick(ticking, tickID);

	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Room R = mob.location();
			if (R != null) {
				int type = R.domainType();
				if (((type & Room.INDOORS) > 0)
						|| (type == Room.DOMAIN_OUTDOORS_AIR)
						|| (type == Room.DOMAIN_OUTDOORS_CITY)
						|| (type == Room.DOMAIN_OUTDOORS_SPACEPORT)
						|| (type == Room.DOMAIN_OUTDOORS_UNDERWATER)
						|| (type == Room.DOMAIN_OUTDOORS_WATERSURFACE))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		int type = mob.location().domainType();
		if (((type & Room.INDOORS) > 0) || (type == Room.DOMAIN_OUTDOORS_AIR)
				|| (type == Room.DOMAIN_OUTDOORS_CITY)
				|| (type == Room.DOMAIN_OUTDOORS_SPACEPORT)
				|| (type == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (type == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
			mob.tell("That magic won't work here.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, mob.location(), this,
					verbalCastCode(mob, mob.location(), auto), auto ? ""
							: "^S<S-NAME> chant(s) to the ground.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
						"The ground here turns to MUD!");
				if (CMLib.law().doesOwnThisProperty(mob, mob.location())) {
					mob.location().addNonUninvokableEffect((Ability) copyOf());
					CMLib.database().DBUpdateRoom(mob.location());
				} else
					beneficialAffect(mob, mob.location(), asLevel, 0);
			}

		} else
			beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to the ground, but nothing happens.");

		// return whether it worked
		return success;
	}
}