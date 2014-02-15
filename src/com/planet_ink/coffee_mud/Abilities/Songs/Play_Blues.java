package com.planet_ink.coffee_mud.Abilities.Songs;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Social;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
public class Play_Blues extends Play
{
	public String ID() { return "Play_Blues"; }
	public String name(){ return "Blues";}
	public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	protected boolean maliciousButNotAggressiveFlag(){return true;}
	protected String songOf(){return "the "+name();}

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		// the sex rules
		if(!(affected instanceof MOB)) return true;

		MOB myChar=(MOB)affected;
		if((msg.target()!=null)&&(msg.target() instanceof MOB)&&(myChar!=invoker()))
		{
			if((msg.amISource(myChar)||(msg.amITarget(myChar))
			&&(msg.tool() instanceof Social)
			&&(msg.tool().Name().equals("MATE <T-NAME>")
				||msg.tool().Name().equals("SEX <T-NAME>"))))
			{
				if(msg.amISource(myChar))
					myChar.tell("You really don't feel like it.");
				else
				if(msg.amITarget(myChar))
					msg.source().tell(myChar.name()+" doesn't look like "+myChar.charStats().heshe()+" feels like it.");
				return false;
			}
		}
		return true;
	}
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected!=null)&&(affected instanceof MOB)&&(affected!=invoker()))
		{
			MOB mob=(MOB)affected;
			mob.curState().adjHunger(-2,mob.maxState().maxHunger(mob.baseWeight()));
			if(CMLib.dice().rollPercentage()>(adjustedLevel(invoker(),0)/4))
			{
				Ability A=CMClass.getAbility("Disease_Depression");
				if(A!=null) A.invoke(invoker(),affected,true,0);
			}
		}
		return true;
	}
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((invoker==null)||(invoker==affected))
			return;
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
											-((invoker().charStats().getStat(CharStats.STAT_CHARISMA)/4)
													+(adjustedLevel(invoker(),0))));
	}
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if((invoker()!=null)&&(invoker()!=mob))
			stats.setStat(CharStats.STAT_SAVE_JUSTICE,stats.getStat(CharStats.STAT_SAVE_JUSTICE)-(invoker().charStats().getStat(CharStats.STAT_CHARISMA)+getXLEVELLevel(invoker())));
	}
}

