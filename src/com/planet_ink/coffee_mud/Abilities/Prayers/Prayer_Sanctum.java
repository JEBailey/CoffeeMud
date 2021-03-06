package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
public class Prayer_Sanctum extends Prayer {
	public String ID() {
		return "Prayer_Sanctum";
	}

	public String name() {
		return "Sanctum";
	}

	public String displayText() {
		return "(Sanctum)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_WARDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_ROOMS;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

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
		if ((msg.targetMinor() == CMMsg.TYP_ENTER)
				&& (msg.target() == R)
				&& (!msg.source().Name().equals(text()))
				&& (msg.source().getClanRole(text()) == null)
				&& ((msg.source().amFollowing() == null) || ((!msg.source()
						.amFollowing().Name().equals(text())) && (msg.source()
						.amFollowing().getClanRole(text()) == null)))
				&& (!CMLib.law().doesHavePriviledgesHere(msg.source(), R))) {
			msg.source().tell("You feel your muscles unwilling to cooperate.");
			return false;
		}
		if ((CMath.bset(msg.sourceMajor(), CMMsg.MASK_MALICIOUS))
				|| (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				|| (CMath.bset(msg.othersMajor(), CMMsg.MASK_MALICIOUS))) {
			if ((msg.source() != null) && (msg.target() != null)
					&& (msg.source() != affected)
					&& (msg.source() != msg.target())) {
				if (affected instanceof MOB) {
					MOB mob = (MOB) affected;
					if ((CMLib.flags().aliveAwakeMobile(mob, true))
							&& (!mob.isInCombat())) {
						String t = "No fighting!";
						if (text().indexOf(';') > 0) {
							List<String> V = CMParms.parseSemicolons(text(),
									true);
							t = V.get(CMLib.dice().roll(1, V.size(), -1));
						}
						CMLib.commands().postSay(mob, msg.source(), t, false,
								false);
					} else
						return super.okMessage(myHost, msg);
				} else {
					String t = "You feel too peaceful here.";
					if (text().indexOf(';') > 0) {
						List<String> V = CMParms.parseSemicolons(text(), true);
						t = V.get(CMLib.dice().roll(1, V.size(), -1));
					}
					msg.source().tell(t);
				}
				MOB victim = msg.source().getVictim();
				if (victim != null)
					victim.makePeace();
				msg.source().makePeace();
				msg.modify(msg.source(), msg.target(), msg.tool(),
						CMMsg.NO_EFFECT, "", CMMsg.NO_EFFECT, "",
						CMMsg.NO_EFFECT, "");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = mob.location();
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell("This place is already a sanctum.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to make this place a sanctum.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				setMiscText(mob.Name());

				if ((target instanceof Room)
						&& (CMLib.law().doesOwnThisProperty(mob,
								((Room) target)))) {
					String landOwnerName = CMLib.law().getLandOwnerName(
							(Room) target);
					if (CMLib.clans().getClan(landOwnerName) != null) {
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
			beneficialWordsFizzle(
					mob,
					target,
					"<S-NAME> "
							+ prayForWord(mob)
							+ " to make this place a sanctum, but <S-IS-ARE> not answered.");

		return success;
	}
}
