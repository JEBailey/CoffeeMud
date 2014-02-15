package com.planet_ink.coffee_mud.Behaviors;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


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
public class CorpseLooter extends StdBehavior
{
	public String ID(){return "CorpseLooter";}

	public String accountForYourself()
	{ 
		return "corpse looting";
	}
	
	int tickTocker=1;
	int tickTock=0;
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(--tickTock>0) return true;
		((MOB)ticking).setBitmap(CMath.setb(((MOB)ticking).getBitmap(),MOB.ATT_AUTOLOOT));
		if((++tickTocker)==100) tickTocker=99;
		tickTock=tickTocker;
		return true;
	}
}
