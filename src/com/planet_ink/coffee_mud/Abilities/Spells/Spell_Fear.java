package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_Fear extends Spell {
	public String ID() {
		return "Spell_Fear";
	}

	public String name() {
		return "Fear";
	}

	public String displayText() {
		return "(Afraid)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
	}

	public void unInvoke() {
		MOB M = null;
		MOB oldI = invoker;
		if (affected instanceof MOB)
			M = (MOB) affected;
		super.unInvoke();
		if (M != null) {
			if (!M.isMonster())
				CMLib.commands().postStand(M, true);
			if ((oldI != M) && (oldI != null))
				M.tell(M, oldI, null,
						"You are no longer afraid of <T-NAMESELF>.");
			else
				M.tell("You are no longer afraid.");
		}
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected instanceof MOB)
				&& (invoker != null)
				&& (invoker != affected)
				&& ((((MOB) affected).location() == null) || (!((MOB) affected)
						.location().isInhabitant(invoker)))) {
			unInvoke();
		}
		return super.tick(ticking, tickID);
	}

	public void affectPhyStats(Physical E, PhyStats stats) {
		if ((affected instanceof MOB) && (invoker != null)
				&& (invoker != affected)
				&& (((MOB) affected).getVictim() == invoker)) {
			int xlvl = super.getXLEVELLevel(invoker());
			float f = (float) 0.05 * xlvl;
			stats.setArmor(stats.armor() + 30 + (3 * xlvl));
			stats.setAttackAdjustment((int) Math.round(CMath.mul(
					stats.attackAdjustment(), 0.90 - f)));
			stats.setDamage((int) Math.round(CMath.mul(stats.damage(), 0.90 - f)));
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null) {
			if (!auto)
				mob.tell("There doesn't appear to be anyone here worth scaring.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			for (Iterator f = h.iterator(); f.hasNext();) {
				MOB target = (MOB) f.next();

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB. Then tell everyone else
				// what happened.
				CMMsg msg = CMClass.getMsg(mob, target, this,
						verbalCastCode(mob, target, auto), auto ? ""
								: "^S<S-NAME> scare(s) <T-NAMESELF>.^?");
				CMMsg msg2 = CMClass.getMsg(mob, target, this,
						CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND
								| (auto ? CMMsg.MASK_ALWAYS : 0), null);
				if (((text().toUpperCase().indexOf("WEAK") < 0) || ((mob
						.phyStats().level() / 2) > target.phyStats().level()))
						&& ((mob.location().okMessage(mob, msg)) && ((mob
								.location().okMessage(mob, msg2))))) {
					mob.location().send(mob, msg);
					if (msg.value() <= 0) {
						mob.location().send(mob, msg2);
						if (msg2.value() <= 0) {
							invoker = mob;
							CMLib.commands().postFlee(target, "");
						}
					}
				}
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> attempt(s) a frightening spell, but completely flub(s) it.");

		// return whether it worked
		return success;
	}
}
