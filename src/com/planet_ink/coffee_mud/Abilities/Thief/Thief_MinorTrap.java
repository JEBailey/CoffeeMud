package com.planet_ink.coffee_mud.Abilities.Thief;

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
public class Thief_MinorTrap extends Thief_Trap {
	public String ID() {
		return "Thief_MinorTrap";
	}

	public String name() {
		return "Lay Minor Traps";
	}

	private static final String[] triggerStrings = { "MTRAP", "MINORTRAP" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	protected int maxLevel() {
		return 3;
	}
}
