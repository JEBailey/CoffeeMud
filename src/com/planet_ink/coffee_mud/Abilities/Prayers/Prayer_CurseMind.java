package com.planet_ink.coffee_mud.Abilities.Prayers;

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
public class Prayer_CurseMind extends Prayer {
	public String ID() {
		return "Prayer_CurseMind";
	}

	public String name() {
		return "Curse Mind";
	}

	public String displayText() {
		return "(Cursed Mind)";
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

	protected int canAffectCode() {
		return CAN_MOBS;
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
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int adjustment = target.phyStats().level()
				- (mob.phyStats().level() + super.getXLEVELLevel(mob));
		boolean success = proficiencyCheck(mob, -adjustment, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> feel(s) <T-HIS-HER> mind become cursed!"
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to curse the mind of <T-NAMESELF>!^?");
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if ((mob.location().okMessage(mob, msg))
					&& (mob.location().okMessage(mob, msg2))) {
				mob.location().send(mob, msg);
				mob.location().send(mob, msg2);
				if ((msg.value() <= 0) && (msg2.value() <= 0))
					success = maliciousAffect(mob, target, asLevel, 15, -1);
			}
		} else
			return maliciousFizzle(
					mob,
					target,
					"<S-NAME> "
							+ prayForWord(mob)
							+ " to curse the mind of <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
