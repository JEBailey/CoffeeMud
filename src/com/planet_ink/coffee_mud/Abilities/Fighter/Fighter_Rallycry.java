package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Fighter_Rallycry extends FighterSkill {
	public String ID() {
		return "Fighter_Rallycry";
	}

	public String name() {
		return "Rally Cry";
	}

	public String displayText() {
		return "(Rally Cry)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	private static final String[] triggerStrings = { "RALLYCRY" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_SINGING;
	}

	protected int timesTicking = 0;
	protected int hpUp = 0;

	public void affectCharState(MOB affected, CharState affectableStats) {
		super.affectCharState(affected, affectableStats);
		if (invoker == null)
			return;
		affectableStats.setHitPoints(affectableStats.getHitPoints() + hpUp);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((affected == null) || (invoker == null)
				|| (!(affected instanceof MOB)))
			return false;
		if ((!((MOB) affected).isInCombat()) && (++timesTicking > 5))
			unInvoke();
		return true;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked()) {
			mob.tell("You feel less rallied.");
			mob.recoverMaxState();
			if (mob.curState().getHitPoints() > mob.maxState().getHitPoints())
				mob.curState().setHitPoints(mob.maxState().getHitPoints());
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_SPEAK,
					auto ? ""
							: "^S<S-NAME> scream(s) a mighty RALLYING CRY!!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Set<MOB> h = properTargets(mob, givenTarget, auto);
				if (h == null)
					return false;
				for (Iterator e = h.iterator(); e.hasNext();) {
					MOB target = (MOB) e.next();
					target.location().show(target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> seem(s) rallied!");
					timesTicking = 0;
					hpUp = mob.phyStats().level() + (2 * getXLEVELLevel(mob));
					beneficialAffect(mob, target, asLevel, 0);
					target.recoverMaxState();
					if (target.fetchEffect(ID()) != null)
						mob.curState().adjHitPoints(hpUp, mob.maxState());
				}
			}
		} else
			beneficialWordsFizzle(mob, null, auto ? ""
					: "<S-NAME> mumble(s) a weak rally cry.");

		// return whether it worked
		return success;
	}
}
