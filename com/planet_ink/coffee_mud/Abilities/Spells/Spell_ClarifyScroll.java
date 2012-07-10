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
public class Spell_ClarifyScroll extends Spell
{
	public String ID() { return "Spell_ClarifyScroll"; }
	public String name(){return "Clarify Scroll";}
	public int overrideMana(){return 50;}
	protected int canTargetCode(){return CAN_ITEMS;}
	public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;	}
	public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Item target=getTarget(mob,null,givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!(target instanceof Scroll))
		{
			mob.tell("You can't clarify that.");
			return false;
		}

		if(((Scroll)target).usesRemaining()>((Scroll)target).getSpells().size())
		{
			mob.tell("That scroll can not be enhanced any further.");
			return false;
		}
		
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":"^S<S-NAME> wave(s) <S-HIS-HER> fingers at <T-NAMESELF>, uttering a magical phrase.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,"The words on <T-NAME> become more definite!");
				((Scroll)target).setUsesRemaining(((Scroll)target).usesRemaining()+1);
			}

		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> wave(s) <S-HIS-HER> fingers at <T-NAMESELF>, uttering a magical phrase, and looking very frustrated.");


		// return whether it worked
		return success;
	}
}
