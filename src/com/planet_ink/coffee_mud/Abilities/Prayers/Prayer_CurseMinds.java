package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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

@SuppressWarnings("rawtypes")
public class Prayer_CurseMinds extends Prayer {
	public String ID() {
		return "Prayer_CurseMinds";
	}

	public String name() {
		return "Curse Minds";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public String displayText() {
		return "(Cursed Mind)";
	}

	boolean notAgain = false;

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return super.tick(ticking, tickID);

		if (!super.tick(ticking, tickID))
			return false;
		MOB mob = (MOB) affected;
		if (mob.isInCombat()) {
			MOB newvictim = mob.location().fetchRandomInhabitant();
			if (newvictim != mob)
				mob.setVictim(newvictim);
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked())
			mob.tell("Your mind feels less cursed.");
		CMLib.commands().postStand(mob, true);
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,
				affectableStats.getStat(CharStats.STAT_SAVE_MIND) - 50);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null)
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		boolean nothingDone = true;
		if (success) {
			for (Iterator e = h.iterator(); e.hasNext();) {
				MOB target = (MOB) e.next();
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB. Then tell everyone else
				// what happened.
				CMMsg msg = CMClass.getMsg(mob, target, this,
						verbalCastCode(mob, target, auto)
								| CMMsg.MASK_MALICIOUS,
						auto ? "" : "^S<S-NAME> " + prayWord(mob)
								+ " an unholy curse upon <T-NAMESELF>.^?");
				CMMsg msg2 = CMClass.getMsg(mob, target, this,
						CMMsg.MASK_MALICIOUS | CMMsg.TYP_MIND
								| (auto ? CMMsg.MASK_ALWAYS : 0), null);
				if ((target != mob) && (mob.location().okMessage(mob, msg))
						&& (mob.location().okMessage(mob, msg2))) {
					mob.location().send(mob, msg);
					mob.location().send(mob, msg2);
					if ((msg.value() <= 0) && (msg2.value() <= 0)) {
						success = maliciousAffect(mob, target, asLevel, 15, -1);
						mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
								"<S-NAME> look(s) confused!");
					}
					nothingDone = false;
				}
			}
		}

		if (nothingDone)
			return maliciousFizzle(mob, null,
					"<S-NAME> attempt(s) to curse everyone, but flub(s) it.");

		// return whether it worked
		return success;
	}
}
