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
public class Puppy extends StdMOB {
	public String ID() {
		return "Puppy";
	}

	public Puppy() {
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username = "a puppy";
		setDescription("It\\`s small, cute, and furry with four legs, just like a puppy ought to be.");
		setDisplayText("A puppy scurries nearby.");
		CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		addBehavior(CMClass.getBehavior("Follower"));
		addBehavior(CMClass.getBehavior("MudChat"));

		basePhyStats().setDamage(4);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
		baseCharStats().setMyRace(CMClass.getRace("Puppy"));
		baseCharStats().getMyRace().startRacing(this, false);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(99);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20,
				basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}