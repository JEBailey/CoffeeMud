package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Spell_MajorGlobe extends Spell {
	public String ID() {
		return "Spell_MajorGlobe";
	}

	public String name() {
		return "Greater Globe";
	}

	public String displayText() {
		return "(Greater Globe/Invul.)";
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
						"<S-YOUPOSS> greater anti-magic globe fades.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob))
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				&& (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
				&& (msg.tool() != null)
				&& (msg.tool() instanceof Ability)
				&& (((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
						|| ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER) || ((((Ability) msg
						.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT))
				&& (!mob.amDead())
				&& (CMLib.ableMapper().lowestQualifyingLevel(msg.tool().ID()) <= 15)
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null,
						0, false))) {
			amountAbsorbed += CMLib.ableMapper().lowestQualifyingLevel(
					msg.tool().ID());
			mob.location().show(
					mob,
					msg.source(),
					null,
					CMMsg.MSG_OK_VISUAL,
					"The globe around <S-NAME> absorbs the "
							+ msg.tool().name() + " from <T-NAME>!");
			return false;
		}
		if ((invoker != null)
				&& (amountAbsorbed > ((invoker.phyStats().level() + (2 * getXLEVELLevel(invoker))) * 4)))
			unInvoke();
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
							(auto ? "A great anti-magic field envelopes <T-NAME>!"
									: "^S<S-NAME> invoke(s) a great anti-magic globe of protection around <T-NAMESELF>.^?"));
			if (mob.location().okMessage(mob, msg)) {
				amountAbsorbed = 0;
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke a great anti-magic globe, but fail(s).");

		return success;
	}
}
