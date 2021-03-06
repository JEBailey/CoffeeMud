package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Spell_Timeport extends Spell {
	public String ID() {
		return "Spell_Timeport";
	}

	public String name() {
		return "Timeport";
	}

	public String displayText() {
		return "(Time Travelling)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected final static int mask = PhyStats.CAN_NOT_TASTE
			| PhyStats.CAN_NOT_SMELL | PhyStats.CAN_NOT_SEE
			| PhyStats.CAN_NOT_HEAR;
	protected final static int mask2 = Integer.MAX_VALUE
			- PhyStats.CAN_SEE_BONUS - PhyStats.CAN_SEE_DARK
			- PhyStats.CAN_SEE_EVIL - PhyStats.CAN_SEE_GOOD
			- PhyStats.CAN_SEE_HIDDEN - PhyStats.CAN_SEE_INFRARED
			- PhyStats.CAN_SEE_INVISIBLE - PhyStats.CAN_SEE_METAL
			- PhyStats.CAN_SEE_SNEAKERS - PhyStats.CAN_SEE_VICTIM;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setSensesMask(mask & mask2);
		affectableStats.setDisposition(PhyStats.IS_NOT_SEEN);
		affectableStats.setDisposition(PhyStats.IS_CLOAKED);
		affectableStats.setDisposition(PhyStats.IS_INVISIBLE);
		affectableStats.setDisposition(PhyStats.IS_HIDDEN);
	}

	public void unInvoke() {
		// undo the affects of this spell
		MOB mob = null;
		Room room = null;
		if ((affected != null) && (canBeUninvoked())
				&& (affected instanceof MOB)) {
			mob = (MOB) affected;
			room = mob.location();
			CMLib.threads().resumeTicking(mob, -1);
		}
		super.unInvoke();
		if (room != null)
			room.show(mob, null, CMMsg.MSG_OK_VISUAL, "<S-NAME> reappear(s)!");
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)) {
			if (!canBeUninvoked()) {
				msg.source().tell("The timeport spell on you fizzles away.");
				affected.delEffect(this);
			} else if ((((msg.sourceMinor() == CMMsg.TYP_QUIT) && (msg.source() == affected))
					|| (msg.sourceMinor() == CMMsg.TYP_SHUTDOWN)
					|| ((msg.targetMinor() == CMMsg.TYP_EXPIRE)) || (msg
						.sourceMinor() == CMMsg.TYP_ROOMRESET))) {
				unInvoke();
			} else if (msg.amISource((MOB) affected))
				if ((!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_ALWAYS))) {
					msg.source()
							.tell("Nothing just happened.  You are time travelling, and can't do that.");
					return false;
				}
		}
		return super.okMessage(myHost, msg);
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

			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), (auto ? ""
							: "^S<S-NAME> speak(s) and gesture(s)") + "!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Room room = mob.location();
				target.makePeace();
				for (int i = 0; i < room.numInhabitants(); i++) {
					MOB M = room.fetchInhabitant(i);
					if ((M != null) && (M.getVictim() == target))
						M.makePeace();
				}
				mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> vanish(es)!");
				CMLib.threads().suspendTicking(target, -1);
				beneficialAffect(mob, target, asLevel, 3);
				Ability A = target.fetchEffect(ID());
				if (A != null)
					CMLib.threads().startTickDown(A, Tickable.TICKID_MOB, 1);
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> incant(s) for awhile, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
