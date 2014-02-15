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
public class Shark extends GiantFish
{
	public String ID(){ return "Shark"; }
	public String name(){ return "Shark"; }
	public int shortestMale(){return 10;}
	public int shortestFemale(){return 15;}
	public int heightVariance(){return 20;}
	public int lightestWeight(){return 355;}
	public int weightVariance(){return 105;}
	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE,1);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH,16);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY,15);
	}
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<25;i++)
				resources.addElement(makeResource
				("some "+name().toLowerCase(),RawMaterial.RESOURCE_FISH));
				for(int i=0;i<15;i++)
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_HIDE));
				for(int i=0;i<5;i++)
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" tooth",RawMaterial.RESOURCE_BONE));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
