package com.planet_ink.coffee_mud.Abilities.Skills;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Skill_Haggle extends StdSkill
{
	public String ID() { return "Skill_Haggle"; }
	public String name(){ return "Haggle";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return CAN_MOBS;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings = {"HAGGLE"};
	public String[] triggerStrings(){return triggerStrings;}
	public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_INFLUENTIAL;}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB,affectableStats);
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)+10+getXLEVELLevel(invoker()));
	}


	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String cmd="";
		if(commands.size()>0)
			cmd=((String)commands.firstElement()).toUpperCase();

		if((commands.size()<2)||((!cmd.equals("BUY")&&(!cmd.equals("SELL")))))
		{
			mob.tell("You must specify BUY, SELL, an item, and possibly a ShopKeeper (unless it is implied).");
			return false;
		}

		Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,CMStrings.capitalizeAndLower(cmd)+" what to whom?");
		if(shopkeeper==null) return false;
		if(commands.size()==0)
		{
			mob.tell(CMStrings.capitalizeAndLower(cmd)+" what?");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,shopkeeper,this,CMMsg.MSG_SPEAK,auto?"":"<S-NAME> haggle(s) with <T-NAMESELF>.");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				mob.addEffect(this);
				mob.recoverCharStats();
				commands.insertElementAt(CMStrings.capitalizeAndLower(cmd),0);
				mob.doCommand(commands,Command.METAFLAG_FORCED);
				commands.addElement(shopkeeper.name());
				mob.delEffect(this);
				mob.recoverCharStats();
			}
		}
		else
			beneficialWordsFizzle(mob,shopkeeper,"<S-NAME> haggle(s) with <T-NAMESELF>, but <S-IS-ARE> unconvincing.");

		// return whether it worked
		return success;
	}
}
