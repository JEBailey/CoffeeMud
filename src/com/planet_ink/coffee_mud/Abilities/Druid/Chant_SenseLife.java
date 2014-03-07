package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;
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
public class Chant_SenseLife extends Chant {
	public String ID() {
		return "Chant_SenseLife";
	}

	public String name() {
		return "Life Echoes";
	}

	public String displayText() {
		return "(Life Echoes)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	protected Room lastRoom = null;

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked()) {
			lastRoom = null;
			mob.tell("Your life echo sensations fade.");
		}
	}

	public boolean inhabitated(MOB mob, Room R) {
		if (R == null)
			return false;
		for (int i = 0; i < R.numInhabitants(); i++) {
			MOB M = R.fetchInhabitant(i);
			if ((M != null)
					&& (!CMLib.flags().isGolem(M))
					&& (M.charStats().getMyRace().canBreedWith(M.charStats()
							.getMyRace())) && (M != mob))
				return true;
		}
		return false;
	}

	public void messageTo(MOB mob) {
		String last = "";
		String dirs = "";
		for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
			Room R = mob.location().getRoomInDir(d);
			Exit E = mob.location().getExitInDir(d);
			if ((R != null) && (E != null) && (inhabitated(mob, R))) {
				if (last.length() > 0)
					dirs += ", " + last;
				last = Directions.getFromDirectionName(d);
			}
		}
		if (inhabitated(mob, mob.location())) {
			if (last.length() > 0)
				dirs += ", " + last;
			last = "here";
		}

		if ((dirs.length() == 0) && (last.length() == 0))
			mob.tell("You do not feel any life beyond your own.");
		else if (dirs.length() == 0)
			mob.tell("You feel a life force coming from " + last + ".");
		else
			mob.tell("You feel a life force coming from " + dirs.substring(2)
					+ ", and " + last + ".");
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((tickID == Tickable.TICKID_MOB)
				&& (affected != null)
				&& (affected instanceof MOB)
				&& (((MOB) affected).location() != null)
				&& ((lastRoom == null) || (((MOB) affected).location() != lastRoom))) {
			lastRoom = ((MOB) affected).location();
			messageTo((MOB) affected);
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already sensing life echoes.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> gain(s) life-senses!"
									: "^S<S-NAME> chant(s) softly, and then stop(s) to listen.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) softly, but nothing happens.");

		// return whether it worked
		return success;
	}
}