package com.planet_ink.coffee_mud.Items.Basic;
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
   Copyright 2000-2010 Bo Zimmerman

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
public class QuestPoint extends StdItem
{
	public String ID(){	return "QuestPoint";}
	public QuestPoint()
	{
		super();
		setName("a quest point");
		setDisplayText("A shiny blue coin has been left here.");
		myContainer=null;
		setDescription("A shiny blue coin with magical script around the edges.");
		myUses=Integer.MAX_VALUE;
		myWornCode=0;
		material=0;
		baseEnvStats.setWeight(0);
        baseEnvStats.setSensesMask(baseEnvStats().sensesMask()|EnvStats.SENSE_ITEMNORUIN);
		recoverEnvStats();
	}


	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_REMOVE:
			{
                unWear();
				setContainer(null);
				if(!mob.isMine(this))
					mob.setQuestPoint(mob.getQuestPoint()+1);
				if(!CMath.bset(msg.targetCode(),CMMsg.MASK_OPTIMIZE))
					mob.location().recoverRoomStats();
                destroy();
				return;
			}
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
