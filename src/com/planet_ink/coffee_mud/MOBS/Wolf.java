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
public class Wolf extends StdMOB {
	public String ID() {
		return "Wolf";
	}

	public Wolf() {
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username = "a wolf";
		setDescription("A powerful wolf with grey fur and amber eyes.");
		setDisplayText("A wolf growls and stares at you.");
		CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats.setWeight(50 + Math.abs(randomizer.nextInt() % 55));

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH, 10);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY, 14);
		baseCharStats().setMyRace(CMClass.getRace("Wolf"));
		baseCharStats().getMyRace().startRacing(this, false);

		basePhyStats().setDamage(6);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(4);
		basePhyStats().setArmor(80);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20,
				basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}