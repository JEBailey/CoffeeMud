package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Spell_MindBlock extends Spell {
	public String ID() {
		return "Spell_MindBlock";
	}

	public String name() {
		return "Mind Block";
	}

	public String displayText() {
		return "(Mind Block)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
	}

	int amountAbsorbed = 0;

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> anti-psionic field fades.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob))
				&& (!mob.amDead())
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null,
						0, false))) {
			boolean yep = (msg.targetMinor() == CMMsg.TYP_MIND);
			if ((!yep) && (msg.tool() != null)
					&& (msg.tool() instanceof Ability)) {
				Ability A = (Ability) msg.tool();
				if (((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ILLUSION)
						|| ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ENCHANTMENT))
					yep = true;
			}
			if (yep) {
				msg.source().tell(msg.source(), mob, null,
						"<T-NAME> seem(s) unaffected by the enchantment.");
				return false;
			}
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							(auto ? "A anti-psionic field envelopes <T-NAME>!"
									: "^S<S-NAME> invoke(s) an anti-psionic field of protection around <T-NAMESELF>.^?"));
			if (mob.location().okMessage(mob, msg)) {
				amountAbsorbed = 0;
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke an anti-psionic field, but fail(s).");

		return success;
	}
}
