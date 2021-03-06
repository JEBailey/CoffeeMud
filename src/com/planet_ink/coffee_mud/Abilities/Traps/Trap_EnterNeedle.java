package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;

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
public class Trap_EnterNeedle extends Trap_Enter {
	public String ID() {
		return "Trap_EnterNeedle";
	}

	public String name() {
		return "Entry Needle Trap";
	}

	protected int canAffectCode() {
		return Ability.CAN_EXITS | Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapType() {
		return TRAP_NEEDLE;
	}
}
