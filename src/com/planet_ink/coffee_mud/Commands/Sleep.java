package com.planet_ink.coffee_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

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
@SuppressWarnings("rawtypes")
public class Sleep extends StdCommand
{
	public Sleep(){}

	private final String[] access={"SLEEP","SL"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMLib.flags().isSleeping(mob))
		{
			mob.tell("You are already asleep!");
			return false;
		}
		Room R=mob.location();
		if(R==null)
			return false;
		if(commands.size()<=1)
		{
			CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SLEEP,"<S-NAME> lay(s) down and take(s) a nap.");
			if(R.okMessage(mob,msg))
				R.send(mob,msg);
			return false;
		}
		String possibleRideable=CMParms.combine(commands,1);
		Environmental E=R.fetchFromRoomFavorItems(null,possibleRideable);
		if((E==null)||(!CMLib.flags().canBeSeenBy(E,mob)))
		{
			mob.tell("You don't see '"+possibleRideable+"' here.");
			return false;
		}
		String mountStr=null;
		if(E instanceof Rideable)
			mountStr="<S-NAME> "+((Rideable)E).mountString(CMMsg.TYP_SLEEP,mob)+" <T-NAME>.";
		else
			mountStr="<S-NAME> sleep(s) on <T-NAME>.";
		String sourceMountStr=null;
		if(!CMLib.flags().canBeSeenBy(E,mob))
			sourceMountStr=mountStr;
		else
		{
			sourceMountStr=CMStrings.replaceAll(mountStr,"<T-NAME>",E.name());
			sourceMountStr=CMStrings.replaceAll(sourceMountStr,"<T-NAMESELF>",E.name());
		}
		CMMsg msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_SLEEP,sourceMountStr,mountStr,mountStr);
		if(R.okMessage(mob,msg))
			R.send(mob,msg);
		return false;
	}
	public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCombatActionCost(ID());}
	public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getActionCost(ID());}
	public boolean canBeOrdered(){return true;}

	
}
