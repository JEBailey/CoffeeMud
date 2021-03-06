package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
@SuppressWarnings("rawtypes")
public class Spell_FeatherFall extends Spell {
	public String ID() {
		return "Spell_FeatherFall";
	}

	public String name() {
		return "Feather Fall";
	}

	public String displayText() {
		return "(Feather Fall)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setWeight(0);
	}

	public int mobWeight(MOB mob) {
		int weight = mob.baseWeight();
		for (int i = 0; i < mob.numItems(); i++) {
			Item I = mob.getItem(i);
			if ((I != null) && (!I.amWearingAt(Wearable.WORN_FLOATING_NEARBY)))
				weight += I.phyStats().weight();
		}
		return weight;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (msg.amISource((MOB) affected))
				&& (msg.targetMinor() == CMMsg.TYP_GET)
				&& (msg.target() instanceof Item)
				&& (((msg.tool() == null) || (msg.tool() instanceof MOB)))) {
			MOB mob = msg.source();
			if ((((Item) msg.target()).phyStats().weight() > (mob.maxCarry() - mobWeight(mob)))
					&& (!mob.isMine(msg.target()))) {
				mob.tell(((Item) msg.target()).name(mob) + " is too heavy.");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();

		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> normal weight returns.");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

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
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> become(s) very light!"
									: "^S<S-NAME> invoke(s) immediate lightness upon <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke a spell upon <T-NAMESELF>, but nothing happens.");
		// return whether it worked
		return success;
	}
}
