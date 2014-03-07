package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.Deity;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Prayer_InfuseHoliness extends Prayer {
	public String ID() {
		return "Prayer_InfuseHoliness";
	}

	public String name() {
		return "Infuse Holiness";
	}

	public String displayText() {
		return "(Infused Holiness)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS
				| Ability.CAN_EXITS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS
				| Ability.CAN_EXITS;
	}

	protected int serviceRunning = 0;

	public int abilityCode() {
		return serviceRunning;
	}

	public void setAbilityCode(int newCode) {
		serviceRunning = newCode;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOOD);
		if (CMath.bset(affectableStats.disposition(), PhyStats.IS_EVIL))
			affectableStats.setDisposition(affectableStats.disposition()
					- PhyStats.IS_EVIL);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if ((affected == null))
			return;
		if (canBeUninvoked())
			if (affected instanceof MOB)
				((MOB) affected).tell("Your infused holiness fades.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (serviceRunning == 0)
			return super.okMessage(myHost, msg);
		if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) == CMMsg.MASK_MALICIOUS)
				&& (msg.target() instanceof MOB)) {
			if (msg.source().getWorshipCharID()
					.equalsIgnoreCase(((MOB) msg.target()).getWorshipCharID())) {
				msg.source().tell("Not right now -- you're in a service.");
				msg.source().makePeace();
				((MOB) msg.target()).makePeace();
				return false;
			}
		}
		if ((msg.sourceMinor() == CMMsg.TYP_LEAVE)
				&& (msg.source().isMonster())) {
			msg.source().tell("Not right now -- you're in a service.");
			return false;
		}
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_ANY);
		if (target == null) {
			if ((CMLib.law().doesOwnThisProperty(mob, mob.location()))
					&& (CMParms.combine(commands, 0).equalsIgnoreCase("room") || CMParms
							.combine(commands, 0).equalsIgnoreCase("here")))
				target = mob.location();
			else
				return false;
		}

		Deity D = null;
		if (CMLib.law().getClericInfusion(target) != null) {

			if (target instanceof Room)
				D = CMLib.law().getClericInfused((Room) target);
			if (D != null)
				mob.tell("There is already an infused aura of " + D.Name()
						+ " around " + target.name(mob) + ".");
			else
				mob.tell("There is already an infused aura around "
						+ target.name(mob) + ".");
			return false;
		}

		D = mob.getMyDeity();
		if (target instanceof Room) {
			if (D == null) {
				mob.tell("The faithless may not infuse holiness in a room.");
				return false;
			}
			Area A = mob.location().getArea();
			Room R = null;
			for (Enumeration e = A.getMetroMap(); e.hasMoreElements();) {
				R = (Room) e.nextElement();
				if (CMLib.law().getClericInfused((Room) target) == D) {
					mob.tell("There is already a holy place of " + D.Name()
							+ " in this area at " + R.displayText(mob) + ".");
					return false;
				}
			}
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "A holy aura appears around <T-NAME>."
									: "^S<S-NAME> "
											+ prayForWord(mob)
											+ " to infuse a holy aura around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (D != null)
					setMiscText(D.Name());
				if ((target instanceof Room)
						&& (CMLib.law().doesOwnThisProperty(mob,
								((Room) target)))) {
					target.addNonUninvokableEffect((Ability) this.copyOf());
					CMLib.database().DBUpdateRoom((Room) target);
				} else
					beneficialAffect(mob, target, asLevel, 0);
				target.recoverPhyStats();
			}
		} else
			beneficialWordsFizzle(mob, target, "<S-NAME> " + prayForWord(mob)
					+ " to infuse a holy aura in <T-NAMESELF>, but fail(s).");

		return success;
	}
}