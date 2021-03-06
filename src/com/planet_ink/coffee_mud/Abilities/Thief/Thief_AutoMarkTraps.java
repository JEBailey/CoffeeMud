package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Thief_AutoMarkTraps extends ThiefSkill {
	public String ID() {
		return "Thief_AutoMarkTraps";
	}

	public String displayText() {
		return "(Automarking traps)";
	}

	public String name() {
		return "AutoMark Traps";
	}

	private static final String[] triggerStrings = { "AUTOMARKTRAPS" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_ALERT;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((affected instanceof MOB) && (msg.targetMinor() == CMMsg.TYP_ENTER)
				&& (msg.source() == affected) && (msg.target() instanceof Room)
				&& (msg.tool() instanceof Exit)
				&& (((MOB) affected).location() != null)) {
			Room R = (Room) msg.target();
			Room R2 = null;
			dropem(msg.source(), R);
			Exit E = null;
			Item I = null;
			for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
				R2 = R.getRoomInDir(d);
				E = R.getExitInDir(d);
				if ((E != null) && (CMLib.utensils().fetchMyTrap(E) != null))
					dropem(msg.source(), E);
				E = R.getReverseExit(d);
				if ((E != null) && (CMLib.utensils().fetchMyTrap(E) != null))
					dropem(msg.source(), E);
				if ((R2 != null) && (CMLib.utensils().fetchMyTrap(R2) != null))
					dropem(msg.source(), R2);
			}
			for (int i = 0; i < R.numItems(); i++) {
				I = R.getItem(i);
				if ((I.container() == null)
						&& (CMLib.utensils().fetchMyTrap(I) != null))
					dropem(msg.source(), I);
			}
		}
	}

	public void dropem(MOB mob, Physical P) {
		Ability A = mob.fetchAbility("Thief_DetectTraps");
		if (A == null) {
			A = CMClass.getAbility("Thief_DetectTraps");
			A.setProficiency(100);
		}
		CharState savedState = (CharState) mob.curState().copyOf();
		if (A.invoke(mob, P, false, 0)) {
			A = mob.fetchAbility("Thief_MarkTrapped");
			if (A == null) {
				A = CMClass.getAbility("Thief_MarkTrapped");
				A.setProficiency(100);
			}
			A.invoke(mob, P, false, 0);
		}
		mob.curState().setMana(savedState.getMana());
		mob.curState().setHitPoints(savedState.getHitPoints());
		mob.curState().setMovement(savedState.getMovement());
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = (givenTarget instanceof MOB) ? (MOB) givenTarget : mob;
		if (target.fetchEffect(ID()) != null) {
			target.tell("You are no longer automatically marking traps.");
			target.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if ((!auto) && (target.fetchAbility("Thief_MarkTrapped") == null)) {
			target.tell("You don't know how to mark traps yet!");
			return false;
		}
		if ((!auto) && (target.fetchAbility("Thief_DetectTraps") == null)) {
			target.tell("You don't know how to detect traps yet!");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			target.tell("You will now automatically mark traps when you enter a room.");
			beneficialAffect(mob, target, asLevel, 0);
			Ability A = mob.fetchEffect(ID());
			if (A != null)
				A.makeLongLasting();
			dropem(target, target.location());
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> attempt(s) to mark traps, but can't seem to concentrate.");
		return success;
	}
}
