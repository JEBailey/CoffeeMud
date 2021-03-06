package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Play_Charge extends Play {
	public String ID() {
		return "Play_Charge";
	}

	public String name() {
		return "Charge!";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected boolean persistantSong() {
		return false;
	}

	Vector chcommands = null;

	protected void inpersistantAffect(MOB mob) {
		Ability A = CMClass.getAbility("Fighter_Charge");
		if (A != null) {
			A.setAbilityCode(4 * getXLEVELLevel(invoker()));
			A.invoke(mob, chcommands, null, true, adjustedLevel(invoker(), 0));
		}
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Ability A = CMClass.getAbility("Fighter_Charge");
			if (A != null)
				return A.castingQuality(mob, target);
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((commands.size() == 0) && (!mob.isInCombat())) {
			mob.tell("Play charge at whom?");
			return false;
		}
		if (commands.size() == 0)
			commands.addElement(mob.getVictim().name());
		chcommands = commands;
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		return true;
	}
}
