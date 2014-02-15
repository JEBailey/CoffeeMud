package com.planet_ink.coffee_mud.Items.ClanItems;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Items.interfaces.ClanItem;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine;
import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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

@SuppressWarnings({"unchecked","rawtypes"})
public class StdClanDonationList extends StdClanItem
{

	private Item lastItem=null;
	
	public StdClanDonationList()
	{
		super();
		setName("a donation list");
		basePhyStats.setWeight(1);
		setDisplayText("an list is setting here.");
		setDescription("");
		setCIType(ClanItem.CI_DONATEJOURNAL);
		CMLib.flags().setReadable(this,true);
		secretIdentity="";
		baseGoldValue=1;
		material=RawMaterial.RESOURCE_PAPER;
	}
	
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((((ClanItem)this).clanID().length()>0)
		&&(CMLib.flags().isGettable(this))
		&&(msg.target()==this)
		&&(owner() instanceof Room))
		{
			Clan C=CMLib.clans().getClan(clanID());
			if((C!=null)&&(C.getDonation().length()>0))
			{
				Room R=CMLib.map().getRoom(C.getDonation());
				if(R==owner())
				{
					CMLib.flags().setGettable(this,false);
					text();
				}
			}
		}
		return super.okMessage(myHost,msg);
	}
	
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(((ClanItem)this).clanID().length()>0)
		{
			if((msg.target()==this)
			&&(msg.targetMinor()==CMMsg.TYP_READ))
			{
				MOB mob=msg.source();
				if(CMLib.flags().canBeSeenBy(this,mob))
				{
					StringBuffer text=new StringBuffer("");
					List<PlayerData> V=CMLib.database().DBReadData(clanID(),"DONATIONS");
					Vector sorted=new Vector();
					String key=null;
					int x=0;
					long val=0;
					DatabaseEngine.PlayerData set=null;
					while(V.size()>0)
					{
						set=V.get(0);
						key=set.key;
						x=key.indexOf('/');
						if(x>0)
						{
							val=CMath.s_long(key.substring(0,x));
							boolean did=false;
							for(int i=0;i<sorted.size();i++)
								if(((Long)((Object[])sorted.elementAt(i))[0]).longValue()>val)
								{
									did=true;
									Object[] O=new Object[2];
									O[0]=Long.valueOf(val);
									O[1]=set.xml;
									sorted.insertElementAt(O,i);
								}
							if(!did) 
							{
								Object[] O=new Object[2];
								O[0]=Long.valueOf(val);
								O[1]=set.xml;
								sorted.addElement(O);
							}
						}
						V.remove(0);
					}
					for(int i=0;i<sorted.size();i++)
						text.append(((String)((Object[])sorted.elementAt(i))[1])+"\n\r");
					if(text.length()>0)
						mob.tell("It says '"+text.toString()+"'.");
					else
						mob.tell("There is nothing written on "+name()+".");
				}
				else
					mob.tell("You can't see that!");
				return;
			}
			else
			if((msg.target() instanceof Item)
			&&(msg.tool() instanceof Ability)
			&&(msg.target()!=lastItem)
			&&(msg.tool().ID().equalsIgnoreCase("Spell_ClanDonate")))
			{
				lastItem=(Item)msg.target();
				CMLib.database().DBCreateData(clanID(),"DONATIONS",System.currentTimeMillis()+"/"+msg.source().Name()+"/"+Math.random(),msg.source().name()+" donated "+msg.target().name()+" at "+msg.source().location().getArea().getTimeObj().getShortTimeDescription()+".");
			}
			else
			if((msg.targetMinor()==CMMsg.TYP_GET)
			&&(msg.target() instanceof Item)
			&&(!msg.targetMajor(CMMsg.MASK_INTERMSG)))
				CMLib.database().DBCreateData(clanID(),"DONATIONS",System.currentTimeMillis()+"/"+msg.source().Name()+"/"+Math.random(),msg.source().name()+" gets "+msg.target().name()+" at "+msg.source().location().getArea().getTimeObj().getShortTimeDescription()+".");
			else
			if(((msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
			&&(msg.target() instanceof Item)
			&&(!msg.targetMajor(CMMsg.MASK_INTERMSG)))
				CMLib.database().DBCreateData(clanID(),"DONATIONS",System.currentTimeMillis()+"/"+msg.source().Name()+"/"+Math.random(),msg.source().name()+" moves "+msg.target().name()+" at "+msg.source().location().getArea().getTimeObj().getShortTimeDescription()+".");
			else
			if((msg.targetMinor()==CMMsg.TYP_DROP)
			&&(msg.target() instanceof Item)
			&&(!msg.targetMajor(CMMsg.MASK_INTERMSG)))
				CMLib.database().DBCreateData(clanID(),"DONATIONS",System.currentTimeMillis()+"/"+msg.source().Name()+"/"+Math.random(),msg.source().name()+" drops "+msg.target().name()+" at "+msg.source().location().getArea().getTimeObj().getShortTimeDescription()+".");
		}
		super.executeMsg(myHost,msg);
	}
}
