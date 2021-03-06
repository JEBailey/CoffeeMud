package com.planet_ink.coffee_mud.Abilities.Specializations;

import com.planet_ink.coffee_mud.Items.interfaces.Weapon;

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
public class Specialization_Axe extends Specialization_Weapon {
	public String ID() {
		return "Specialization_Axe";
	}

	public String name() {
		return "Axe Specialization";
	}

	public Specialization_Axe() {
		super();
		weaponClass = Weapon.CLASS_AXE;
	}

	private final static String[] EXPERTISES = { "AXESTRIKE", "AXESLICE",
			"AXEPIERCE" };
	private final static String[] EXPERTISE_NAMES = { "Axe Striking",
			"Axe Slicing", "Axe Piercing" };
	private final static String[] EXPERTISE_STATS = { "DEX", "STR", "STR" };
	private final static int[] EXPERTISE_LEVELS = { 24, 27, 27 };
	private final int[] EXPERTISE_DAMAGE_TYPE = { 0, Weapon.TYPE_SLASHING,
			Weapon.TYPE_PIERCING };

	protected String[] EXPERTISES() {
		return EXPERTISES;
	}

	protected String[] EXPERTISES_NAMES() {
		return EXPERTISE_NAMES;
	}

	protected String[] EXPERTISE_STATS() {
		return EXPERTISE_STATS;
	}

	protected int[] EXPERTISE_LEVELS() {
		return EXPERTISE_LEVELS;
	}

	protected int[] EXPERTISE_DAMAGE_TYPE() {
		return EXPERTISE_DAMAGE_TYPE;
	}

}
