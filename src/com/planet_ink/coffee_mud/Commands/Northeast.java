package com.planet_ink.coffee_mud.Commands;
import java.util.Vector;

import com.planet_ink.coffee_mud.Items.interfaces.SpaceShip;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;

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
public class Northeast extends Go
{
	public Northeast(){}

	private final String[] access={"NORTHEAST","NE"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		standIfNecessary(mob,metaFlags);
		if((CMLib.flags().isSitting(mob))||(CMLib.flags().isSleeping(mob)))
		{
			mob.tell("You need to stand up first.");
			return false;
		}
		if(CMath.bset(mob.getBitmap(),MOB.ATT_AUTORUN))
			CMLib.tracking().run(mob, Directions.NORTHEAST, false,false,false);
		else
			CMLib.tracking().walk(mob, Directions.NORTHEAST, false,false,false);
		return false;
	}
	public boolean canBeOrdered(){return true;}

	public boolean securityCheck(MOB mob)
	{
		if(Directions.NUM_DIRECTIONS()<=6)
			return false;
		return (mob==null) || (mob.isMonster()) || (mob.location()==null) 
				|| ((!(mob.location() instanceof SpaceShip)) && (!(mob.location().getArea() instanceof SpaceShip)));
	}
}

