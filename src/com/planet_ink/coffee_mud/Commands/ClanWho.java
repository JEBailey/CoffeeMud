package com.planet_ink.coffee_mud.Commands;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.XVector;

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
public class ClanWho extends Who
{
	public ClanWho(){}

	private final String[] access={"CLANWHO","CLWH"};
	public String[] getAccessWords(){return access;}
	
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String clanName=CMParms.combine(commands,1).toUpperCase();
		StringBuffer msg=new StringBuffer("");
		List<String> clanList=new XVector<String>();
		if(clanName.trim().length()>0)
		{
			Clan C=CMLib.clans().findClan(clanName);
			if(C==null)
				mob.tell("There's no such clan as '"+clanName+"'.");
			else
				clanList.add(C.clanID());
		}
		else
		{
			for(Pair<Clan,Integer> c : mob.clans())
				clanList.add(c.first.clanID());
			if(clanList.size()==0)
			{
				mob.tell("You need to specify a clan.");
				return false;
			}
		}
		Set<MOB> alreadyDone=new HashSet<MOB>();
		int[] colWidths=getShortColWidths(mob);
		for(String clanID : clanList)
		{
			Clan C=CMLib.clans().getClan(clanID);
			if(C!=null)
			{
				msg.append("\n\r^x").append(C.getGovernmentName()).append(" ").append(C.getName()).append("\n\r");
				msg.append(getHead(colWidths));
				for(Session S : CMLib.sessions().localOnlineIterable())
				{
					MOB mob2=S.mob();
					if((mob2!=null)&&(mob2.soulMate()!=null))
						mob2=mob2.soulMate();

					if((mob2!=null)
					&&(!S.isStopped())
					&&((((mob2.phyStats().disposition()&PhyStats.IS_CLOAKED)==0)
						||((CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.CLOAK)||CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.WIZINV))&&(mob.phyStats().level()>=mob2.phyStats().level()))))
					&&(mob2.getClanRole(C.clanID())!=null)
					&&(!alreadyDone.contains(mob2))
					&&(CMLib.flags().isInTheGame(mob2,true))
					&&(mob2.phyStats().level()>0))
					{
						msg.append(showWhoShort(mob2,colWidths));
						alreadyDone.add(mob2);
					}
				}
			}
		}
		mob.tell(msg.toString());
		return false;
	}
	
	public boolean canBeOrdered(){return true;}

	
}
