package com.planet_ink.coffee_mud.Items.BasicTech;

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
public class GenDisruptor extends GenElecWeapon {
	public String ID() {
		return "GenDisruptor";
	}

	protected int state = 0;

	public GenDisruptor() {
		super();
		setName("a disruptor weapon");
		basePhyStats.setWeight(5);
		setDisplayText("a disruptor");
		setDescription("There are two activation settings: stun, and disrupt.");
		super.mode = ModeType.DISRUPT;
		super.modeTypes = new ModeType[] { ModeType.STUN, ModeType.DISRUPT };
	}
}
