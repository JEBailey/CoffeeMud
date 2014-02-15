package com.planet_ink.coffee_mud.Commands;
import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;

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
public class Beacon extends StdCommand
{
	public Beacon(){}

	private final String[] access={"BEACON"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(commands.size()==0)
		{
			if(mob.getStartRoom()==mob.location())
				mob.tell("This is already your beacon.");
			else
			{
				mob.setStartRoom(mob.location());
				mob.tell("You have modified your beacon.");
			}
		}
		else
		{
			String name=CMParms.combine(commands,0);
			MOB M=CMLib.sessions().findPlayerOnline(name,true);
			if(M==null) M=CMLib.sessions().findPlayerOnline(name,false);
			if(M==null)
			{
				mob.tell("No one is online called '"+name+"'!");
				return false;
			}
			if(M.getStartRoom()==M.location())
			{
				mob.tell(M.name(mob)+" is already at their beacon.");
				return false;
			}
			if(!CMSecurity.isAllowed(mob,M.location(),CMSecurity.SecFlag.BEACON))
			{
				mob.tell("You cannot beacon "+M.name(mob)+" there.");
				return false;
			}
			M.setStartRoom(M.location());
			mob.tell("You have modified "+M.name(mob)+"'s beacon.");
		}
		return false;
	}
	
	public boolean canBeOrdered(){return true;}
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.BEACON);}

	
}
