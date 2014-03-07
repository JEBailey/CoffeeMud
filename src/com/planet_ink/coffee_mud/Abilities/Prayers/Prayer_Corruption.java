package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.HashSet;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Prayer_Corruption extends Prayer {
	public String ID() {
		return "Prayer_Corruption";
	}

	public String name() {
		return "Corruption";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		CMMsg msg2 = null;
		if ((mob != target)
				&& (!mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
			msg2 = CMClass
					.getMsg(mob, target, this,
							verbalCastCode(mob, target, auto)
									| CMMsg.MASK_MALICIOUS,
							"<T-NAME> do(es) not seem to like <S-NAME> messing with <T-HIS-HER> head.");
		if (success
				&& (CMLib.factions().getFaction(CMLib.factions().AlignID()) != null)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					(auto ? "<T-NAME> feel(s) more evil." : "^S<S-NAME> "
							+ prayWord(mob) + " to corrupt <T-NAMESELF>!^?"));
			if ((mob.location().okMessage(mob, msg))
					&& ((msg2 == null) || (mob.location().okMessage(mob, msg2)))) {
				mob.location().send(mob, msg);
				if ((msg.value() <= 0)
						&& ((msg2 == null) || (msg2.value() <= 0))) {
					target.tell("Evil, vile thoughts fill your head.");
					int evilness = CMLib.dice().roll(10,
							adjustedLevel(mob, asLevel), 0)
							* -1;
					CMLib.factions().postFactionChange(target, this,
							CMLib.factions().AlignID(), evilness);
				}
				if (msg2 != null)
					mob.location().send(mob, msg2);
			}
		} else {
			if ((msg2 != null) && (mob.location().okMessage(mob, msg2)))
				mob.location().send(mob, msg2);
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> point(s) at <T-NAMESELF> and " + prayWord(mob)
							+ ", but nothing happens.");
		}

		// return whether it worked
		return success;
	}
}