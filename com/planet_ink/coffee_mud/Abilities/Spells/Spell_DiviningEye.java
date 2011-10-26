package com.planet_ink.coffee_mud.Abilities.Spells;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2011 Bo Zimmerman

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
@SuppressWarnings("unchecked")
public class Spell_DiviningEye extends Spell
{
	public String ID() { return "Spell_DiviningEye"; }
	public String name(){return "Divining Eye";}
	public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}
	public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()==0)
		{
			mob.tell("You must specify a divining spell and any parameters for it.");
			return false;
		}
		
		Ability pryingEyeA=mob.fetchEffect("Spell_PryingEye");
		if(pryingEyeA==null)
		{
			mob.tell("This spell requires an active prying eye.");
			return false;
		}
		
		String commandStr=CMParms.combine(commands);
		commands.insertElementAt("CAST",0);
		Ability A=CMLib.english().getToEvoke(mob, commands);
		if(A==null)
		{
			mob.tell("'"+commandStr+"' does not refer to any diviner spell you know.");
			return false;
		}
		if(((A.classificationCode() & Ability.ALL_ACODES)!=Ability.ACODE_SPELL)
		||((A.classificationCode() & Ability.ALL_DOMAINS)!=Ability.DOMAIN_DIVINATION))
		{
			mob.tell("'"+A.name()+"' is not a diviner spell you know.");
			return false;
		}
		
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),auto?"":"^S<S-NAME> invoke(s) a remote divination!^?");
			final Room room=mob.location();
			if(room.okMessage(mob,msg))
			{
				room.send(mob,msg);
				final String myName=mob.Name();
				try
				{
					final MOB eyeM=(MOB)pryingEyeA.affecting();
					final Room eyeRoom=eyeM.location();
					if(eyeRoom!=null)
					{
						mob.setName(eyeM.Name());
						room.delInhabitant(mob);
						mob.setLocation(eyeRoom);
						A.invoke(mob, commands, null, false, 0);
					}
				}
				finally
				{
					mob.setName(myName);
					if(mob.location()!=room)
					{
						mob.location().delInhabitant(mob);
						room.addInhabitant(mob);
						mob.setLocation(room);
					}
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,null,"<S-NAME> attempt(s) to invoke something, but fail(s).");

		// return whether it worked
		return success;
	}
}
