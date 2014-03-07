package com.planet_ink.coffee_mud.Abilities.Skills;

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
public class Skill_Attack3 extends Skill_Attack2 {
	public String ID() {
		return "Skill_Attack3";
	}

	public String name() {
		return "Third Attack";
	}

	protected int attackToNerf() {
		return 3;
	}

	protected int roundToNerf() {
		return 1;
	}

	protected double nerfAmount() {
		return .6;
	}

	protected double numberOfFullAttacks() {
		return 1.0;
	}

}