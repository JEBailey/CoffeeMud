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
public class Spell_FreeMovement extends Spell {
	public String ID() {
		return "Spell_FreeMovement";
	}

	public String name() {
		return "Free Movement";
	}

	public String displayText() {
		return "(Free Movement)";
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

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			mob.tell("Your uninhibiting protection dissipates.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob))
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				&& (msg.tool() != null) && (msg.tool() instanceof Ability)
				&& (!mob.amDead())) {
			Ability A = (Ability) msg.tool();
			if (CMath.bset(A.flags(), Ability.FLAG_PARALYZING)) {
				msg.addTrailerMsg(CMClass.getMsg(mob, null,
						CMMsg.MSG_OK_VISUAL,
						"The uninhibiting barrier around <S-NAME> repels the "
								+ A.name() + "."));
				return false;
			}
			MOB newMOB = CMClass.getFactoryMOB();
			CMMsg msg2 = CMClass
					.getMsg(newMOB, null, null, CMMsg.MSG_SIT, null);
			newMOB.recoverPhyStats();
			try {
				A.affectPhyStats(newMOB, newMOB.phyStats());
				if ((!CMLib.flags().aliveAwakeMobileUnbound(newMOB, true))
						|| (CMath.bset(A.flags(), Ability.FLAG_PARALYZING))
						|| (!A.okMessage(newMOB, msg2))) {
					msg.addTrailerMsg(CMClass.getMsg(mob, null,
							CMMsg.MSG_OK_VISUAL,
							"The uninhibiting barrier around <S-NAME> repels the "
									+ A.name() + "."));
					newMOB.destroy();
					return false;
				}
			} catch (Exception e) {
			}
			newMOB.destroy();
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
							auto ? "<T-NAME> feel(s) freely protected."
									: "^S<S-NAME> invoke(s) an uninhibiting barrier of protection around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke an uninhibiting barrier, but fail(s).");

		return success;
	}
}
