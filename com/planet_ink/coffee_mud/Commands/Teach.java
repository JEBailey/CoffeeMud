package com.planet_ink.coffee_mud.Commands;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary.ExpertiseDefinition;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/* 
   Copyright 2000-2012 Bo Zimmerman

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
public class Teach extends StdCommand
{
	public Teach(){}

	private final String[] access={"TEACH"};
	public String[] getAccessWords(){return access;}
	
	
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell("Teach who what?");
			return false;
		}
		commands.removeElementAt(0);


		MOB student=mob.location().fetchInhabitant((String)commands.elementAt(0));
		if((student==null)||(!CMLib.flags().canBeSeenBy(student,mob)))
		{
			mob.tell("That person doesn't seem to be here.");
			return false;
		}
		commands.removeElementAt(0);


		String abilityName=CMParms.combine(commands,0);
		Ability realAbility=CMClass.findAbility(abilityName,student.charStats());
		Ability myAbility=null;
		if(realAbility!=null)
			myAbility=mob.fetchAbility(realAbility.ID());
		else
			myAbility=mob.findAbility(abilityName);
		if(myAbility==null)
		{
			ExpertiseLibrary.ExpertiseDefinition theExpertise=null;
			List<ExpertiseDefinition> V=CMLib.expertises().myListableExpertises(mob);
			for(Enumeration<String> exi=mob.expertises();exi.hasMoreElements();)
			{
				final String experID=exi.nextElement();
				if(experID!=null)
				{
					ExpertiseLibrary.ExpertiseDefinition def=CMLib.expertises().getDefinition(experID);
					if((def != null) && (!V.contains(def))) 
						V.add(def);
				}
			}
			for(int v=0;v<V.size();v++)
			{
				ExpertiseLibrary.ExpertiseDefinition def=V.get(v);
				if((def.name.equalsIgnoreCase(abilityName))
				&&(theExpertise==null))
					theExpertise=def;
			}
			if(theExpertise==null)
			for(int v=0;v<V.size();v++)
			{
				ExpertiseLibrary.ExpertiseDefinition def=V.get(v);
				if((CMLib.english().containsString(def.name,abilityName)
				&&(theExpertise==null)))
					theExpertise=def;
			}
			if(theExpertise!=null)
			{
				return CMLib.expertises().postTeach(mob,student,theExpertise);
			}
			mob.tell("You don't seem to know "+abilityName+".");
			return false;
		}
		return CMLib.expertises().postTeach(mob,student,myAbility);
	}
	public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCombatActionCost(ID());}
	public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getActionCost(ID());}
	public boolean canBeOrdered(){return true;}

	
}
