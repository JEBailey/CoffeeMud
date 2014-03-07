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
public class Jaguar extends StdMOB {
	public String ID() {
		return "Jaguar";
	}

	public Jaguar() {
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username = "a jaguar";
		setDescription("A powerful cat with a deep chest and muscular limbs.  It\\`s covered in light yellow fur with black spots.");
		setDisplayText("A jaguar prowls quietly.");
		CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 45));
		setWimpHitPoint(2);

		basePhyStats.setWeight(200 + Math.abs(randomizer.nextInt() % 55));

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH, 12);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY, 17);
		baseCharStats().setMyRace(CMClass.getRace("GreatCat"));
		baseCharStats().getMyRace().startRacing(this, false);

		basePhyStats().setDamage(8);
		basePhyStats().setSpeed(2.0);
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