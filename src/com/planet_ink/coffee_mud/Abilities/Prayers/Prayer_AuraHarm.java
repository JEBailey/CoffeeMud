package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.HashSet;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Prayer_AuraHarm extends Prayer {
	public String ID() {
		return "Prayer_AuraHarm";
	}

	public String name() {
		return "Aura of Harm";
	}

	public String displayText() {
		return "(Harm Aura)";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_VEXING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	private int damageTickDown = 4;

	public Prayer_AuraHarm() {
		super();

		damageTickDown = 4;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if ((affected == null) || (!(affected instanceof Room)))
			return;
		Room R = (Room) affected;

		super.unInvoke();

		if (canBeUninvoked())
			R.showHappens(CMMsg.MSG_OK_VISUAL,
					"The harmful aura around you fades.");
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected == null) || (!(affected instanceof Room)))
			return super.tick(ticking, tickID);

		if ((--damageTickDown) >= 0)
			return super.tick(ticking, tickID);
		damageTickDown = 4;

		HashSet H = null;
		if ((invoker() != null) && (invoker().location() == affected)) {
			H = new HashSet();
			invoker().getGroupMembers(H);
		}
		Room R = (Room) affected;
		for (int i = 0; i < R.numInhabitants(); i++) {
			MOB M = R.fetchInhabitant(i);
			if ((M != null) && ((H == null) || (!H.contains(M)))) {
				if (invoker() != null) {
					int harming = CMLib.dice().roll(1,
							adjustedLevel(invoker(), 0) / 3, 1);
					CMLib.combat().postDamage(invoker(), M, this, harming,
							CMMsg.MASK_MALICIOUS | CMMsg.TYP_UNDEAD,
							Weapon.TYPE_BURSTING,
							"The unholy aura <DAMAGE> <T-NAME>!");
				} else {
					int harming = CMLib.dice().roll(1,
							CMLib.ableMapper().lowestQualifyingLevel(ID()) / 3,
							1);
					CMLib.combat().postDamage(M, M, this, harming,
							CMMsg.MASK_MALICIOUS | CMMsg.TYP_UNDEAD,
							Weapon.TYPE_BURSTING,
							"The unholy aura <DAMAGE> <T-NAME>!");
				}
				if ((!M.isInCombat()) && (M.isMonster()) && (M != invoker)
						&& (invoker != null)
						&& (M.location() == invoker.location())
						&& (M.location().isInhabitant(invoker))
						&& (CMLib.flags().canBeSeenBy(invoker, M)))
					CMLib.combat().postAttack(M, invoker, M.fetchWieldedItem());
			}
		}
		return super.tick(ticking, tickID);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof Room) {
				if (!mob.isInCombat())
					return super.castingQuality(mob, target,
							Ability.QUALITY_INDIFFERENT);
				if (mob.charStats().getMyRace().racialCategory()
						.equals("Undead"))
					return super.castingQuality(mob, target,
							Ability.QUALITY_BENEFICIAL_SELF);
				return super.castingQuality(mob, target,
						Ability.QUALITY_MALICIOUS);
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Room target = mob.location();
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell("The aura of harm is already here.");
			return false;
		}
		if (target.fetchEffect("Prayer_AuraHeal") != null) {
			target.fetchEffect("Prayer_AuraHeal").unInvoke();
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
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> " + prayWord(mob)
									+ " for all to feel pain.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"A harmful aura descends over the area!");
				maliciousAffect(mob, target, asLevel, 0, -1);
			}
		} else
			return maliciousFizzle(
					mob,
					target,
					"<S-NAME> "
							+ prayWord(mob)
							+ " for an aura of harm, but <S-HIS-HER> plea is not answered.");

		// return whether it worked
		return success;
	}
}
