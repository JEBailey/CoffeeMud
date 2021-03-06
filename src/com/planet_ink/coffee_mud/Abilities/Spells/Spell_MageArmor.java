package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Spell_MageArmor extends Spell {
	public String ID() {
		return "Spell_MageArmor";
	}

	public String name() {
		return "Mage Armor";
	}

	public String displayText() {
		return "(Mage Armor)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
	}

	Armor theArmor = null;

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			if (theArmor != null) {
				theArmor.destroy();
				mob.location().recoverRoomStats();
			}
		super.unInvoke();
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> magical armor fades away.");
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (((MOB) target).freeWearPositions(Wearable.WORN_TORSO,
						(short) 0, (short) 0) == 0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already wearing mage armor.");
			return false;
		}

		if (target.freeWearPositions(Wearable.WORN_TORSO, (short) 0, (short) 0) == 0) {
			mob.tell("You are already wearing something on your torso!");
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
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "A magical breast plate appears around <S-NAME>."
									: "^S<S-NAME> invoke(s) a magical glowing breast plate!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				theArmor = CMClass.getArmor("GlowingMageArmor");
				theArmor.basePhyStats().setArmor(
						theArmor.basePhyStats().armor()
								+ super.getXLEVELLevel(mob));
				theArmor.setLayerAttributes(Armor.LAYERMASK_SEETHROUGH);
				mob.addItem(theArmor);
				theArmor.wearAt(Wearable.WORN_TORSO);
				theArmor.recoverPhyStats();
				success = beneficialAffect(mob, target, asLevel, 0);
				mob.location().recoverRoomStats();
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke magical protection, but fail(s).");

		// return whether it worked
		return success;
	}
}
