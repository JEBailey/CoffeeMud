package com.planet_ink.coffee_mud.Races;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;

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
public class Sasquatch extends Gorilla
{
	public String ID(){	return "Sasquatch"; }
	public String name(){ return "Sasquatch"; }
	public long forbiddenWornBits(){return 0;}
	public String racialCategory(){return "Primate";}
	private String[]culturalAbilityNames={"Gigantic"};
	private int[]culturalAbilityProficiencies={100};
	public String[] culturalAbilityNames(){return culturalAbilityNames;}
	public int[] culturalAbilityProficiencies(){return culturalAbilityProficiencies;}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	public int[] bodyMask(){return parts;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+5);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)+5);
	}
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<4;i++)
					resources.addElement(makeResource
					("a strip of "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" spleen",RawMaterial.RESOURCE_HIDE));
				resources.addElement(makeResource
				("a pound of "+name().toLowerCase()+" flesh",RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
