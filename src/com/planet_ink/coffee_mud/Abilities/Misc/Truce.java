package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.Iterator;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.collections.CMList;
import com.planet_ink.coffee_mud.core.collections.Pair;
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
public class Truce extends StdAbility {
	public String ID() {
		return "Truce";
	}

	public String name() {
		return "Truce";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean putInCommandlist() {
		return false;
	}

	private static final String[] triggerStrings = { "DECLARETRUCE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL;
	}

	public boolean truceWithAnyone = false;
	public CMList<Pair<String, Long>> truces = new CMList<Pair<String, Long>>();

	public void setMiscText(String newMiscText) {
		super.setMiscText(CMStrings.capitalizeAndLower(newMiscText));
		truceWithAnyone = ((newMiscText == null) || (newMiscText.trim()
				.length() == 0));
	}

	public Pair<String, Long> getMyPair(final String name) {
		final long now = System.currentTimeMillis();
		for (Iterator<Pair<String, Long>> i = truces.iterator(); i.hasNext();) {
			final Pair<String, Long> p = i.next();
			if (p != null) {
				if ((now - p.second.longValue()) > 30000)
					i.remove();
				else if (p.first.equals(name))
					return p;
			}
		}
		return null;
	}

	public boolean isTruceWith(final String name) {
		if (truceWithAnyone)
			return getMyPair(name) != null;
		else
			return (text().equals(name));
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			if (truceWithAnyone && (msg.sourceMinor() == CMMsg.TYP_DEATH)
					&& (msg.tool() == mob)) {
				final Pair<String, Long> p = getMyPair(msg.source().Name());
				if (p != null)
					p.second = Long.valueOf(System.currentTimeMillis());
				else
					truces.add(new Pair<String, Long>(msg.source().Name(), Long
							.valueOf(System.currentTimeMillis())));
			}
		}
		super.executeMsg(myHost, msg);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.targetMajor(CMMsg.MASK_MALICIOUS))
				&& (((msg.source() == mob) && (msg.target() != null) && (isTruceWith(msg
						.target().Name()))) || ((msg.target() == mob) && (isTruceWith(msg
						.source().Name()))))
				&& (!msg.sourceMajor(CMMsg.MASK_ALWAYS))) {
			msg.source().tell(msg.source(), msg.target(), null,
					"You have made peace with <T-NAMESELF>.");
			msg.source().makePeace();
			if (msg.target() instanceof MOB)
				((MOB) msg.target()).makePeace();
			return false;
		}
		return super.okMessage(myHost, msg);
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
							auto ? ""
									: "^S<S-NAME> make(s) a truce with <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					success = beneficialAffect(mob, target, asLevel, auto ? 3
							: 0);
					Ability A = target.fetchEffect(ID());
					if (A != null)
						A.setMiscText(target.Name());
				}
				target.makePeace();
				if (mob.getVictim() == target)
					mob.makePeace();
			}
		} else
			return maliciousFizzle(
					mob,
					target,
					auto ? ""
							: "^S<S-NAME> tr(ys) to make <T-NAMESELF> fall asleep, but fails.^?");

		// return whether it worked
		return success;
	}
}
