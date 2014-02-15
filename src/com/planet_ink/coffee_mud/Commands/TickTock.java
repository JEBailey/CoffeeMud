package com.planet_ink.coffee_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Libraries.interfaces.CMLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;

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
public class TickTock extends StdCommand
{
	public TickTock(){}

	private final String[] access={"TICKTOCK"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String s=CMParms.combine(commands,1).toLowerCase();
		try
		{
			if(CMath.isInteger(s))
			{
				int h=CMath.s_int(s);
				if(h==0) h=1;
				mob.tell("..tick..tock..");
				mob.location().getArea().getTimeObj().tickTock(h);
				mob.location().getArea().getTimeObj().save();
			}
			else
			if(s.startsWith("clantick"))
				CMLib.clans().tickAllClans();
			else
			{
				for(Enumeration e=CMLib.libraries();e.hasMoreElements();)
				{
					CMLibrary lib=(CMLibrary)e.nextElement();
					if((lib.getServiceClient()!=null)&&(s.equalsIgnoreCase(lib.getServiceClient().getName())))
					{
						if(lib instanceof Runnable)
							((Runnable)lib).run();
						else
							lib.getServiceClient().tickTicker(true);
						mob.tell("Done.");
						return false;
					}
				}
				mob.tell("Ticktock what?  Enter a number of mud-hours, or clanticks, or thread id.");
			}
		}
		catch(Exception e)
		{
			mob.tell("Ticktock failed: "+e.getMessage());
		}
		
		return false;
	}
	
	public boolean canBeOrdered(){return true;}
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.TICKTOCK);}

	
}
