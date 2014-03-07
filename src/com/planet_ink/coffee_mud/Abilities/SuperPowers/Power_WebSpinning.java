package com.planet_ink.coffee_mud.Abilities.SuperPowers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Power_WebSpinning extends SuperPower {
	public String ID() {
		return "Power_WebSpinning";
	}

	public String name() {
		return "Web Spinning";
	}

	public String displayText() {
		return "(Webbed)";
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(5);
	}

	public int minRange() {
		return 1;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_MOBS | CAN_ITEMS | CAN_EXITS;
	}

	protected int canTargetCode() {
		return CAN_MOBS | CAN_ITEMS | CAN_EXITS;
	}

	public long flags() {
		return Ability.FLAG_BINDING;
	}

	public int amountRemaining = 0;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_BOUND);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;

			// when this spell is on a MOBs Affected list,
			// it should consistantly prevent the mob
			// from trying to do ANYTHING except sleep
			if (msg.amISource(mob)) {
				if ((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
						&& ((msg.sourceMajor(CMMsg.MASK_HANDS)) || (msg
								.sourceMajor(CMMsg.MASK_MOVE)))) {
					if (mob.location().show(mob, null, CMMsg.MSG_OK_ACTION,
							"<S-NAME> struggle(s) against the web.")) {
						amountRemaining -= (mob.charStats().getStat(
								CharStats.STAT_STRENGTH) + mob.phyStats()
								.level());
						if (amountRemaining < 0)
							unInvoke();
					}
					return false;
				}
			}
		} else if (affected instanceof Item) {
			if (msg.target() == affected) {
				if (msg.targetMinor() == CMMsg.TYP_GET)
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),
							msg.target(), null, CMMsg.MSG_OK_VISUAL,
							"<T-NAME> is covered in sticky webbing!", null,
							null));
				else if ((msg.targetMinor() == CMMsg.TYP_DROP)
						&& (((Item) affected).owner() == msg.source())) {
					msg.source().tell(msg.source(), affected, null,
							"<T-NAME> is too sticky to let go of!");
					return false;
				}
			}
		} else if (affected instanceof Exit) {
			if (msg.target() == affected) {
				if (msg.targetMinor() == CMMsg.TYP_OPEN) {
					msg.source().tell(msg.source(), affected, null,
							"<T-NAME> is held fast by gobs of webbing!");
					return false;
				}
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked()) {
			if (!mob.amDead())
				mob.location()
						.show(mob, null, CMMsg.MSG_NOISYMOVEMENT,
								"<S-NAME> manage(s) to break <S-HIS-HER> way free of the web.");
			CMLib.commands().postStand(mob, true);
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							CMMsg.MSG_NOISYMOVEMENT,
							(auto ? ""
									: "^S<S-NAME> shoot(s) and spin(s) a web at <T-NAMESELF>!^?")
									+ CMLib.protocol().msp("web.wav", 40));
			if ((mob.location().okMessage(mob, msg))
					&& (target.fetchEffect(this.ID()) == null)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					amountRemaining = 160;
					if (CMLib.map().roomLocation(target) == mob.location()) {
						success = maliciousAffect(mob, target, asLevel,
								(adjustedLevel(mob, asLevel) * 10), -1);
						mob.location().show(mob, target, CMMsg.MSG_OK_ACTION,
								"<T-NAME> become(s) stuck in a mass of web!");
					}
				}
			}
		} else
			return maliciousFizzle(mob, null,
					"<S-NAME> spin(s) a web towards <T-NAMESELF>, but miss(es).");

		// return whether it worked
		return success;
	}
}