package com.planet_ink.coffee_mud.Races;

/*
 Copyright 2008-2014 Bo Zimmerman

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
public class PlayerRace extends Human {
	public String ID() {
		return "PlayerRace";
	}

	public String name() {
		return "PlayerRace";
	}

	public String[] culturalAbilityNames() {
		return null;
	}

	public int[] culturalAbilityProficiencies() {
		return null;
	}

	public int availabilityCode() {
		return 0;
	}

	public PlayerRace() {
		super();
	}

}