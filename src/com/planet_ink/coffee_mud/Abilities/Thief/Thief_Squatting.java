package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.LandTitle;
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
public class Thief_Squatting extends ThiefSkill {
	public String ID() {
		return "Thief_Squatting";
	}

	public String name() {
		return "Squatting";
	}

	public String displayText() {
		return "(Squatting)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "SQUAT", "SQUATTING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	protected boolean failed = false;
	protected Room room = null;
	private LandTitle title = null;

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_CRIMINAL;
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			if (msg.source() == mob) {
				if ((msg.target() == mob.location())
						&& (msg.targetMinor() == CMMsg.TYP_LEAVE)) {
					failed = true;
					unInvoke();
				} else if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
						|| (msg.sourceMinor() == CMMsg.TYP_QUIT)) {
					failed = true;
					unInvoke();
				}
			} else if ((CMLib.flags().isStanding(mob))
					|| (mob.location() != room)) {
				failed = true;
				unInvoke();
			}
		}
		super.executeMsg(host, msg);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();

		if ((canBeUninvoked()) && (mob.location() != null)) {
			if ((failed) || (!CMLib.flags().isSitting(mob)) || (room == null)
					|| (title == null) || (mob.location() != room))
				mob.tell("You are no longer squatting.");
			else if (title.getOwnerName().length() > 0) {
				mob.tell("Your squat has succeeded.  This property no longer belongs to "
						+ title.getOwnerName() + ".");
				title.setOwnerName("");
				title.updateTitle();
				title.updateLot(null);
			} else if (title.getOwnerName().length() > 0) {
				mob.tell("Your squat has succeeded.  This property now belongs to you.");
				title.setOwnerName(mob.Name());
				title.updateTitle();
				title.updateLot(new XVector(mob.name()));
			}
		}
		failed = false;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already squatting.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		if (CMLib.law().doesHavePriviledgesHere(mob, mob.location())) {
			mob.tell("This is your place already!");
			return false;
		}
		LandTitle T = CMLib.law().getLandTitle(mob.location());
		boolean confirmed = false;
		for (final Enumeration<Ability> a = mob.location().effects(); a
				.hasMoreElements();) {
			final Ability A = a.nextElement();
			if (A == T)
				confirmed = true;
		}
		if (T == null) {
			mob.tell("This property is not available for sale, and cannot be squatted upon.");
			return false;
		}
		MOB warnMOB = null;
		if (T.getOwnerName().length() > 0) {
			Clan C = CMLib.clans().getClan(T.getOwnerName());
			if (C == null) {
				MOB M = CMLib.players().getLoadPlayer(T.getOwnerName());
				if (M != null)
					warnMOB = M;
			} else {
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null) && (S.mob() != mob)
							&& (S.mob().getClanRole(C.clanID()) != null))
						warnMOB = S.mob();
				}
			}
			if ((warnMOB == null)
					|| (!CMLib.flags().isInTheGame(warnMOB, true))) {
				mob.tell("The owners must be in the game for you to begin squatting.");
				return false;
			}
		}
		if (!confirmed) {
			mob.tell("You cannot squat on an area for sale.");
			return false;
		}
		if (!CMLib.flags().isSitting(mob)) {
			mob.tell("You must be sitting!");
			return false;
		}

		boolean success = proficiencyCheck(mob, 0, auto);

		CMMsg msg = CMClass.getMsg(mob, null, this, auto ? CMMsg.MASK_ALWAYS
				: CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,
				CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,
				CMMsg.MSG_DELICATE_SMALL_HANDS_ACT, auto ? ""
						: "<S-NAME> start(s) squatting.");
		if (!success)
			return beneficialVisualFizzle(mob, null, auto ? ""
					: "<S-NAME> can't seem to get comfortable here.");
		else if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			failed = false;
			room = mob.location();
			title = T;
			beneficialAffect(mob, target, asLevel,
					(CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)));
			if (warnMOB != null)
				warnMOB.tell("You've heard a rumor that someone is squatting on "
						+ T.getOwnerName() + "'s property.");
		}
		return success;
	}
}
