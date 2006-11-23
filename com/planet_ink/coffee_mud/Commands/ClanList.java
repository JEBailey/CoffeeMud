package com.planet_ink.coffee_mud.Commands;
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
   Copyright 2000-2006 Bo Zimmerman

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
public class ClanList extends BaseClanner
{
	public ClanList(){}

	private String[] access={"CLANLIST","CLANS"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
	    boolean trophySystemActive=CMLib.clans().trophySystemActive();
		StringBuffer head=new StringBuffer("");
		head.append("^x[");
		head.append(CMStrings.padRight("Clan Name",24)+" | ");
		head.append(CMStrings.padRight("Type",13)+" | ");
		head.append(CMStrings.padRight("Status",8)+" | ");
		head.append(CMStrings.padRight("Members",7));
		if(trophySystemActive)
			head.append(" | "+CMStrings.padRight("Trophies",8));
		head.append("]^.^? \n\r");
		StringBuffer msg=new StringBuffer("");
		for(Enumeration e=CMLib.clans().clans();e.hasMoreElements();)
		{
			Clan thisClan=(Clan)e.nextElement();
			msg.append(" ");
			msg.append(CMStrings.padRight("^<CLAN^>"+CMStrings.removeColors(thisClan.clanID())+"^</CLAN^>",24)+"   ");
			msg.append(CMStrings.padRight(thisClan.typeName(),13)+"   ");
			boolean war=false;
			for(Enumeration e2=CMLib.clans().clans();e2.hasMoreElements();)
			{
				Clan C=(Clan)e2.nextElement();
				if((C!=thisClan)
				&&((thisClan.getClanRelations(C.clanID())==Clan.REL_WAR)
					||(C.getClanRelations(thisClan.clanID())==Clan.REL_WAR)))
				{ war=true; break;}
			}
			String status=(war)?"At War":"Active";
			switch(thisClan.getStatus())
			{
			case Clan.CLANSTATUS_FADING:
				status="Inactive";
				break;
			case Clan.CLANSTATUS_PENDING:
				status="Pending";
				break;
			}
			msg.append(CMStrings.padRight(status,8)+"   ");
			msg.append(CMStrings.padRight(new Integer(thisClan.getSize()).toString(),7)+"   ");
			if(trophySystemActive)
				for(int i=0;i<Clan.TROPHY_DESCS_SHORT.length;i++)
				    if((Clan.TROPHY_DESCS_SHORT[i].length()>0)&&(CMath.bset(thisClan.getTrophies(),i)))
				        msg.append(Clan.TROPHY_DESCS_SHORT[i]+" ");
			msg.append("\n\r");
		}
		mob.tell(head.toString()+msg.toString());
		return false;
	}
	
	public boolean canBeOrdered(){return true;}

	
}
