package com.planet_ink.coffee_mud.Races;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Skeleton extends Undead
{
	public String ID(){	return "Skeleton"; }
	public String name(){ return "Skeleton"; }

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(myHost instanceof MOB)
		{
			MOB mob=(MOB)myHost;
			if((msg.amITarget(mob))
			&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Weapon)
			&&((((Weapon)msg.tool()).weaponType()==Weapon.TYPE_PIERCING)
				||(((Weapon)msg.tool()).weaponType()==Weapon.TYPE_SLASHING))
			&&(!mob.amDead()))
			{
				int recovery=(int)Math.round(CMath.div((msg.value()),2.0));
				msg.setValue(recovery);
			}
		}
		return super.okMessage(myHost,msg);
	}

	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<2;i++)
					resources.addElement(makeResource
						("knuckle bone",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
						("a skull",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
						("a bone",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
