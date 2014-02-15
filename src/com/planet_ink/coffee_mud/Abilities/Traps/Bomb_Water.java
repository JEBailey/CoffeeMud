package com.planet_ink.coffee_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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
@SuppressWarnings({"unchecked","rawtypes"})
public class Bomb_Water extends StdBomb
{
	public String ID() { return "Bomb_Water"; }
	public String name(){ return "water bomb";}
	protected int trapLevel(){return 1;}
	public String requiresToSet(){return "a water container";}

	public List<Item> getTrapComponents() {
		Vector V=new Vector();
		V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_FRESHWATER));
		return V;
	}
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Drink))
		||(((Drink)P).liquidHeld()!=((Drink)P).liquidRemaining())
		||(((Drink)P).liquidType()!=RawMaterial.RESOURCE_FRESHWATER))
		{
			if(mob!=null)
				mob.tell("You need a full water container to make this out of.");
			return false;
		}
		return true;
	}
	public void spring(MOB target)
	{
		if(target.location()!=null)
		{
			if((target==invoker())
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,"<S-NAME> avoid(s) the water bomb!");
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,affected.name()+" explodes water all over <T-NAME>!"))
			{
				super.spring(target);
				CMLib.utensils().extinguish(invoker(),target,true);
			}
		}
	}

}
