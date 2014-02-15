package com.planet_ink.coffee_mud.Abilities.Properties;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.SLinkedList;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Prop_Trashcan extends Property
{
	public String ID() { return "Prop_Trashcan"; }
	public String name(){ return "Auto purges items put into a container";}
	protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	protected SLinkedList<Item> trashables=new SLinkedList<Item>();
	protected int tickDelay=0;
	protected volatile long lastAddition=0;
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(tickID==Tickable.TICKID_PROPERTY_SPECIAL)
		{
			synchronized(trashables)
			{
				if((System.currentTimeMillis()-lastAddition)<((tickDelay-1)*CMProps.getTickMillis()))
					return true;
				for(Item I : trashables)
					I.destroy();
				lastAddition=0;
				trashables.clear();
				CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
			}
			return false;
		}
		return true;
	}

	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		tickDelay=CMParms.getParmInt(newMiscText, "DELAY", 0);
	}
	
	protected void process(Item I)
	{
		if(tickDelay<=0)
			I.destroy();
		else
		synchronized(trashables)
		{
			if(lastAddition==0)
			{
				CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
				CMLib.threads().startTickDown(this, Tickable.TICKID_PROPERTY_SPECIAL, tickDelay);
			}
			lastAddition=System.currentTimeMillis()-10;
			trashables.add(I);
		}
	}
	
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof Item)
		&&(msg.targetMinor()==CMMsg.TYP_PUT)
		&&(msg.amITarget(affected))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Item))
			process((Item)msg.tool());
		else
		if((affected instanceof Room)
		&&(msg.targetMinor()==CMMsg.TYP_DROP)
		&&(msg.target()!=null)
		&&(msg.target() instanceof Item))
			process((Item)msg.target());
	}
}
