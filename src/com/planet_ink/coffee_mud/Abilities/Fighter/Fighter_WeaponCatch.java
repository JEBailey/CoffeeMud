package com.planet_ink.coffee_mud.Abilities.Fighter;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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

public class Fighter_WeaponCatch extends FighterSkill {
	public String ID() {
		return "Fighter_WeaponCatch";
	}

	public String name() {
		return "Weapon Catch";
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
		return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;

		if (msg.amITarget(mob)
				&& (CMLib.flags().aliveAwakeMobileUnbound(mob, true))
				&& (msg.tool() instanceof Ability)
				&& (msg.tool().ID().equals("Skill_Disarm"))
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(mob,
						0, false)) && (mob.rangeToTarget() == 0)) {
			CMMsg msg2 = CMClass
					.getMsg(mob,
							msg.source(),
							this,
							CMMsg.MSG_NOISYMOVEMENT,
							"<T-NAME> disarms <S-NAMESELF>, but <S-NAME> catch the weapon!",
							"<T-NAME> disarm <S-NAMESELF>, but <S-NAME> catches the weapon!",
							"<T-NAME> disarms <S-NAMESELF>, but <S-NAME> catches the weapon!");
			if (mob.location().okMessage(mob, msg2)) {
				mob.location().send(mob, msg2);
				helpProficiency(mob, 0);
				return false;
			}
		}
		return true;
	}
}
