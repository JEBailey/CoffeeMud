package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_Awe extends Spell {
	public String ID() {
		return "Spell_Awe";
	}

	public String name() {
		return "Awe";
	}

	public String displayText() {
		return "(Awe spell)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
				&& ((msg.amITarget(affected)))) {
			MOB target = (MOB) msg.target();
			if ((!target.isInCombat())
					&& (msg.source().getVictim() != target)
					&& (msg.source().location() == target.location())
					&& (CMLib.dice().rollPercentage() > ((msg.source()
							.phyStats().level() - (target.phyStats().level() + (2 * getXLEVELLevel(invoker())))) * 10))) {
				msg.source().tell(
						"You are too much in awe of "
								+ target.name(msg.source()));
				if (target.getVictim() == msg.source()) {
					target.makePeace();
					target.setVictim(null);
				}
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (((MOB) target).isInCombat())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();

		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> seem(s) less awesome.");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		Room R = CMLib.map().roomLocation(target);
		if (R == null)
			R = mob.location();

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
							: "^S<S-NAME> invoke(s) a spell.^?");
			if (R.okMessage(mob, msg)) {
				R.send(mob, msg);
				R.show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> seem(s) awesome!");
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke a spell, but fail(s) miserably.");

		// return whether it worked
		return success;
	}
}