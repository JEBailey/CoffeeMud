package com.planet_ink.coffee_mud.Items.Basic;
import java.util.Hashtable;
import java.util.List;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;

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
public class BardMap extends GenMap
{
	public String ID(){	return "BardMap";}
	public BardMap()
	{
		super();
		setName("a map");
		basePhyStats.setWeight(0);
		setDisplayText("a map is rolled up here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(3);
		setMaterial(RawMaterial.RESOURCE_PAPER);
		recoverPhyStats();
	}

	public void doMapArea()
	{
		//myMap=null;
	}

	public StringBuffer[][] getMyMappedRoom(int width)
	{
		StringBuffer[][] myMap=null;
		//if(myMap!=null)	return myMap;
		myMap=finishMapMaking(width);
		return myMap;
	}

	public Hashtable makeMapRooms(int width)
	{
		String newText=getMapArea();
		List<String> mapAreas=CMParms.parseSemicolons(newText,true);
		Hashtable mapRooms=new Hashtable();
		for(int a=0;a<mapAreas.size();a++)
		{
			String area=mapAreas.get(a);
			Room room=CMLib.map().getRoom(area);
			if(room!=null)
			{
				MapRoom mr=new MapRoom();
				mr.r=room;
				mapRooms.put(room,mr);
			}
		}
		super.clearTheSkys(mapRooms);
		return mapRooms;
	}
}
