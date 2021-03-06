package com.planet_ink.coffee_mud.Abilities.Poisons;

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

public class Inebriation extends Poison_Alcohol {
	public String ID() {
		return "Inebriation";
	}

	public String name() {
		return "Inebriation";
	}

	private static final String[] triggerStrings = { "INEBRIATE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int alchoholContribution() {
		return 6;
	}
}
