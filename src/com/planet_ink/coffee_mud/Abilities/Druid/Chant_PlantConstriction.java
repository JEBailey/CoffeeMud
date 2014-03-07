package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Chant_PlantConstriction extends Chant {
	public String ID() {
		return "Chant_PlantConstriction";
	}

	public String name() {
		return "Plant Constriction";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
	}

	public String displayText() {
		return "(Plant Constriction)";
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(10);
	}

	public int minRange() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public boolean bubbleAffect() {
		return true;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public void unInvoke() {
		Item I = null;
		if (affected instanceof Item)
			I = (Item) affected;
		super.unInvoke();
		if ((canBeUninvoked()) && (I != null) && (I.owner() instanceof MOB)
				&& (!I.amWearingAt(Wearable.IN_INVENTORY))) {
			MOB mob = (MOB) I.owner();
			if ((!mob.amDead()) && (CMLib.flags().isInTheGame(mob, false))) {
				mob.tell(I.name(mob)
						+ " loosens its grip on you and falls off.");
				I.setRawWornCode(0);
				mob.location().moveItemTo(I, ItemPossessor.Expire.Player_Drop);
			}
		}
	}

	public boolean tick(Tickable ticking, int tickID) {
		Item I = null;
		if (affected instanceof Item)
			I = (Item) affected;
		if ((canBeUninvoked())
				&& (I != null)
				&& (I.owner() instanceof MOB)
				&& (I.amWearingAt(Wearable.WORN_LEGS) || I
						.amWearingAt(Wearable.WORN_ARMS))) {
			MOB mob = (MOB) I.owner();
			if ((!mob.amDead()) && (mob.isMonster())
					&& (CMLib.flags().isInTheGame(mob, false)))
				CMLib.commands().postRemove(mob, I, false);
		}
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (!super.okMessage(host, msg))
			return false;
		if ((msg.targetMinor() == CMMsg.TYP_REMOVE)
				&& (msg.target() == affected)
				&& (affected instanceof Item)
				&& (((Item) affected).amWearingAt(Wearable.WORN_LEGS) || ((Item) affected)
						.amWearingAt(Wearable.WORN_ARMS))) {
			if (CMLib.dice().rollPercentage() > (msg.source().charStats()
					.getStat(CharStats.STAT_STRENGTH) * 4)) {
				msg.source()
						.location()
						.show(msg.source(), affected, CMMsg.MSG_OK_VISUAL,
								"<S-NAME> struggle(s) to remove <T-NAME> and fail(s).");
				return false;
			}
		}
		return true;
	}

	public void affectPhyStats(Physical aff, PhyStats affectableStats) {
		if ((aff instanceof MOB) && (affected instanceof Item)
				&& (((MOB) aff).isMine(affected))
				&& ((Item) affected).amWearingAt(Wearable.WORN_ARMS))
			affectableStats.setSpeed(affectableStats.speed() / 2.0);
	}

	public void affectCharState(MOB aff, CharState affectableState) {
		if ((affected instanceof Item) && (aff.isMine(affected))
				&& ((Item) affected).amWearingAt(Wearable.WORN_LEGS))
			affectableState.setMovement(affectableState.getMovement() / 2);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Item myPlant = Druid_MyPlants.myPlant(mob.location(), mob, 0);
			if (myPlant == null)
				return Ability.QUALITY_INDIFFERENT;
			if (target instanceof MOB) {
				Vector positionChoices = new Vector();
				if (((MOB) target).getWearPositions(Wearable.WORN_ARMS) > 0)
					positionChoices
							.addElement(Long.valueOf(Wearable.WORN_ARMS));
				if (((MOB) target).getWearPositions(Wearable.WORN_LEGS) > 0)
					positionChoices
							.addElement(Long.valueOf(Wearable.WORN_LEGS));
				if (positionChoices.size() == 0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		Item myPlant = Druid_MyPlants.myPlant(mob.location(), mob, 0);
		if (myPlant == null) {
			if (auto)
				myPlant = Chant_SummonPlants.buildPlant(mob, mob.location());
			else {
				mob.tell("There doesn't appear to be any of your plants here to choke with.");
				return false;
			}
		}
		Vector positionChoices = new Vector();
		if (target.getWearPositions(Wearable.WORN_ARMS) > 0)
			positionChoices.addElement(Long.valueOf(Wearable.WORN_ARMS));
		if (target.getWearPositions(Wearable.WORN_LEGS) > 0)
			positionChoices.addElement(Long.valueOf(Wearable.WORN_LEGS));
		if (positionChoices.size() == 0) {
			if (!auto)
				mob.tell("Ummm, " + target.name(mob)
						+ " doesn't have arms or legs to constrict...");
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

			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> chant(s) at <T-NAME> while pointing at "
											+ myPlant.name() + "!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				target.moveItemTo(myPlant);
				Long II = (Long) positionChoices.elementAt(CMLib.dice().roll(1,
						positionChoices.size(), -1));
				myPlant.setRawWornCode(II.longValue());
				if (II.longValue() == Wearable.WORN_ARMS)
					mob.location()
							.show(target,
									null,
									CMMsg.MSG_OK_VISUAL,
									myPlant.name()
											+ " jumps up and wraps itself around <S-YOUPOSS> arms!");
				else
					mob.location()
							.show(target,
									null,
									CMMsg.MSG_OK_VISUAL,
									myPlant.name()
											+ " jumps up and wraps itself around <S-YOUPOSS> legs!");
				beneficialAffect(mob, myPlant, asLevel, 20);
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) at <T-NAME>, but the magic fizzles.");

		// return whether it worked
		return success;
	}
}