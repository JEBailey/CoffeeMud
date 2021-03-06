package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Chant_Unbreakable extends Chant {
	public String ID() {
		return "Chant_Unbreakable";
	}

	public String name() {
		return "Unbreakable";
	}

	public String displayText() {
		return "(Unbreakable)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PRESERVING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	protected int maintainCondition = 100;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (!(affected instanceof Item))
			return;
		if (maintainCondition > 0)
			((Item) affected).setUsesRemaining(maintainCondition);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (!(affected instanceof Item))
			return true;
		if (maintainCondition > 0)
			((Item) affected).setUsesRemaining(maintainCondition);
		return true;
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (!super.okMessage(host, msg))
			return false;
		if ((msg.target() == affected)
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS) || ((msg
						.tool() instanceof Ability) && (((Ability) msg.tool())
						.abstractQuality() == Ability.QUALITY_MALICIOUS)))) {
			msg.source().tell(affected.name() + " is unbreakable!");
			return false;
		}

		return true;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (canBeUninvoked()) {
			if (((affected != null) && (affected instanceof Item))
					&& ((((Item) affected).owner() != null) && (((Item) affected)
							.owner() instanceof MOB)))
				((MOB) ((Item) affected).owner()).tell("The enchantment on "
						+ ((Item) affected).name() + " fades.");
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target.name(mob) + " is already unbreakable.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> appear(s) unbreakable!"
							: "^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (!target.subjectToWearAndTear())
					maintainCondition = -1;
				else
					maintainCondition = target.usesRemaining();

				beneficialAffect(mob, target, asLevel, 0);
				mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
						"<T-NAME> is unbreakable!");
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens.");
		// return whether it worked
		return success;
	}
}
