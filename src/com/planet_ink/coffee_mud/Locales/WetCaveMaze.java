package com.planet_ink.coffee_mud.Locales;
import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.interfaces.Places;

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
@SuppressWarnings("unchecked")
public class WetCaveMaze extends StdMaze
{
	public String ID(){return "WetCaveMaze";}
	public WetCaveMaze()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_DARK);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	public int domainType(){return Room.DOMAIN_INDOORS_CAVE;}

	public String getGridChildLocaleID(){return "WetCaveRoom";}
	public int maxRange(){return 5;}
	public List<Integer> resourceChoices(){return CaveRoom.roomResources;}
}
