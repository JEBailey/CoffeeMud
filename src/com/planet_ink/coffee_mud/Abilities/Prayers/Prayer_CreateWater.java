package com.planet_ink.coffee_mud.Abilities.Prayers;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class Prayer_CreateWater extends Prayer
{
	public String ID() { return "Prayer_CreateWater"; }
	public String name(){ return "Create Water";}
	public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CREATION;}
	public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;
		if((!(target instanceof Drink))||((target.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_LIQUID))
		{
			mob.tell("You can not create water inside "+target.name(mob)+".");
			return false;
		}
		Drink D=(Drink)target;
		if(D.containsDrink()&&(D.liquidType()!=RawMaterial.RESOURCE_FRESHWATER))
		{
			mob.tell(target.name(mob)+" already contains another liquid, and must be emptied first.");
			return false;
		}
		if(D.containsDrink()&&(D.liquidRemaining()>=D.liquidHeld()))
		{
			mob.tell(target.name(mob)+" is full.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":"^S<S-NAME> "+prayWord(mob)+" over <T-NAME> for water.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().recoverPhyStats();
				D.setLiquidType(RawMaterial.RESOURCE_FRESHWATER);
				D.setLiquidRemaining(D.liquidHeld());
				if(target.owner() instanceof Room)
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,target.name()+" fills up with water!");
				else
					mob.tell(target.name(mob)+" fills up with water!");
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> "+prayWord(mob)+" over <T-NAME> for water, but there is no answer.");

		// return whether it worked
		return success;
	}
}
