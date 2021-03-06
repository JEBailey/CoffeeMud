package com.planet_ink.coffee_mud.Abilities.Spells;

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
public class Spell_RepairingAura extends Spell {
	public String ID() {
		return "Spell_RepairingAura";
	}

	public String name() {
		return "Repairing Aura";
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int overrideMana() {
		return 50;
	}

	public static final int REPAIR_MAX = 30;
	public int repairDown = REPAIR_MAX;
	public int adjustedLevel = 1;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_BONUS);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		repairDown -= adjustedLevel;
		if ((repairDown <= 0) && (affected instanceof Item)) {
			repairDown = REPAIR_MAX;
			Item I = (Item) affected;
			if ((I.subjectToWearAndTear()) && (I.usesRemaining() < 100)) {
				if (I.owner() instanceof Room)
					((Room) I.owner()).showHappens(CMMsg.MSG_OK_VISUAL, I,
							"<S-NAME> is magically repairing itself.");
				else if (I.owner() instanceof MOB)
					((MOB) I.owner()).tell(I.name()
							+ " is magically repairing itself.");
				I.setUsesRemaining(I.usesRemaining() + 1);
			}
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target.name(mob) + " is already repairing!");
			return false;
		}
		if ((!(target instanceof Item)) && (!(target instanceof MOB))) {
			mob.tell(target.name(mob) + " would not be affected by this spell.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		Item realTarget = null;
		if (target instanceof Item)
			realTarget = (Item) target;
		else if (target instanceof MOB) {
			Vector choices = new Vector();
			Vector inventory = new Vector();
			MOB M = (MOB) target;
			Item I = null;
			for (int i = 0; i < M.numItems(); i++) {
				I = M.getItem(i);
				if ((I != null) && (I.subjectToWearAndTear())
						&& (I.fetchEffect(ID()) == null)) {
					if (I.amWearingAt(Wearable.IN_INVENTORY))
						inventory.addElement(I);
					else
						choices.addElement(I);
				}
			}
			Vector chooseFrom = inventory;
			if (choices.size() < 3)
				inventory.addAll(choices);
			else
				chooseFrom = choices;
			if (chooseFrom.size() < 1)
				success = false;
			else
				realTarget = (Item) chooseFrom.elementAt(CMLib.dice().roll(1,
						chooseFrom.size(), -1));
		}

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?");
			CMMsg msg2 = (target == realTarget) ? null : CMClass.getMsg(mob,
					target, this, verbalCastCode(mob, target, auto), null);
			if (mob.location().okMessage(mob, msg) && (realTarget != null)
					&& ((msg2 == null) || mob.location().okMessage(mob, msg2))) {
				mob.location().send(mob, msg);
				if (msg2 != null)
					mob.location().send(mob, msg2);
				mob.location().show(mob, realTarget, CMMsg.MSG_OK_ACTION,
						"<T-NAME> attain(s) a repairing aura.");
				beneficialAffect(mob, realTarget, asLevel, 0);
				Spell_RepairingAura A = (Spell_RepairingAura) realTarget
						.fetchEffect(ID());
				if (A != null)
					A.adjustedLevel = adjustedLevel(mob, asLevel);
				realTarget.recoverPhyStats();
				mob.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		} else
			beneficialWordsFizzle(
					mob,
					target,
					"<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting, but nothing happens.");

		// return whether it worked
		return success;
	}
}
