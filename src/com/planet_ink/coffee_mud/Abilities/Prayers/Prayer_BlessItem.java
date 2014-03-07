package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Coins;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Prayer_BlessItem extends Prayer implements MendingSkill {
	public String ID() {
		return "Prayer_BlessItem";
	}

	public String name() {
		return "Bless Item";
	}

	public String displayText() {
		return "(Blessed)";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_BLESSING;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOOD);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_BONUS);
		if (affected instanceof MOB)
			affectableStats.setArmor((affectableStats.armor() - 5)
					- ((affected.phyStats().level() / 10) + (2 * super
							.getXLEVELLevel(invoker()))));
		else if (affected instanceof Item)
			affectableStats.setAbility(affectableStats.ability() + 1);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB)) {
			if (canBeUninvoked())
				if ((affected instanceof Item)
						&& (((Item) affected).owner() != null)
						&& (((Item) affected).owner() instanceof MOB))
					((MOB) ((Item) affected).owner()).tell("The blessing on "
							+ ((Item) affected).name() + " fades.");
			super.unInvoke();
			return;
		}
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			mob.tell("Your aura of blessing fades.");
		super.unInvoke();
	}

	public boolean supportsMending(Physical item) {
		return (item instanceof Item)
				&& (CMLib.flags().domainAffects(item, Ability.DOMAIN_CURSING)
						.size() > 0);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				Item I = Prayer_Bless.getSomething((MOB) target, true);
				if (I == null)
					I = Prayer_Bless.getSomething((MOB) target, false);
				if (I == null)
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
			target = Prayer_Bless.getSomething(mobTarget, true);
		if ((target == null) && (mobTarget != null))
			target = Prayer_Bless.getSomething(mobTarget, false);
		if (target == null)
			target = getTarget(mob, mob.location(), givenTarget, commands,
					Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if (target instanceof Coins) {
			mob.tell("You can not bless that.");
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
					(auto ? "<T-NAME> appear(s) blessed!"
							: "^S<S-NAME> bless(es) <T-NAMESELF>"
									+ inTheNameOf(mob) + ".^?")
							+ CMLib.protocol().msp("bless.wav", 10));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Prayer_Bless.endLowerCurses(target, CMLib.ableMapper()
						.lowestQualifyingLevel(ID()));
				beneficialAffect(mob, target, asLevel, 0);
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob) + " for blessings, but nothing happens.");
		// return whether it worked
		return success;
	}
}