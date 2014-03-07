package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

/**
 * <p>
 * Title: False Realities Flavored CoffeeMUD
 * </p>
 * <p>
 * Description: The False Realities Version of CoffeeMUD
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003 Jeremy Vyska
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License.
 * <p>
 * You may obtain a copy of the License at
 * 
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * <p>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * <p>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * <p>
 * limitations under the License.
 * <p>
 * Company: http://www.falserealities.com
 * </p>
 * 
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */

@SuppressWarnings("rawtypes")
public class Spell_CEqCold extends Spell_BaseClanEq {
	public String ID() {
		return "Spell_CEqCold";
	}

	public String name() {
		return "ClanEnchant Cold";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		type = "Cold";
		// All the work is done by the base model
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		return true;
	}
}