package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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

public class Disease_Plague extends Disease {
	public String ID() {
		return "Disease_Plague";
	}

	public String name() {
		return "The Plague";
	}

	public String displayText() {
		return "(Plague)";
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
		return 48;
	}

	protected int DISEASE_DELAY() {
		return 4;
	}

	protected String DISEASE_DONE() {
		return "The sores on your face clear up.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> look(s) seriously ill!^?";
	}

	protected String DISEASE_AFFECT() {
		return "<S-NAME> watch(es) <S-HIS-HER> body erupt with a fresh batch of painful oozing sores!";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_CONSUMPTION
				| DiseaseAffect.SPREAD_PROXIMITY | DiseaseAffect.SPREAD_CONTACT
				| DiseaseAffect.SPREAD_STD;
	}

	public int difficultyLevel() {
		return 0;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (affected == null)
			return false;
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
			MOB diseaser = invoker;
			if (diseaser == null)
				diseaser = mob;
			diseaseTick = DISEASE_DELAY();
			mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
					DISEASE_AFFECT());
			int dmg = mob.phyStats().level() / 2;
			if (dmg < 1)
				dmg = 1;
			CMLib.combat().postDamage(diseaser, mob, this, dmg,
					CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1, null);
			catchIt(mob);
			return true;
		}
		return true;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		if (affected == null)
			return;
		affectableStats.setStat(CharStats.STAT_CONSTITUTION, 3);
		affectableStats.setStat(CharStats.STAT_DEXTERITY, 3);
	}
}
