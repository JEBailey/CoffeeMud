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
public class Archon_Hush extends ArchonSkill {
	boolean doneTicking = false;

	public String ID() {
		return "Archon_Hush";
	}

	public String name() {
		return "Hush";
	}

	public String displayText() {
		return "(Hushed)";
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

	private static final String[] triggerStrings = { "HUSH" };

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
		if (!super.okMessage(myHost, msg))
			return false;

		if (((msg.sourceMinor() == CMMsg.TYP_TELL) || (msg
				.othersMajor(CMMsg.MASK_CHANNEL)))
				&& ((msg.source() == affected) || ((msg.source().location() == CMLib
						.map().roomLocation(affected))
						&& (msg.source().isMonster()) && (msg.source()
						.willFollowOrdersOf((MOB) affected))))) {
			msg.source().tell("Your message drifts into oblivion.");
			return false;
		}
		return true;
	}

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("You are no longer hushed!");
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
			mob.tell(target.Name() + " is released from his hushing.");
			return true;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE
					| CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
					auto ? "Silence falls upon <T-NAME>!"
							: "^F<S-NAME> hush(es) <T-NAMESELF>.^?");
			CMLib.color().fixSourceFightColor(msg);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> <S-IS-ARE> hushed!");
				beneficialAffect(mob, target, asLevel,
						Ability.TICKS_ALMOST_FOREVER);
				Log.sysOut("Banish", mob.Name() + " hushed " + target.name()
						+ ".");
			}
		} else
			return beneficialVisualFizzle(mob, target,
					"<S-NAME> attempt(s) to hush <T-NAMESELF>, but fail(s).");
		return success;
	}
}
