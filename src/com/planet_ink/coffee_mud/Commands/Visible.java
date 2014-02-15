package com.planet_ink.coffee_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Visible extends StdCommand
{
	public Visible(){}

	private final String[] access={"VISIBLE","VIS"};
	public String[] getAccessWords(){return access;}
	
	public static java.util.List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		MOB newMOB=CMClass.getFactoryMOB();
		Vector offenders=new Vector();
		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			Ability A=fromMe.fetchEffect(a);
			if((A!=null)&&(A.canBeUninvoked()))
			{
				try
				{
					newMOB.recoverPhyStats();
					A.affectPhyStats(newMOB,newMOB.phyStats());
					if(CMLib.flags().isInvisible(newMOB)||CMLib.flags().isHidden(newMOB))
					  offenders.addElement(A);
				}
				catch(Exception e)
				{}
			}
		}
		newMOB.destroy();
		return offenders;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String str="Prop_WizInvis";
		Ability A=mob.fetchEffect(str);
		boolean didSomething=false;
		if(A!=null)
		{
			Command C=CMClass.getCommand("WizInv");
			if((C!=null)&&(C.securityCheck(mob)))
			{
				didSomething=true;
				C.execute(mob,new XVector("WIZINV","OFF"),metaFlags);
			}
		}
		java.util.List V=returnOffensiveAffects(mob);
		if(V.size()==0)
		{
			if(!didSomething)
			mob.tell("You are not invisible or hidden!");
		}
		else
		for(int v=0;v<V.size();v++)
			((Ability)V.get(v)).unInvoke();
		return false;
	}
	public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getActionCost(ID());}
	public double combatActionsCost(MOB mob, List<String> cmds){return 0.25;}
	public boolean canBeOrdered(){return true;}
	
}
