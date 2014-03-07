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
public class Spell_SpellTurning extends Spell {
	public String ID() {
		return "Spell_SpellTurning";
	}

	public String name() {
		return "Spell Turning";
	}

	public String displayText() {
		return "(Spell Turning)";
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

	protected boolean oncePerRound = false;

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> reflective protection dissipates.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob))
				&& (!oncePerRound)
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				&& (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
				&& (msg.tool() != null)
				&& (msg.tool() instanceof Ability)
				&& ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
				&& (!mob.amDead())
				&& (mob != msg.source())
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(
						null,
						((mob.phyStats().level() + getXLEVELLevel(invoker())) - (msg
								.source().phyStats().level())) * 2, false))
				&& ((CMLib.dice().rollPercentage() + (2 * getXLEVELLevel(invoker()))) > 75)) {
			oncePerRound = true;
			mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
					"The field around <S-NAME> reflects the spell!");
			Ability A = (Ability) msg.tool();
			A.invoke(mob, msg.source(), true, msg.source().phyStats().level());
			return false;
		}
		return true;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				MOB victim = ((MOB) target).getVictim();
				if ((victim != null)
						&& (CMLib.flags()
								.domainAbilities(victim, Ability.ACODE_SPELL)
								.size() == 0))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean tick(Tickable ticking, int tickID) {
		oncePerRound = false;
		return super.tick(ticking, tickID);
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
							auto ? "A reflective barrier appears around <T-NAMESELF>."
									: "^S<S-NAME> invoke(s) a reflective barrier of protection around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke a reflective spell, but fail(s).");

		return success;
	}
}