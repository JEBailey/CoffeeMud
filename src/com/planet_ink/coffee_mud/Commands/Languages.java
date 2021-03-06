package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;

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
public class Languages extends Skills {
	public Languages() {
	}

	private final String[] access = { "LANGUAGES", "LANGS" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		StringBuffer msg = new StringBuffer("");
		if (parsedOutIndividualSkill(mob, CMParms.combine(commands, 1),
				Ability.ACODE_SPELL))
			return true;
		msg.append("\n\r^HLanguages known:^? "
				+ getAbilities(mob, mob, Ability.ACODE_LANGUAGE, -1, true,
						parseOutLevel(commands)) + "\n\r");
		if (!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
