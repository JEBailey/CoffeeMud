package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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

@SuppressWarnings("rawtypes")
public class Chant_StarGazing extends Chant {
	public String ID() {
		return "Chant_StarGazing";
	}

	public String name() {
		return "Star Gazing";
	}

	public String displayText() {
		return "(Gazing at the Stars)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	long lastTime = 0;

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();
		if (canBeUninvoked()) {
			if (!mob.amDead()) {
				if (mob.location() != null)
					mob.location().show(mob, null, CMMsg.MSG_OK_ACTION,
							"<S-NAME> end(s) <S-HIS-HER> star gazing.");
				else
					mob.tell("You stop star gazing.");
			}
		}
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		if ((msg.amISource(mob))
				&& (msg.tool() != this)
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL))
				&& ((CMath.bset(msg.sourceMajor(), CMMsg.MASK_MOVE))
						|| (CMath.bset(msg.sourceMajor(), CMMsg.MASK_HANDS))
						|| (CMath.bset(msg.sourceMajor(), CMMsg.MASK_MAGIC)) || (CMath
							.bset(msg.sourceMajor(), CMMsg.MASK_EYES))))
			unInvoke();
		return;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return super.tick(ticking, tickID);

		MOB mob = (MOB) affected;

		if (tickID != Tickable.TICKID_MOB)
			return true;
		if (!mob.isInCombat()) {
			if (!mob.location().getArea().getClimateObj()
					.canSeeTheStars(mob.location())) {
				unInvoke();
				return false;
			}
			if ((System.currentTimeMillis() - lastTime) < 60000)
				return true;
			if (!proficiencyCheck(null, 0, false))
				return true;
			lastTime = System.currentTimeMillis();
			Room room = mob.location();
			int myAlignment = mob.fetchFaction(CMLib.factions().AlignID());
			int total = CMLib.factions().getTotal(CMLib.factions().AlignID());
			int ratePct = (int) Math.round(CMath.mul(total, .01));
			if (CMLib.factions().getAlignPurity(myAlignment,
					Faction.Align.INDIFF) < 99) {
				if (CMLib.factions().getAlignPurity(myAlignment,
						Faction.Align.EVIL) < CMLib.factions().getAlignPurity(
						myAlignment, Faction.Align.GOOD))
					CMLib.factions().postFactionChange(mob, this,
							CMLib.factions().AlignID(), ratePct);
				else
					CMLib.factions().postFactionChange(mob, this,
							CMLib.factions().AlignID(), -ratePct);
				switch (CMLib.dice().roll(1, 10, 0)) {
				case 0:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> whisper(s) to infinity.");
					break;
				case 1:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> learn(s) the patterns of the heavens.");
					break;
				case 2:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> watch(es) a single point of light.");
					break;
				case 3:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> embrace(s) the cosmos.");
					break;
				case 4:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> inhale(s) the heavens.");
					break;
				case 5:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> watch(es) the stars move across the sky.");
					break;
				case 6:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> become(s) one with the universe.");
					break;
				case 7:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> seek(s) the inner beauty of the cosmic order.");
					break;
				case 8:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> expunge(s) <S-HIS-HER> unnatural thoughts.");
					break;
				case 9:
					room.show(mob, null, this, CMMsg.MSG_CONTEMPLATE,
							"<S-NAME> find(s) clarity in the stars.");
					break;
				}
			}
		} else {
			unInvoke();
			return false;
		}
		return super.tick(ticking, tickID);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (mob.isInCombat()) {
			mob.tell("You can't star gaze while in combat!");
			return false;
		}
		if ((mob.location().domainType() & Room.INDOORS) > 0) {
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		if (mob.location().domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER) {
			mob.tell("This magic will not work here.");
			return false;
		}
		if (!mob.location().getArea().getClimateObj()
				.canSeeTheStars(mob.location())) {
			mob.tell("You can't see the stars right now.");
			return false;
		}

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, null, this,
					somanticCastCode(mob, null, auto),
					"^S<S-NAME> begin(s) to gaze at the stars...^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, mob, asLevel, Ability.TICKS_FOREVER);
				helpProficiency(mob, 0);
			}
		} else
			return beneficialVisualFizzle(mob, null,
					"<S-NAME> chant(s) to begin star gazing, but lose(s) concentration.");

		// return whether it worked
		return success;
	}
}
