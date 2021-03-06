package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Thief_ImprovedHiding extends ThiefSkill {
	public String ID() {
		return "Thief_ImprovedHiding";
	}

	public String name() {
		return "Improved Hiding";
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

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public boolean active = false;

	public void improve(MOB mob, boolean yesorno) {
		Ability A = mob.fetchEffect("Thief_Hide");
		if (A != null) {
			if (yesorno)
				A.setAbilityCode(1);
			else
				A.setAbilityCode(0);
		}
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return super.okMessage(myHost, msg);

		MOB mob = (MOB) affected;
		if ((!CMLib.flags().isHidden(mob)) && (active)) {
			active = false;
			improve(mob, false);
			mob.recoverPhyStats();
		}
		return super.okMessage(myHost, msg);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((affected != null) && (affected instanceof MOB)) {
			if (CMLib.flags().isHidden(affected)) {
				if (!active) {
					active = true;
					helpProficiency((MOB) affected, 0);
					improve((MOB) affected, true);
				}
			} else if (active) {
				active = false;
				improve((MOB) affected, false);
			}
		}
		return true;
	}
}
