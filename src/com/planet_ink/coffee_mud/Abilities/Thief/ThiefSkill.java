package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Directions;
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
public class ThiefSkill extends StdAbility {
	public String ID() {
		return "ThiefSkill";
	}

	public String name() {
		return "a Thief Skill";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int enchantQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		if ((!auto) && (!mob.isMonster()) && (!disregardsArmorCheck(mob))
				&& (!CMLib.utensils().armorCheck(mob, CharClass.ARMOR_LEATHER))
				&& (mob.isMine(this)) && (mob.location() != null)
				&& (CMLib.dice().rollPercentage() < 50)) {
			mob.location().show(
					mob,
					null,
					CMMsg.MSG_OK_VISUAL,
					"<S-NAME> fumble(s) " + name()
							+ " due to <S-HIS-HER> clumsy armor!");
			return false;
		}
		return true;
	}

	public int getMOBLevel(MOB meMOB) {
		if (meMOB == null)
			return 0;
		return meMOB.phyStats().level();
	}

	public MOB getHighestLevelMOB(MOB meMOB, Vector not) {
		if (meMOB == null)
			return null;
		Room R = meMOB.location();
		if (R == null)
			return null;
		int highestLevel = 0;
		MOB highestMOB = null;
		Set<MOB> H = meMOB.getGroupMembers(new HashSet<MOB>());
		if (not != null)
			H.addAll(not);
		for (int i = 0; i < R.numInhabitants(); i++) {
			MOB M = R.fetchInhabitant(i);
			if ((M != null) && (M != meMOB) && (!CMLib.flags().isSleeping(M))
					&& (!H.contains(M))
					&& (highestLevel < M.phyStats().level())
					&& (!CMSecurity.isASysOp(M))) {
				highestLevel = M.phyStats().level();
				highestMOB = M;
			}
		}
		return highestMOB;
	}

	public Physical getOpenable(MOB mob, Room room, Physical givenTarget,
			Vector commands, int[] dirCode, boolean failOnOpen) {
		if ((room == null) || (mob == null))
			return null;
		String whatToOpen = CMParms.combine(commands, 0);
		Physical unlockThis = null;
		dirCode[0] = Directions.getGoodDirectionCode(whatToOpen);
		if (dirCode[0] >= 0)
			unlockThis = room.getExitInDir(dirCode[0]);
		if (unlockThis == null)
			unlockThis = getTarget(mob, room, givenTarget, commands,
					Wearable.FILTER_ANY);
		else if (givenTarget != null)
			unlockThis = givenTarget;

		if (unlockThis instanceof Exit) {
			if (((Exit) unlockThis).isOpen() == failOnOpen) {
				if (failOnOpen)
					mob.tell(mob, unlockThis, null, "<T-NAME> is open!");
				else
					mob.tell(mob, unlockThis, null, "<T-NAME> is closed!");
				return null;
			}

		} else if (unlockThis instanceof Container) {
			if (((Container) unlockThis).isOpen() == failOnOpen) {
				if (failOnOpen)
					mob.tell(mob, unlockThis, null, "<T-NAME> is open!");
				else
					mob.tell(mob, unlockThis, null, "<T-NAME> is closed!");
				return null;
			}
		} else {
			mob.tell(mob, unlockThis, null, "You can't do that to <T-NAME>.");
			return null;
		}
		return unlockThis;
	}
}
