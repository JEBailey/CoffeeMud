package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Chant_Brittle extends Chant {
	public String ID() {
		return "Chant_Brittle";
	}

	public String name() {
		return "Brittle";
	}

	protected int canTargetCode() {
		return CAN_MOBS | CAN_ITEMS;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int oldCondition = -1;
	protected boolean noRecurse = true;

	public void checkBritality(final Physical E) {
		synchronized (this) {
			if ((E instanceof Item) && (!noRecurse)
					&& (((Item) E).subjectToWearAndTear())) {
				noRecurse = true;
				if (oldCondition < ((Item) E).usesRemaining())
					oldCondition = ((Item) E).usesRemaining();
				if (((Item) E).usesRemaining() < oldCondition) {
					Room R = CMLib.map().roomLocation(E);
					if (R != null)
						R.showHappens(CMMsg.MSG_OK_ACTION, E.name()
								+ " is destroyed!");
					((Item) E).destroy();
				}
				noRecurse = false;
			}
		}
	}

	public void affectPhyStats(Physical E, PhyStats stats) {
		super.affectPhyStats(E, stats);
		checkBritality(affected);
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		super.executeMsg(host, msg);
		// checkBritality(affected);
	}

	private Item getItem(MOB mobTarget) {
		Vector goodPossibilities = new Vector();
		Vector possibilities = new Vector();
		for (int i = 0; i < mobTarget.numItems(); i++) {
			Item item = mobTarget.getItem(i);
			if ((item != null) && (item.subjectToWearAndTear())) {
				if (item.amWearingAt(Wearable.IN_INVENTORY))
					possibilities.addElement(item);
				else
					goodPossibilities.addElement(item);
			}
		}
		if (goodPossibilities.size() > 0)
			return (Item) goodPossibilities.elementAt(CMLib.dice().roll(1,
					goodPossibilities.size(), -1));
		else if (possibilities.size() > 0)
			return (Item) possibilities.elementAt(CMLib.dice().roll(1,
					possibilities.size(), -1));
		return null;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (!(target instanceof MOB))
			return Ability.QUALITY_INDIFFERENT;
		if ((mob != null) && (mob != target)) {
			Item I = getItem((MOB) target);
			if (I == null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB mobTarget = getTarget(mob, commands, givenTarget, true, false);
		Item target = null;
		if (mobTarget != null) {
			target = getItem(mobTarget);
			if (target == null)
				return maliciousFizzle(mob, mobTarget,
						"<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens.");
		}

		if (target == null)
			target = getTarget(mob, mob.location(), givenTarget, commands,
					Wearable.FILTER_ANY);

		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		oldCondition = -1;
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> becomes brittle!"
									: "^S<S-NAME> chant(s), causing <T-NAMESELF> to grow brittle!^?");
			CMMsg msg2 = CMClass.getMsg(mob, mobTarget, this,
					verbalCastCode(mob, mobTarget, auto), null);
			if ((mob.location().okMessage(mob, msg))
					&& ((mobTarget == null) || (mob.location().okMessage(mob,
							msg2)))) {
				mob.location().send(mob, msg);
				if (mobTarget != null)
					mob.location().send(mob, msg2);
				if ((msg.value() <= 0) && (msg2.value() <= 0)) {
					if (target.subjectToWearAndTear())
						oldCondition = target.usesRemaining();
					maliciousAffect(mob, target, asLevel, 0, -1);
				}
			}
		} else
			return maliciousFizzle(mob, null,
					"<S-NAME> chant(s), but nothing happens.");

		// return whether it worked
		return success;
	}
}
