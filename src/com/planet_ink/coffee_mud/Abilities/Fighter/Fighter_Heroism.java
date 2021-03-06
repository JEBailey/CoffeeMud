package com.planet_ink.coffee_mud.Abilities.Fighter;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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

public class Fighter_Heroism extends FighterSkill {
	public String ID() {
		return "Fighter_Heroism";
	}

	public String name() {
		return "Heroism";
	}

	public String displayText() {
		return "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
	}

	private boolean activated = false;

	public void setActivated(boolean activate) {
		if (activate == activated)
			return;
		activated = activate;
		if (affected instanceof MOB)
			((MOB) affected).recoverCharStats();
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return super.tick(ticking, tickID);

		MOB mob = (MOB) affected;

		if ((CMLib.flags().isStanding(mob))
				&& (mob.isInCombat())
				&& ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null,
						0, false)) && (tickID == Tickable.TICKID_MOB)) {
			setActivated(true);
			if (CMLib.dice().rollPercentage() == 1)
				helpProficiency(mob, 0);
		} else
			setActivated(false);
		return super.tick(ticking, tickID);
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		affectableStats
				.setStat(
						CharStats.STAT_SAVE_JUSTICE,
						affectableStats.getStat(CharStats.STAT_SAVE_JUSTICE)
								+ (affectableStats
										.getStat(CharStats.STAT_CHARISMA) / 4)
								+ (affectableStats
										.getStat(CharStats.STAT_STRENGTH) / 4)
								+ (adjustedLevel(affected, 0) / 2));
	}
}
