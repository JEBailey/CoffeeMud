package com.planet_ink.coffee_mud.Abilities.Thief;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
@SuppressWarnings("rawtypes")
public class Thief_SilentOpen extends ThiefSkill
{
	public String ID() { return "Thief_SilentOpen"; }
	public String name(){ return "Silent Open";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	private static final String[] triggerStrings = {"SILENTOPEN","SOPEN"};
	public String[] triggerStrings(){return triggerStrings;}
	public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=0;

	public int abilityCode(){return code;}
	public void setAbilityCode(int newCode){code=newCode;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((commands.size()<1)&&(givenTarget==null))
		{
			mob.tell("What would you like to open?");
			return false;
		}
		Environmental item=super.getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(item==null) return false;
		if((item instanceof MOB)
		||(item instanceof Area)
		||(item instanceof Room))
		{
			mob.tell("You can't open that!");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,item,this,CMMsg.MSG_THIEF_ACT,"<S-NAME> open(s) <T-NAME>.",CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				CMLib.commands().postOpen(mob,item,true);
			}
		}
		else
		{
			beneficialVisualFizzle(mob,item,"<S-NAME> attempt(s) to open <T-NAME> quietly, but fail(s).");
			CMLib.commands().postOpen(mob,item,false);
		}
		return success;
	}
}
