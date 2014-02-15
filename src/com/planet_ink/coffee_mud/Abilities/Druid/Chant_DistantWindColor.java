package com.planet_ink.coffee_mud.Abilities.Druid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Chant_DistantWindColor extends Chant
{
	public String ID() { return "Chant_DistantWindColor"; }
	public String name(){ return "Distant Wind Color";}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_WEATHER_MASTERY;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return CAN_ROOMS;}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		if(commands.size()<1)
		{
			mob.tell("Discern the wind color where?");
			return false;
		}

		String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		Room anyRoom=null;
		Room newRoom=null;
		try
		{
			List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName, true, 10);
			for(Room R : rooms)
			{
				anyRoom=R;
				if(((R.domainType()&Room.INDOORS)==0)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_WATERSURFACE))
				{
					newRoom=R;
					break;
				}
			}
		}catch(NoSuchElementException e){}

		if(newRoom==null)
		{
			if(anyRoom==null)
				mob.tell("You don't know of a place called '"+CMParms.combine(commands,0)+"'.");
			else
			if((anyRoom.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
			||(anyRoom.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
				mob.tell("There IS such a place, but it is on or in the water, so your magic would fail.");
			else
				mob.tell("There IS such a place, but it is not outdoors, so your magic would fail.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),"^S<S-NAME> chant(s) about a far away place.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				String msg2=Chant_WindColor.getWindColor(mob,newRoom);
				if(msg2.length()==0)
					mob.tell("The winds at "+newRoom.displayText(mob)+" are clear.");
				else
					mob.tell("The winds at "+newRoom.displayText(mob)+" are "+msg2+".");
			}
		}
		else
			beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) about a far away place, but the magic fades.");


		// return whether it worked
		return success;
	}
}
