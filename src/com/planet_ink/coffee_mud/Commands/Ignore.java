package com.planet_ink.coffee_mud.Commands;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;

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
public class Ignore extends StdCommand
{
	public Ignore(){}

	private final String[] access={"IGNORE"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		Set<String> h=pstats.getIgnored();
		if((commands.size()<2)||(((String)commands.elementAt(1)).equalsIgnoreCase("list")))
		{
			if(h.size()==0)
				mob.tell("You have no names on your ignore list.  Use IGNORE ADD to add more.");
			else
			{
				StringBuffer str=new StringBuffer("You are ignoring: ");
				for(Iterator e=h.iterator();e.hasNext();)
					str.append(((String)e.next())+" ");
				mob.tell(str.toString());
			}
		}
		else
		if(((String)commands.elementAt(1)).equalsIgnoreCase("ADD"))
		{
			String name=CMParms.combine(commands,2);
			if(name.length()==0)
			{
				mob.tell("Add whom?");
				return false;
			}
			name=CMStrings.capitalizeAndLower(name);
			if((!CMLib.players().playerExists(name))&&(name.indexOf('@')<0))
			{
				mob.tell("No player by that name was found.");
				return false;
			}
			if(h.contains(name))
			{
				mob.tell("That name is already on your list.");
				return false;
			}
			h.add(name);
			mob.tell("The Player '"+name+"' has been added to your ignore list.");
		}
		else
		if(((String)commands.elementAt(1)).equalsIgnoreCase("REMOVE"))
		{
			String name=CMParms.combine(commands,2);
			if(name.length()==0)
			{
				mob.tell("Remove whom?");
				return false;
			}
			if(!h.contains(name))
			{
				mob.tell("That name '"+name+"' does not appear on your list.  Watch your casing!");
				return false;
			}
			h.remove(name);
			mob.tell("The Player '"+name+"' has been removed from your ignore list.");
		}
		else
		{
			mob.tell("Parameter '"+((String)commands.elementAt(1))+"' is not recognized.  Try LIST, ADD, or REMOVE.");
			return false;
		}
		return false;
	}
	
	public boolean canBeOrdered(){return true;}

	
}
