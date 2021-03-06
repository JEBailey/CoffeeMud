package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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

public class Disease_Vampirism extends Disease {
	public String ID() {
		return "Disease_Vampirism";
	}

	public String name() {
		return "Vampirism";
	}

	public String displayText() {
		return "(Vampirism)";
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

	public boolean putInCommandlist() {
		return false;
	}

	protected int DISEASE_TICKS() {
		return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 6;
	}

	protected int DISEASE_DELAY() {
		return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
	}

	protected String DISEASE_DONE() {
		return "Your vampirism lifts.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> seem(s) pale and cold.^?";
	}

	protected String DISEASE_AFFECT() {
		return "";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_CONSUMPTION;
	}

	public int difficultyLevel() {
		return 9;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (!(affected instanceof MOB))
			return;
		if (((MOB) affected).location() == null)
			return;
		if (CMLib.flags().isInDark(((MOB) affected).location()))
			affectableStats.setSensesMask(affectableStats.sensesMask()
					| PhyStats.CAN_SEE_DARK);
		else
			affectableStats.setSensesMask(affectableStats.sensesMask()
					| PhyStats.CAN_NOT_SEE);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if (msg.amISource(mob) && (msg.tool() != null)
					&& (msg.tool().ID().equals("Skill_Swim"))) {
				mob.tell("You can't swim!");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		if (affected == null)
			return;
		affectableStats.setStat(CharStats.STAT_CHARISMA,
				affectableStats.getStat(CharStats.STAT_CHARISMA) + 1);
	}
}
