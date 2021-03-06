package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Chant_CheetahBurst extends Chant {
	public String ID() {
		return "Chant_CheetahBurst";
	}

	public String name() {
		return "Cheetah Burst";
	}

	public String displayText() {
		return "(Cheetah Burst)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_SHAPE_SHIFTING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int cheetahTick = 3;

	public Chant_CheetahBurst() {
		super();
		cheetahTick = 3;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (cheetahTick == 1)
			affectableStats.setSpeed(affectableStats.speed() + 3.0
					+ CMath.mul(0.1, getXLEVELLevel(invoker())));
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("You begin to slow down to a normal speed.");
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (!(affected instanceof MOB))
			return true;
		MOB mob = (MOB) affected;
		if ((--cheetahTick) == 0) {
			mob.recoverPhyStats();
			cheetahTick = 3;
		} else if (cheetahTick == 1)
			mob.recoverPhyStats();
		mob.curState().adjMovement(
				mob.charStats().getStat(CharStats.STAT_STRENGTH) / 5,
				mob.maxState());
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already at a cheetah's speed.");
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
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) and snarl(s)!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (target.location() == mob.location()) {
					target.location().show(target, null, CMMsg.MSG_OK_ACTION,
							"<S-NAME> gain(s) cheetah-like reflexes!");
					beneficialAffect(mob, target, asLevel, 0);
					Chant_CheetahBurst A = (Chant_CheetahBurst) target
							.fetchEffect(ID());
					if (A != null)
						A.cheetahTick = 3;
				}
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) and snarl(s), but nothing more happens.");

		// return whether it worked
		return success;
	}
}
