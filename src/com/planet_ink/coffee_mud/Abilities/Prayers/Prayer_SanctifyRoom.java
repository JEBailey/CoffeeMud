package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.SHashSet;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Prayer_SanctifyRoom extends Prayer {
	public String ID() {
		return "Prayer_SanctifyRoom";
	}

	public String name() {
		return "Sanctify Room";
	}

	public String displayText() {
		return "(Sanctify Room)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_WARDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_ROOMS;
	}

	protected int canTargetCode() {
		return CAN_ROOMS;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	public static final SHashSet MSG_CODESH = new SHashSet(new Integer[] {
			Integer.valueOf(CMMsg.TYP_GET), Integer.valueOf(CMMsg.TYP_PULL),
			Integer.valueOf(CMMsg.TYP_PUSH),
			Integer.valueOf(CMMsg.TYP_CAST_SPELL) });

	protected boolean inRoom(MOB mob, Room R) {
		if (!CMLib.law().doesAnyoneHavePrivilegesHere(mob, text(), R)) {
			mob.tell("You feel your muscles unwilling to cooperate.");
			return false;
		}
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected == null)
			return super.okMessage(myHost, msg);

		Room R = (Room) affected;
		int targMinor = msg.targetMinor();
		if (((targMinor == CMMsg.TYP_GET) || (targMinor == CMMsg.TYP_PULL)
				|| (targMinor == CMMsg.TYP_PUSH) || (targMinor == CMMsg.TYP_CAST_SPELL))
				&& (msg.target() instanceof Item)
				&& (!msg.targetMajor(CMMsg.MASK_INTERMSG))
				&& (!msg.source().isMine(msg.target()))
				&& ((!(msg.tool() instanceof Item)) || (!msg.source().isMine(
						msg.tool()))))
			return inRoom(msg.source(), R);
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if (target instanceof MOB) {
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = mob.location();
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell("This place is already a sanctified place.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to sanctify this place.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				setMiscText(mob.Name());

				if ((target instanceof Room)
						&& (CMLib.law().doesOwnThisProperty(mob,
								((Room) target)))) {
					String landOwnerName = CMLib.law().getLandOwnerName(
							(Room) target);
					if ((CMLib.clans().getClan(landOwnerName) != null)
							&& (!CMLib
									.clans()
									.getClan(landOwnerName)
									.getMorgue()
									.equals(CMLib.map().getExtendedRoomID(
											(Room) target)))) {
						setMiscText(landOwnerName);
						beneficialAffect(mob, target, asLevel, 0);
					} else {
						target.addNonUninvokableEffect((Ability) this.copyOf());
						CMLib.database().DBUpdateRoom((Room) target);
					}
				} else
					beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target, "<S-NAME> " + prayForWord(mob)
					+ " to sanctify this place, but <S-IS-ARE> not answered.");

		return success;
	}
}
