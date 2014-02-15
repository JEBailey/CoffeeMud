package com.planet_ink.coffee_mud.Abilities.Skills;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Scroll;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
@SuppressWarnings("rawtypes")
public class Skill_Write extends StdSkill
{
	public String ID() { return "Skill_Write"; }
	public String name(){ return "Write";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_ITEMS;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings = {"WRITE","WR"};
	public String[] triggerStrings(){return triggerStrings;}
	public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_CALLIGRAPHY;}
	public int overrideMana(){return 0;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.charStats().getStat(CharStats.STAT_INTELLIGENCE)<5)
		{
			mob.tell("You are too stupid to actually write anything.");
			return false;
		}
		if(commands.size()<1)
		{
			mob.tell("What would you like to write on?");
			return false;
		}
		Item target=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,(String)commands.elementAt(0));
		if(target==null)
		{
			target=mob.location().findItem(null,(String)commands.elementAt(0));
			if((target!=null)&&(CMLib.flags().isGettable(target)))
			{
				mob.tell("You don't have that.");
				return false;
			}
		}
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell("You don't see '"+((String)commands.elementAt(0))+"' here.");
			return false;
		}

		Item item=target;
		if(((item.material()!=RawMaterial.RESOURCE_PAPER)
		   &&(item.material()!=RawMaterial.RESOURCE_SILK)
		   &&(item.material()!=RawMaterial.RESOURCE_HIDE)
		   &&(item.material()!=RawMaterial.RESOURCE_HEMP))
		||(!item.isReadable()))
		{
			mob.tell("You can't write on that.");
			return false;
		}

		if(item instanceof Scroll)
		{
			mob.tell("You can't write on a scroll.");
			return false;
		}

		if(CMParms.combine(commands,1).toUpperCase().startsWith("FILE="))
		{
			mob.tell("You can't write that.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_WRITE,"<S-NAME> write(s) on <T-NAMESELF>.",CMMsg.MSG_WRITE,CMParms.combine(commands,1),CMMsg.MSG_WRITE,"<S-NAME> write(s) on <T-NAMESELF>.");
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
			mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,"<S-NAME> attempt(s) to write on <T-NAMESELF>, but mess(es) up.");
		return success;
	}

}
