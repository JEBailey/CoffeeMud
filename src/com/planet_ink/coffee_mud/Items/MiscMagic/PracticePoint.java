package com.planet_ink.coffee_mud.Items.MiscMagic;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.Items.interfaces.MiscMagic;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class PracticePoint extends StdItem implements MiscMagic
{
	public String ID(){ return "PracticePoint";}
	public PracticePoint()
	{
		super();
		setName("a practice point");
		setDisplayText("A shiny green coin has been left here.");
		myContainer=null;
		setDescription("A shiny green coin with magical script around the edges.");
		myUses=Integer.MAX_VALUE;
		myWornCode=0;
		material=0;
		basePhyStats.setWeight(0);
		basePhyStats.setSensesMask(basePhyStats().sensesMask()|PhyStats.SENSE_ITEMNORUIN|PhyStats.SENSE_ITEMNOWISH);
		recoverPhyStats();
	}


	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_REMOVE:
			{
				setContainer(null);
				destroy();
				if(!mob.isMine(this))
					mob.setPractices(mob.getPractices()+1);
				unWear();
				if(!CMath.bset(msg.targetMajor(),CMMsg.MASK_OPTIMIZE))
					mob.location().recoverRoomStats();
				return;
			}
			default:
				break;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
