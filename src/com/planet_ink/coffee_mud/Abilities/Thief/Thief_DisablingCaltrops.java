package com.planet_ink.coffee_mud.Abilities.Thief;
import java.util.HashSet;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Thief_DisablingCaltrops extends Thief_Caltrops
{
	public String ID() { return "Thief_DisablingCaltrops"; }
	public String name(){ return "Disabling Caltrops";}
	private static final String[] triggerStrings = {"DISABLINGCALTROPS"};
	public String[] triggerStrings(){return triggerStrings;}
	public String caltropTypeName(){return "disabling ";}
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,"<S-NAME> avoid(s) some "+caltropTypeName()+"caltrops on the floor.");
		else
		if(mob.curState().getMovement()>6)
		{
			mob.curState().adjMovement(-CMLib.dice().roll(3+getX1Level(mob),6,20),mob.maxState());
			mob.location().show(invoker(),mob,this,CMMsg.MSG_OK_ACTION,"The "+caltropTypeName()+"caltrops on the ground disable <T-NAME>");
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}
