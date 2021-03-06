package com.planet_ink.coffee_mud.Abilities.Diseases;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Disease_Leeches extends Disease {
	public String ID() {
		return "Disease_Leeches";
	}

	public String name() {
		return "Leeches";
	}

	public String displayText() {
		return "(Leeches)";
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
		return 35;
	}

	protected int DISEASE_DELAY() {
		return 7;
	}

	protected String DISEASE_DONE() {
		return "The leeches get full and fall off.";
	}

	protected String DISEASE_START() {
		return "^G<S-NAME> <S-HAS-HAVE> leeches covering <S-HIM-HER>!^?";
	}

	protected String DISEASE_AFFECT() {
		return "<S-NAME> cringe(s) from the leeches.";
	}

	public int spreadBitmap() {
		return DiseaseAffect.SPREAD_STD;
	}

	public int difficultyLevel() {
		return 0;
	}

	protected int hp = Integer.MAX_VALUE;
	protected String thename = "";

	public List<Ability> returnOffensiveAffects(Physical fromMe) {
		Vector offenders = new Vector();

		for (int a = 0; a < fromMe.numEffects(); a++) // personal
		{
			Ability A = fromMe.fetchEffect(a);
			if ((A != null)
					&& ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
				offenders.addElement(A);
		}
		return offenders;
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
			diseaseTick = DISEASE_DELAY();
			List<Ability> offensiveEffects = returnOffensiveAffects(mob);
			for (int a = offensiveEffects.size() - 1; a >= 0; a--)
				offensiveEffects.get(a).unInvoke();
			mob.location().show(mob, null, CMMsg.MSG_NOISYMOVEMENT,
					DISEASE_AFFECT());
			MOB diseaser = invoker;
			if (diseaser == null)
				diseaser = mob;
			if (mob.curState().getHitPoints() > 2) {
				mob.maxState().setHitPoints(mob.curState().getHitPoints() - 1);
				CMLib.combat().postDamage(diseaser, mob, this, 1,
						CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1, null);
			}
			return true;
		}
		return true;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		if (affected == null)
			return;
		affectableStats.setStat(CharStats.STAT_CHARISMA,
				affectableStats.getStat(CharStats.STAT_CHARISMA) - 4);
		if (affectableStats.getStat(CharStats.STAT_CHARISMA) <= 0)
			affectableStats.setStat(CharStats.STAT_CHARISMA, 1);
	}

	public void affectCharState(MOB affected, CharState affectableState) {
		if (affected == null)
			return;
		if (!affected.Name().equals(thename)) {
			hp = Integer.MAX_VALUE;
			thename = affected.Name();
		}
		if (affected.curState().getHitPoints() < hp)
			hp = affected.curState().getHitPoints();
		affectableState.setHitPoints(hp);
	}
}
