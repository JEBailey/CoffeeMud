package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Spell_MageClaws extends Spell {
	public String ID() {
		return "Spell_MageClaws";
	}

	public String name() {
		return "Mage Claws";
	}

	public String displayText() {
		return "(Mage Claws spell)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	protected Weapon naturalWeapon = null;

	private boolean freeHands(MOB mob) {
		if ((mob == null) || (mob.fetchWieldedItem() != null)
				|| (mob.fetchHeldItem() != null))
			return false;
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return super.okMessage(myHost, msg);

		MOB mob = (MOB) affected;

		if ((msg.amISource(mob))
				&& (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
				&& (msg.tool() == null) && (freeHands(mob))) {
			if ((naturalWeapon == null) || (naturalWeapon.amDestroyed())) {
				final int level = super.adjustedLevel(mob, 0);
				naturalWeapon = (Weapon) CMClass.getItem("GenWeapon");
				naturalWeapon.setName("a pair of jagged claws");
				naturalWeapon.setWeaponType(Weapon.TYPE_SLASHING);
				naturalWeapon.setWeaponClassification(Weapon.CLASS_NATURAL);
				naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
				naturalWeapon.setUsesRemaining(1000);
				naturalWeapon.basePhyStats().setDamage(level);
				naturalWeapon.basePhyStats().setAttackAdjustment(5 + level);
				naturalWeapon.recoverPhyStats();
			}
			msg.modify(msg.source(), msg.target(), naturalWeapon,
					msg.sourceCode(), msg.sourceMessage(), msg.targetCode(),
					msg.targetMessage(), msg.othersCode(), msg.othersMessage());
		} else if (msg.amISource(mob)
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.tool() instanceof Weapon)
				&& (msg.tool() == naturalWeapon))
			msg.setValue(msg.value() + naturalWeapon.basePhyStats().damage()
					+ super.getXLEVELLevel(mob));
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
						"<S-YOUPOSS> claws return to normal.");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> already <S-HAS-HAVE> mage claws.");
			return false;
		}

		if (!freeHands(target)) {
			mob.tell(target, null, null,
					"<S-NAME> do(es) not have <S-HIS-HER> hands free.");
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
							: "^S<S-NAME> invoke(s) a spell.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location()
						.show(target, null, CMMsg.MSG_OK_VISUAL,
								"<S-NAME> watch(es) <S-HIS-HER> hands turn into brutal claws!");
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke a spell, but fail(s) miserably.");

		// return whether it worked
		return success;
	}
}