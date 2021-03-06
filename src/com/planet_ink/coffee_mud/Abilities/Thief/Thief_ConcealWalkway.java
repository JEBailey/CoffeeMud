package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;
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
public class Thief_ConcealWalkway extends ThiefSkill {
	public String ID() {
		return "Thief_ConcealWalkway";
	}

	public String name() {
		return "Conceal Walkway";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "WALKWAYCONCEAL",
			"WALKCONCEAL", "WCONCEAL", "CONCEALWALKWAY" };

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	public int code = Integer.MIN_VALUE;

	public int abilityCode() {
		if (code < 0)
			code = CMath.s_int(text());
		return code;
	}

	public void setAbilityCode(int newCode) {
		code = newCode;
		super.miscText = "" + newCode;
	}

	public void affectPhyStats(Physical host, PhyStats stats) {
		super.affectPhyStats(host, stats);
		if (host instanceof Exit) {
			stats.setDisposition(stats.disposition() | PhyStats.IS_HIDDEN);
			stats.setLevel(stats.level() + abilityCode());
		}
	}

	public void executeMsg(Environmental myHost, CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (canBeUninvoked() && (invoker() != null)
				&& (!msg.source().isMonster()) && (msg.source() != invoker())
				&& (msg.sourceMinor() == CMMsg.TYP_ENTER) && (affected != null)) {
			if (!CMLib.flags().isInTheGame(invoker(), true)) {
				unInvoke();
				if (affected != null) {
					affected.delEffect(this);
					affected.recoverPhyStats();
				}
			} else {
				Set<MOB> grp = invoker().getGroupMembers(new HashSet<MOB>());
				if (!grp.contains(msg.source())) {
					unInvoke();
					if (affected != null) {
						affected.delEffect(this);
						affected.recoverPhyStats();
					}
				}
			}
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((commands.size() < 1) && (givenTarget == null)) {
			mob.tell("Which way would you like to conceal?");
			return false;
		}
		Environmental chkE = null;
		String typed = CMParms.combine(commands, 0);
		if (Directions.getGoodDirectionCode(typed) < 0)
			chkE = mob.location().fetchFromMOBRoomItemExit(mob, null, typed,
					Wearable.FILTER_WORNONLY);
		else
			chkE = mob.location().getExitInDir(
					Directions.getGoodDirectionCode(typed));
		int direction = -1;
		for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--)
			if (mob.location().getExitInDir(d) == chkE)
				direction = d;
		if ((!(chkE instanceof Exit))
				|| (!CMLib.flags().canBeSeenBy(chkE, mob)) || (direction < 0)) {
			mob.tell("You don't see any directions called '" + typed
					+ "' here.");
			return false;
		}
		Room R2 = mob.location().getRoomInDir(direction);
		if ((!CMath.bset(mob.location().domainType(), Room.INDOORS))
				&& (R2 != null) && (!CMath.bset(R2.domainType(), Room.INDOORS))) {
			mob.tell("This only works on walkways into or within buildings.");
			return false;
		}
		Exit X = (Exit) chkE;
		if ((!auto)
				&& (X.phyStats().level() > (adjustedLevel(mob, asLevel) * 2))) {
			mob.tell("You aren't good enough to conceal that direction.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, X, this, CMMsg.MSG_THIEF_ACT,
					"<S-NAME> conceal(s) <T-NAME>.", CMMsg.MSG_THIEF_ACT, null,
					CMMsg.MSG_THIEF_ACT, null);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability A = (Ability) super.copyOf();
				A.setInvoker(mob);
				A.setAbilityCode((adjustedLevel(mob, asLevel) * 2)
						- X.phyStats().level());
				Room R = mob.location();
				if ((CMLib.law().doesOwnThisProperty(mob, R))
						|| ((R2 != null) && (CMLib.law().doesOwnThisProperty(
								mob, R2)))) {
					X.addNonUninvokableEffect(A);
					CMLib.database().DBUpdateExits(mob.location());
				} else
					A.startTickDown(mob, X, 15 * (adjustedLevel(mob, asLevel)));
				X.recoverPhyStats();
			}
		} else
			beneficialVisualFizzle(mob, X,
					"<S-NAME> attempt(s) to coneal <T-NAME>, but obviously fail(s).");
		return success;
	}
}
