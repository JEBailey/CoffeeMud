package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Chant_SacredEarth extends Chant {
	public String ID() {
		return "Chant_SacredEarth";
	}

	public String name() {
		return "Sacred Earth";
	}

	public String displayText() {
		return "(Sacred Earth)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if ((affected == null) || (!(affected instanceof Room)))
			return;
		Room R = (Room) affected;
		if (canBeUninvoked())
			R.showHappens(CMMsg.MSG_OK_VISUAL,
					"The sacred earth charm is ended.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;

		if ((msg.tool() instanceof Ability)
				&& ((((Ability) msg.tool()).classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_GATHERINGSKILL)) {
			msg.source().tell(
					"The sacred earth will not allow you to violate it.");
			return false;
		}
		if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.target() != null)
				&& (msg.target() instanceof MOB)
				&& ((((MOB) msg.target()).charStats().getMyRace()
						.racialCategory().equals("Vegetation")) || (((MOB) msg
						.target()).charStats().getMyRace().racialCategory()
						.equals("Earth Elemental")))) {
			int recovery = (int) Math.round(CMath.div((msg.value()), 2.0));
			msg.setValue(msg.value() - recovery);
		}
		return true;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Room R = mob.location();
			if (R != null) {
				if (((R.domainType() & Room.INDOORS) > 0)
						|| (R.domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER)
						|| (R.domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE)
						|| (R.domainType() == Room.DOMAIN_OUTDOORS_AIR))
					return Ability.QUALITY_INDIFFERENT;
			}

			if (mob.isInCombat()) {
				MOB victim = mob.getVictim();
				if (victim != null) {
					if (((victim.charStats().getMyRace().racialCategory()
							.equals("Vegetation")) || (victim.charStats()
							.getMyRace().racialCategory()
							.equals("Earth Elemental"))))
						return Ability.QUALITY_INDIFFERENT;
				}
				if (((!mob.charStats().getMyRace().racialCategory()
						.equals("Vegetation")) && (!mob.charStats().getMyRace()
						.racialCategory().equals("Earth Elemental"))))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Room target = mob.location();
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell("This earth is already sacred.");
			return false;
		}
		if ((((mob.location().domainType() & Room.INDOORS) > 0)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE) || (mob
				.location().domainType() == Room.DOMAIN_OUTDOORS_AIR))
				&& (!auto)) {
			mob.tell("This chant will not work here.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) to the ground.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							"The charm of the sacred earth begins here!");
					beneficialAffect(mob, target, asLevel, 0);
					for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
						Room R = mob.location().getRoomInDir(d);
						if ((R != null)
								&& (R.fetchEffect(ID()) == null)
								&& ((R.domainType() & Room.INDOORS) == 0)
								&& (R.domainType() != Room.DOMAIN_OUTDOORS_UNDERWATER)
								&& (R.domainType() != Room.DOMAIN_OUTDOORS_WATERSURFACE)
								&& (R.domainType() != Room.DOMAIN_OUTDOORS_AIR))
							beneficialAffect(mob, R, asLevel, 0);
					}
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) to the ground, but the magic fades.");
		// return whether it worked
		return success;
	}
}
