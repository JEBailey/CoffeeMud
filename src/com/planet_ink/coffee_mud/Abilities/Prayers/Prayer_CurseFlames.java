package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Prayer_CurseFlames extends Prayer {
	public String ID() {
		return "Prayer_CurseFlames";
	}

	public String name() {
		return "Curse Flames";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY | Ability.FLAG_FIREBASED;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(5);
	}

	public int minRange() {
		return 0;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (getFireSource((MOB) target) == null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	private Item getFireSource(MOB target) {
		Item fireSource = null;
		for (int i = 0; i < target.numItems(); i++) {
			Item I = target.getItem(i);
			if ((CMLib.flags().isOnFire(I)) && (I.container() == null)) {
				fireSource = I;
				break;
			}
		}

		if (fireSource == null)
			for (int i = 0; i < target.location().numItems(); i++) {
				Item I = target.location().getItem(i);
				if ((CMLib.flags().isOnFire(I)) && (I.container() == null)) {
					fireSource = I;
					break;
				}
			}
		return fireSource;
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

		Item fireSource = getFireSource(target);

		if ((success) && (fireSource != null)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), (auto ? "Suddenly "
							+ fireSource.name()
							+ " flares up and attacks <T-HIM-HER>!^?"
							: "^S<S-NAME> point(s) at <T-NAMESELF> and "
									+ prayWord(mob) + ".  Suddenly "
									+ fireSource.name()
									+ " flares up and attacks <T-HIM-HER>!^?")
							+ CMLib.protocol().msp("fireball.wav", 40));
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_FIRE
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if ((mob.location().okMessage(mob, msg))
					&& ((mob.location().okMessage(mob, msg2)))) {
				mob.location().send(mob, msg);
				mob.location().send(mob, msg2);
				int damage = CMLib.dice().roll(1,
						2 * adjustedLevel(mob, asLevel),
						(2 * super.getX1Level(mob)) + 1);
				if ((msg.value() > 0) || (msg2.value() > 0))
					damage = (int) Math.round(CMath.div(damage, 2.0));

				if (target.location() == mob.location())
					CMLib.combat().postDamage(mob, target, this, damage,
							CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE,
							Weapon.TYPE_BURNING,
							"The flames <DAMAGE> <T-NAME>!");
			}
			fireSource.destroy();
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> point(s) at <T-NAMESELF> and " + prayWord(mob)
							+ ", but nothing happens.");

		// return whether it worked
		return success;
	}
}