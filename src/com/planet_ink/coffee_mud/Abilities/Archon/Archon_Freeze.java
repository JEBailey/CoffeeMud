package com.planet_ink.coffee_mud.Abilities.Archon;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Log;
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
public class Archon_Freeze extends ArchonSkill {
	boolean doneTicking = false;

	public String ID() {
		return "Archon_Freeze";
	}

	public String name() {
		return "Freeze";
	}

	public String displayText() {
		return "(Freezed)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	private static final String[] triggerStrings = { "FREEZE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_ARCHON;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(1);
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if (msg.amISource(mob)) {
			switch (msg.sourceMinor()) {
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_ADVANCE:
			case CMMsg.TYP_LEAVE:
			case CMMsg.TYP_FLEE:
				mob.tell("You are frozen, and cant go anywhere.");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("You are no longer freezed!");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTargetAnywhere(mob, commands, givenTarget, false, true,
				false);
		if (target == null)
			return false;

		Ability A = target.fetchEffect(ID());
		if (A != null) {
			A.unInvoke();
			mob.tell(target.Name() + " is released from his freezedness.");
			return true;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE
					| CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
					auto ? "A frozen chill falls upon <T-NAME>!"
							: "^F<S-NAME> freeze(s) <T-NAMESELF>.^?");
			CMLib.color().fixSourceFightColor(msg);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> <S-IS-ARE> frozen!");
				beneficialAffect(mob, target, asLevel,
						Ability.TICKS_ALMOST_FOREVER);
				Log.sysOut("Freeze", mob.Name() + " freezed " + target.name()
						+ ".");
			}
		} else
			return beneficialVisualFizzle(mob, target,
					"<S-NAME> attempt(s) to freeze <T-NAMESELF>, but fail(s).");
		return success;
	}
}