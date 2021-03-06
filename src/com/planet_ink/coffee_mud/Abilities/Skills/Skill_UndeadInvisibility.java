package com.planet_ink.coffee_mud.Abilities.Skills;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Skill_UndeadInvisibility extends StdSkill {
	public String ID() {
		return "Skill_UndeadInvisibility";
	}

	public String name() {
		return "Undead Invisibility";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
				&& ((msg.amITarget(affected)))) {
			MOB target = (MOB) msg.target();
			if ((!target.isInCombat())
					&& (msg.source().location() == target.location())
					&& (msg.source().charStats().getMyRace().racialCategory()
							.equals("Undead"))
					&& (msg.source().getVictim() != target)) {
				msg.source().tell("You don't see " + target.name(msg.source()));
				if (target.getVictim() == msg.source()) {
					target.makePeace();
					target.setVictim(null);
					helpProficiency((MOB) affected, 0);
				}
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}
}
