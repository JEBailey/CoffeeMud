package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Social;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Chant_Fertility extends Chant {
	public String ID() {
		return "Chant_Fertility";
	}

	public String name() {
		return "Fertility";
	}

	public String displayText() {
		return "(Fertility)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("Your extreme fertility subsides.");
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		// the sex rules
		if (!(affected instanceof MOB))
			return;

		MOB myChar = (MOB) affected;
		if ((msg.target() != null) && (msg.target() instanceof MOB)) {
			MOB mate = (MOB) msg.target();
			if ((msg.amISource(myChar))
					&& (msg.tool() instanceof Social)
					&& (msg.tool().Name().equals("MATE <T-NAME>") || msg.tool()
							.Name().equals("SEX <T-NAME>"))
					&& (msg.sourceMinor() != CMMsg.TYP_CHANNEL)
					&& (myChar.charStats().getStat(CharStats.STAT_GENDER) != mate
							.charStats().getStat(CharStats.STAT_GENDER))
					&& ((mate.charStats().getStat(CharStats.STAT_GENDER) == ('M')) || (mate
							.charStats().getStat(CharStats.STAT_GENDER) == ('F')))
					&& ((myChar.charStats().getStat(CharStats.STAT_GENDER) == ('M')) || (myChar
							.charStats().getStat(CharStats.STAT_GENDER) == ('F')))
					&& (mate.charStats().getMyRace().canBreedWith(myChar
							.charStats().getMyRace()))
					&& (myChar.location() == mate.location())
					&& (myChar.fetchWornItems(
							Wearable.WORN_LEGS | Wearable.WORN_WAIST,
							(short) -2048, (short) 0).size() == 0)
					&& (mate.fetchWornItems(
							Wearable.WORN_LEGS | Wearable.WORN_WAIST,
							(short) -2048, (short) 0).size() == 0)) {
				MOB female = myChar;
				MOB male = mate;
				if ((mate.charStats().getStat(CharStats.STAT_GENDER) == ('F'))) {
					female = mate;
					male = myChar;
				}
				Ability A = CMClass.getAbility("Pregnancy");
				if ((A != null) && (female.fetchAbility(A.ID()) == null)
						&& (female.fetchEffect(A.ID()) == null)) {
					A.invoke(male, female, true, 0);
					unInvoke();
				}
			}
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability A = target.fetchEffect("Chant_StrikeBarren");
				if (A != null) {
					if (A.invoker() == null)
						A.unInvoke();
					else if (A.invoker().phyStats().level() < adjustedLevel(
							mob, asLevel))
						A.unInvoke();
					else {
						mob.tell("The magical barrenness upon "
								+ target.name(mob) + " is too powerful.");
						return false;
					}
				}
				mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> seem(s) extremely fertile!");
				beneficialAffect(mob, target, asLevel,
						Ability.TICKS_ALMOST_FOREVER);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades.");

		// return whether it worked
		return success;
	}
}
