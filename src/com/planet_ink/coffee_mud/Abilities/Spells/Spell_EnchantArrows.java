package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Ammunition;
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
@SuppressWarnings("rawtypes")
public class Spell_EnchantArrows extends Spell {
	public String ID() {
		return "Spell_EnchantArrows";
	}

	public String name() {
		return "Enchant Arrows";
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
	}

	protected int overridemana() {
		return Ability.COST_ALL;
	}

	public long flags() {
		return Ability.FLAG_NOORDERING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public void affectPhyStats(Physical host, PhyStats affectableStats) {
		affectableStats.setAbility(affectableStats.ability()
				+ CMath.s_int(text()));
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_BONUS);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = super.getTarget(mob, mob.location(), givenTarget,
				commands, Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if ((!(target instanceof Ammunition))
				|| (!((Ammunition) target).ammunitionType().equalsIgnoreCase(
						"arrows"))) {
			mob.tell(mob, target, null,
					"You can't enchant <T-NAME> ith an Enchant Arrows spell!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int experienceToLose = getXPCOSTAdjustment(mob, 5);
		CMLib.leveler().postExperience(mob, null, null, -experienceToLose,
				false);
		mob.tell("The effort causes you to lose " + experienceToLose
				+ " experience.");

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> hold(s) <T-NAMESELF> and cast(s) a spell.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability A = target.fetchEffect(ID());
				if ((A != null) && (CMath.s_int(A.text()) > 2))
					mob.tell("You are not able to enchant " + target.name(mob)
							+ " further.");
				else {
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							"<T-NAME> glows!");
					if (A == null) {
						A = (Ability) copyOf();
						target.addNonUninvokableEffect(A);
					}
					A.setMiscText("" + (CMath.s_int(A.text()) + 1));
					target.recoverPhyStats();
					mob.recoverPhyStats();
				}
			}

		} else
			beneficialWordsFizzle(
					mob,
					target,
					"<S-NAME> hold(s) <T-NAMESELF> tightly and whisper(s), but fail(s) to cast a spell.");

		// return whether it worked
		return success;
	}
}
