package com.planet_ink.coffee_mud.Abilities.Skills;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

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
public class Skill_Recall extends StdSkill
{
	public String ID() { return "Skill_Recall"; }
	public String name(){ return "Recall";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return 0;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings = {"RECALL","/"};
	public String[] triggerStrings(){return triggerStrings;}
	public int classificationCode(){return Ability.ACODE_SKILL;}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=(!mob.isInCombat())||proficiencyCheck(mob,0,auto);
		if(success)
		{
			Room recalledRoom=mob.location();
			Room recallRoom=mob.getStartRoom();
			CMMsg msg=CMClass.getMsg(mob,recalledRoom,this,CMMsg.MSG_RECALL,CMMsg.MSG_LEAVE,CMMsg.MSG_RECALL,auto?getScr("Skills","recallgo1"):getScr("Skills","recallgo2"));
			CMMsg msg2=CMClass.getMsg(mob,recallRoom,this,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,null);
			if((recalledRoom.okMessage(mob,msg))&&(recallRoom.okMessage(mob,msg2)))
			{
				if(mob.isInCombat())
					CMLib.commands().postFlee(mob,"NOWHERE");
				recalledRoom.send(mob,msg);
				recallRoom.send(mob,msg2);
				if(recalledRoom.isInhabitant(mob))
					recallRoom.bringMobHere(mob,false);
				for(int f=0;f<mob.numFollowers();f++)
				{
					MOB follower=mob.fetchFollower(f);
					
					msg=CMClass.getMsg(follower,recalledRoom,this,CMMsg.MSG_RECALL,CMMsg.MSG_LEAVE,CMMsg.MSG_RECALL,auto?getScr("Skills","recallgo1"):getScr("Skills","recallgo3",mob.name()));
					if((follower!=null)
					&&(follower.isMonster())
					&&(!follower.isPossessing())
					&&(follower.location()==recalledRoom)
					&&(recalledRoom.isInhabitant(follower))
					&&(recalledRoom.okMessage(follower,msg)))
					{
						msg2=CMClass.getMsg(follower,recallRoom,this,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,null);
						if(recallRoom.okMessage(follower,msg2))
						{
							if(follower.isInCombat())
								CMLib.commands().postFlee(follower,("NOWHERE"));
							recallRoom.send(follower,msg2);
							if(recalledRoom.isInhabitant(follower))
								recallRoom.bringMobHere(follower,false);
						}
					}
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,getScr("Skills","recallgo4"));

		// return whether it worked
		return success;
	}

}
