package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Chant_MetalMold extends Chant {
	public String ID() {
		return "Chant_MetalMold";
	}

	public String name() {
		return "Metal Mold";
	}

	protected int canTargetCode() {
		return CAN_MOBS | CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	private Item findMobTargetItem(MOB mobTarget) {
		Vector goodPossibilities = new Vector();
		Vector possibilities = new Vector();
		for (int i = 0; i < mobTarget.numItems(); i++) {
			Item item = mobTarget.getItem(i);
			if ((item != null) && (item.subjectToWearAndTear())
					&& (CMLib.flags().isMetal(item))) {
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
		if (mob != null) {
			if ((target instanceof MOB) && (target != mob)) {
				if (findMobTargetItem((MOB) target) == null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB mobTarget = getTarget(mob, commands, givenTarget, true, false);
		Item target = null;
		if (mobTarget != null)
			target = findMobTargetItem(mobTarget);

		if (target == null)
			target = getTarget(mob, mob.location(), givenTarget, commands,
					Wearable.FILTER_ANY);

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success && (target != null) && CMLib.flags().isMetal(target)
				&& target.subjectToWearAndTear()) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> grow(s) moldy!"
									: "^S<S-NAME> chant(s), causing <T-NAMESELF> to get eaten by mold.^?");
			CMMsg msg2 = CMClass.getMsg(mob, mobTarget, this,
					verbalCastCode(mob, mobTarget, auto), null);
			if ((mob.location().okMessage(mob, msg))
					&& ((mobTarget == null) || (mob.location().okMessage(mob,
							msg2)))) {
				mob.location().send(mob, msg);
				if (mobTarget != null)
					mob.location().send(mob, msg2);
				if (msg.value() <= 0) {
					int damage = 2;
					int num = (mob.phyStats().level() + super.getX1Level(mob) + (2 * super
							.getXLEVELLevel(mob))) / 2;
					for (int i = 0; i < num; i++)
						damage += CMLib.dice().roll(1, 2, 2);
					if (CMLib.flags().isABonusItems(target))
						damage = (int) Math.round(CMath.div(damage, 2.0));
					if (target.phyStats().ability() > 0)
						damage = (int) Math.round(CMath.div(damage, 1 + target
								.phyStats().ability()));
					CMLib.combat().postItemDamage(mob, target, null, damage,
							CMMsg.TYP_ACID, null);
				}
			}
		} else if (mobTarget != null)
			return maliciousFizzle(mob, mobTarget,
					"<S-NAME> chant(s) at <T-NAME> for mold, but nothing happens.");
		else if (target != null)
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) at <T-NAME> for mold, but nothing happens.");
		else
			return maliciousFizzle(mob, null,
					"<S-NAME> chant(s) for mold, but nothing happens.");

		// return whether it worked
		return success;
	}
}
