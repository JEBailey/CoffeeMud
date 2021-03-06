package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Spell_DetectUndead extends Spell {
	public String ID() {
		return "Spell_DetectUndead";
	}

	public String name() {
		return "Detect Undead";
	}

	public String displayText() {
		return "(Detecting Undead)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public int enchantQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
	}

	Room lastRoom = null;

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		lastRoom = null;
		super.unInvoke();
		if (canBeUninvoked())
			mob.tell("Your senses are no longer as dark.");
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
			for (int i = 0; i < lastRoom.numInhabitants(); i++) {
				MOB mob = lastRoom.fetchInhabitant(i);
				if ((mob != null)
						&& (mob != affected)
						&& (mob.charStats() != null)
						&& (mob.charStats().getMyRace() != null)
						&& (mob.charStats().getMyRace().racialCategory()
								.equalsIgnoreCase("Undead")))
					((MOB) affected).tell(mob, null, null,
							"<S-NAME> gives off a cold dark vibe.");
			}
		}
		return true;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (((MOB) target).isInCombat() || ((MOB) target).isMonster())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> detecting undead things.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> gain(s) dark cold senses!"
									: "^S<S-NAME> incant(s) softly, and gain(s) dark cold senses!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> incant(s) and open(s) <S-HIS-HER> cold eyes, but the spell fizzles.");

		return success;
	}
}
