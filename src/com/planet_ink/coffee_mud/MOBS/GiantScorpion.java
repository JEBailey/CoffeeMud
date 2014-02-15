package com.planet_ink.coffee_mud.MOBS;
import java.util.Random;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


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
public class GiantScorpion extends StdMOB
{
	public String ID(){return "GiantScorpion";}
	public int stingDown=5;

	public GiantScorpion()
	{
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		username="a Giant Scorpion";
		setDescription("The giant scorpion has a green carapace and yellowish green legs and pincers. The segmented tail is black, with a vicious stinger on the end.");
		setDisplayText("A mean Giant Scorpion hunts.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(2);

		basePhyStats().setWeight(1000 + Math.abs(randomizer.nextInt() % 550));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,5 + Math.abs(randomizer.nextInt() % 3));
		baseCharStats().setStat(CharStats.STAT_STRENGTH,13);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,9);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(5);
		basePhyStats().setArmor(70);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		addBehavior(CMClass.getBehavior("Mobile"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}


	public boolean tick(Tickable ticking, int tickID)
	{
		// ===== are we in combat?
		if((!amDead())&&(tickID==Tickable.TICKID_MOB))
		{
			if((--stingDown)<=0)
			{
				stingDown=5;
				if (isInCombat())
					sting();
			}
		}
		return super.tick(ticking,tickID);
	}

	protected boolean sting()
	{
		if (CMLib.flags().aliveAwakeMobileUnbound(this,true)&&
			(CMLib.flags().canHear(this)||CMLib.flags().canSee(this)||CMLib.flags().canSmell(this)))
		{
			MOB target = getVictim();
			// ===== if it is less than three so roll for it
			int roll = (int)Math.round(Math.random()*99);

			// ===== check the result
			if (roll<20)
			{
				// Sting was successful
 				CMMsg msg=CMClass.getMsg(this, target, null, CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_POISON, "^F^<FIGHT^><S-NAME> sting(s) <T-NAMESELF>!^</FIGHT^>^?");
				CMLib.color().fixSourceFightColor(msg);
				if(location().okMessage(target,msg))
				{
					this.location().send(target,msg);
					if(msg.value()<=0)
					{
						Ability poison = CMClass.getAbility("Poison");
						if(poison!=null) poison.invoke(this, target, true,0);
					}
				}
			}
		}
		return true;
	}


}
