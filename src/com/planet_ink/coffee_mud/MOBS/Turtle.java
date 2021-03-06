package com.planet_ink.coffee_mud.MOBS;

import java.util.Random;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Turtle extends StdMOB {
	public String ID() {
		return "Turtle";
	}

	public Turtle() {
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username = "a turtle";
		setDescription("It\\`s a slow moving turtle with a big green shell.");
		setDisplayText("A turtle seems to be moving here.");
		CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		basePhyStats().setDamage(4);
		basePhyStats().setSpeed(.5);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseCharStats().setMyRace(CMClass.getRace("Turtle"));
		baseCharStats().getMyRace().startRacing(this, false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20,
				basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
