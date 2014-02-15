package com.planet_ink.coffee_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
import com.planet_ink.coffee_mud.core.interfaces.Rider;

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
@SuppressWarnings({"unchecked","rawtypes"})
public class Mount extends StdCommand
{
	public Mount(){}

	private final String[] access={"MOUNT","BOARD","RIDE","M"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(((String)commands.elementAt(0))+" what?");
			return false;
		}
		commands.removeElementAt(0);
		Environmental recipient=null;
		Vector possRecipients=new Vector();
		for(int m=0;m<mob.location().numInhabitants();m++)
		{
			MOB M=mob.location().fetchInhabitant(m);
			if((M!=null)&&(M instanceof Rideable))
				possRecipients.addElement(M);
		}
		for(int i=0;i<mob.location().numItems();i++)
		{
			Item I=mob.location().getItem(i);
			if((I!=null)&&(I instanceof Rideable))
				possRecipients.addElement(I);
		}
		Rider RI=null;
		if(commands.size()>1)
		{
			Item I=mob.location().findItem(null,(String)commands.firstElement());
			if(I!=null)
			{
				commands.removeElementAt(0);
				I.setRiding(null);
				RI=I;
			}
			if(RI==null)
			{
				MOB M=mob.location().fetchInhabitant((String)commands.firstElement());
				if(M!=null)
				{
					if(!CMLib.flags().canBeSeenBy(M,mob))
					{
						mob.tell("You don't see "+((String)commands.firstElement())+" here.");
						return false;
					}
					if((!CMLib.flags().isBoundOrHeld(M))&&(!M.willFollowOrdersOf(mob)))
					{
						mob.tell("Only the bound or servants can be mounted unwillingly.");
						return false;
					}
					RI=M;
					RI.setRiding(null);
					commands.removeElementAt(0);
				}
			}
		}
		recipient=CMLib.english().fetchEnvironmental(possRecipients,CMParms.combine(commands,0),true);
		if(recipient==null)
			recipient=CMLib.english().fetchEnvironmental(possRecipients,CMParms.combine(commands,0),false);
		if(recipient==null)
			recipient=mob.location().fetchFromRoomFavorMOBs(null,CMParms.combine(commands,0));
		if((recipient==null)||(!CMLib.flags().canBeSeenBy(recipient,mob)))
		{
			mob.tell("You don't see '"+CMParms.combine(commands,0)+"' here.");
			return false;
		}
		String mountStr=null;
		if(recipient instanceof Rideable)
		{
			if(RI!=null)
				mountStr="<S-NAME> mount(s) <O-NAME> onto <T-NAMESELF>.";
			else
				mountStr="<S-NAME> "+((Rideable)recipient).mountString(CMMsg.TYP_MOUNT,mob)+" <T-NAMESELF>.";
		}
		else
		{
			if(RI!=null)
				mountStr="<S-NAME> mount(s) <O-NAME> to <T-NAMESELF>.";
			else
				mountStr="<S-NAME> mount(s) <T-NAMESELF>.";
		}
		CMMsg msg=CMClass.getMsg(mob,recipient,RI,CMMsg.MSG_MOUNT,mountStr);
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCombatActionCost(ID());}
	public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getActionCost(ID());}
	public boolean canBeOrdered(){return true;}
}
