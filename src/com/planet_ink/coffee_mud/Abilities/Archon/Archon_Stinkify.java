package com.planet_ink.coffee_mud.Abilities.Archon;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Log;
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
public class Archon_Stinkify extends ArchonSkill {
	boolean doneTicking = false;

	public String ID() {
		return "Archon_Stinkify";
	}

	public String name() {
		return "Stinkify";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	private static final String[] triggerStrings = { "STINKIFY" };

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

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTargetAnywhere(mob, commands, givenTarget, false, true,
				true);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE
					| CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
					auto ? "A stink cloud surrounds <T-NAME>!"
							: "^F<S-NAME> stinkif(ys) <T-NAMESELF>.^?");
			CMLib.color().fixSourceFightColor(msg);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (target.playerStats() != null) {
					mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> <S-IS-ARE> stinkier!");
					target.playerStats().adjHygiene(
							PlayerStats.HYGIENE_DELIMIT + 1);
					Log.sysOut("Stinkify",
							mob.Name() + " stinkied " + target.name() + ".");
				} else
					mob.tell(mob, target, null,
							"<T-NAME> is a mob.  Try a player.");
			}
		} else
			return beneficialVisualFizzle(mob, target,
					"<S-NAME> attempt(s) to stinkify <T-NAMESELF>, but fail(s).");
		return success;
	}
}
