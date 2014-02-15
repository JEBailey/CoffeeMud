package com.planet_ink.coffee_mud.Abilities.Songs;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.Directions;
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
public class Play_Retreat extends Play
{
	public String ID() { return "Play_Retreat"; }
	public String name(){ return "Retreat";}
	public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	protected int canAffectCode(){return 0;}
	protected boolean persistantSong(){return false;}
	protected String songOf(){return "a "+name();}
	protected boolean HAS_QUANTITATIVE_ASPECT(){return true;}
	int directionCode=-1;

	protected void inpersistantAffect(MOB mob)
	{
		if(directionCode<0)
		{
			mob.tell("Flee where?!");
			return;
		}
		mob.makePeace();
		CMLib.tracking().walk(mob,directionCode,true,false);
	}

	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}
	
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		directionCode=-1;
		String where=CMParms.combine(commands,0);
		if(!where.equals("NOWHERE"))
		{
			if(where.length()==0)
			{
				Vector directions=new Vector();
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					Exit thisExit=mob.location().getExitInDir(d);
					Room thisRoom=mob.location().getRoomInDir(d);
					if((thisRoom!=null)&&(thisExit!=null)&&(thisExit.isOpen()))
						directions.addElement(Integer.valueOf(d));
				}
				// up is last resort
				if(directions.size()>1)
					directions.removeElement(Integer.valueOf(Directions.UP));
				if(directions.size()>0)
				{
					directionCode=((Integer)directions.elementAt(CMLib.dice().roll(1,directions.size(),-1))).intValue();
					where=Directions.getDirectionName(directionCode);
				}
			}
			else
				directionCode=Directions.getGoodDirectionCode(where);
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		return true;
	}
}
