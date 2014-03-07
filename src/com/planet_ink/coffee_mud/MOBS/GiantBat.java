package com.planet_ink.coffee_mud.MOBS;

import java.util.Random;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMLib;

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
public class GiantBat extends StdMOB {
	public String ID() {
		return "GiantBat";
	}

	public GiantBat() {
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username = "a giant bat";
		setDescription("It is a giant version of your common bat.");
		setDisplayText("A giant bat flies nearby.");
		CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 100));

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH, 16);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY, 17);

		basePhyStats().setDamage(8);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(4);
		basePhyStats().setArmor(80);
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_FLYING);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20,
				basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}