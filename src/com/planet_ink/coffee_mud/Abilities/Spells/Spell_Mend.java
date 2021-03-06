package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Spell_Mend extends Spell {
	public String ID() {
		return "Spell_Mend";
	}

	public String name() {
		return "Mend";
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, null, givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;
		if (!target.subjectToWearAndTear()) {
			mob.tell(target.name(mob) + " cannot be mended.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob,
				(((mob.phyStats().level() + (2 * getXLEVELLevel(mob))) - target
						.phyStats().level()) * 5), auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					(auto ? "<T-NAME> begins to shimmer!"
							: "^S<S-NAME> incant(s) at <T-NAMESELF>!^?"));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (target.usesRemaining() >= 100)
					mob.tell("Nothing happens to " + target.name(mob) + ".");
				else {
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							"<T-NAME> begin(s) to glow and mend!");
					target.setUsesRemaining(100);
				}
				target.recoverPhyStats();
				mob.location().recoverRoomStats();
			}

		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> incant(s) at <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
