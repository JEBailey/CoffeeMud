package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Scroll;
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
public class Spell_Erase extends Spell {
	public String ID() {
		return "Spell_Erase";
	}

	public String name() {
		return "Erase Scroll";
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

		if ((commands.size() < 1) && (givenTarget == null)) {
			mob.tell("Erase what?.");
			return false;
		}
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if (!(target instanceof Scroll) && (!target.isReadable())) {
			mob.tell("You can't erase that.");
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
							auto ? "The words on <T-NAME> fade."
									: "^S<S-NAME> whisper(s), and then rub(s) on <T-NAMESELF>, making the words fade.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (target instanceof Scroll)
					((Scroll) target).setSpellList("");
				else
					target.setReadableText("");
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> whisper(s), and then rub(s) on <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}