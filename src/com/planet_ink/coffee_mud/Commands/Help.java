package com.planet_ink.coffee_mud.Commands;
import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.Resources;

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
public class Help extends StdCommand
{
	public Help(){}

	private final String[] access={"HELP"};
	public String[] getAccessWords(){return access;}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String helpStr=CMParms.combine(commands,1);
		if(CMLib.help().getHelpFile().size()==0)
		{
			mob.tell("No help is available.");
			return false;
		}
		StringBuilder thisTag=null;
		if(helpStr.length()==0)
			thisTag=new StringBuilder(Resources.getFileResource("help/help.txt",true));
		else
			thisTag=CMLib.help().getHelpText(helpStr,CMLib.help().getHelpFile(),mob);
		if((thisTag==null)&&(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.AHELP)))
			thisTag=CMLib.help().getHelpText(helpStr,CMLib.help().getArcHelpFile(),mob);
		if(thisTag==null)
		{
			StringBuilder thisList=
				CMLib.help().getHelpList(
				helpStr,
				CMLib.help().getHelpFile(),
				CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.AHELP)?CMLib.help().getArcHelpFile():null,
				mob);
			if((thisList!=null)&&(thisList.length()>0))
				mob.tell("No help is available on '"+helpStr+"'.\n\rHowever, here are some search matches:\n\r^N"+thisList.toString().replace('_',' '));
			else
				mob.tell("No help is available on '"+helpStr+"'.\n\rEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list, or 'HELPLIST' to search.");
			Log.helpOut("Help",mob.Name()+" wanted help on "+helpStr);
		}
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(thisTag.toString());
		return false;
	}
	
	public boolean canBeOrdered(){return true;}

	
}
