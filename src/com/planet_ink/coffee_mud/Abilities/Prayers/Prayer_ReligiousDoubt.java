package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Libraries.interfaces.TimeManager;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Prayer_ReligiousDoubt extends Prayer {
	public static final long DOUBT_TIME = TimeManager.MILI_HOUR;

	public String ID() {
		return "Prayer_ReligiousDoubt";
	}

	public String name() {
		return "Religious Doubt";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
	}

	public String displayText() {
		if (otherSide)
			return "";
		return "(Religious Doubt)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int tickUp = 0;
	protected boolean otherSide = false;

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		if (super.canBeUninvoked()) {
			if (!otherSide)
				affectableStats.setStat(CharStats.STAT_FAITH,
						affectableStats.getStat(CharStats.STAT_FAITH) - 100);
			else
				affectableStats.setStat(CharStats.STAT_FAITH,
						affectableStats.getStat(CharStats.STAT_FAITH) + 100);
		}
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((tickID == Tickable.TICKID_MOB) && (super.canBeUninvoked())) {
			boolean oldOther = otherSide;
			otherSide = (++tickUp) > tickDown;
			if ((oldOther != otherSide) && (affected instanceof MOB))
				((MOB) affected).recoverCharStats();
		}
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (otherSide)
			return true;
		if (msg.target() == affected) {
			if (!(affected instanceof MOB))
				return true;
			if ((msg.source() != msg.target())
					&& (msg.tool() instanceof Ability)
					&& (msg.tool().ID().equalsIgnoreCase("Skill_Convert"))) {
				msg.source()
						.tell((MOB) msg.target(), null, null,
								"<S-NAME> is not interested in hearing your religious beliefs.");
				return false;
			}
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

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
									+ " for <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location()
						.show(target,
								null,
								CMMsg.MSG_OK_VISUAL,
								"<S-NAME> <S-IS-ARE> questioning <S-HIS-HER> faith, but does not seem convinced yet.");
				beneficialAffect(mob, target, asLevel,
						(int) (DOUBT_TIME / CMProps.getTickMillis()));
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob) + " for <T-NAMESELF>, but the magic fades.");

		// return whether it worked
		return success;
	}
}
