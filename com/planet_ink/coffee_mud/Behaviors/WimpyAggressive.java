package com.planet_ink.coffee_mud.Behaviors;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2007 Bo Zimmerman

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
public class WimpyAggressive extends Aggressive
{
	public String ID(){return "WimpyAggressive";}
	public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE|Behavior.FLAG_TROUBLEMAKING;}
	protected int tickWait=0;
	protected int tickDown=0;

	public boolean grantsAggressivenessTo(MOB M)
	{
		return ((M!=null)&&(CMLib.flags().isSleeping(M)))&&
			CMLib.masking().maskCheck(getParms(),M,false);
	}
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		tickDown=tickWait;
	}

	public static void pickAWimpyFight(MOB observer, boolean mobKiller, boolean misBehave, String attackMsg)
	{
		if(!canFreelyBehaveNormal(observer)) return;
		Room R=observer.location();
		if(R!=null)
		for(int i=0;i<R.numInhabitants();i++)
		{
			MOB mob=R.fetchInhabitant(i);
			if((mob!=null)&&(mob!=observer)&&(CMLib.flags().isSleeping(mob)))
			{
				startFight(observer,mob,mobKiller,misBehave,attackMsg);
				if(observer.isInCombat()) break;
			}
		}
	}

	public static void tickWimpyAggressively(Tickable ticking, boolean mobKiller, boolean misBehave, int tickID, String attackMsg)
	{
		if(tickID!=Tickable.TICKID_MOB) return;
		if(ticking==null) return;
		if(!(ticking instanceof MOB)) return;

		pickAWimpyFight((MOB)ticking,mobKiller,misBehave,attackMsg);
	}
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID!=Tickable.TICKID_MOB) return true;
		if((--tickDown)<0)
		{
			tickDown=tickWait;
			tickWimpyAggressively(ticking,mobkill,misbehave,tickID,attackMessage);
		}
		return true;
	}
}
