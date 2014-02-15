package com.planet_ink.coffee_mud.Abilities.Properties;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Prop_WearEnabler extends Prop_HaveEnabler
{
	public String ID() { return "Prop_WearEnabler"; }
	public String name(){ return "Granting skills when worn/wielded";}
	protected int canAffectCode(){return Ability.CAN_ITEMS;}
	public boolean checked=false;
	public boolean disabled=false;
	public boolean layered=false;

	public int triggerMask() 
	{ 
		return TriggeredAffect.TRIGGER_WEAR_WIELD;
	}

	public void check(MOB mob, Armor A)
	{
		if(!layered){ checked=true; disabled=false;}
		boolean oldDisabled=disabled;
		if(A.amWearingAt(Wearable.IN_INVENTORY))
		{
			checked=false;
			return;
		}
		if(checked) return;
		Item I=null;
		disabled=false;
		for(int i=0;i<mob.numItems();i++)
		{
			I=mob.getItem(i);
			if((I instanceof Armor)
			&&(!I.amWearingAt(Wearable.IN_INVENTORY))
			&&((I.rawWornCode()&A.rawWornCode())>0)
			&&(I!=A))
			{
				disabled=A.getClothingLayer()<=((Armor)I).getClothingLayer();
				if(disabled)
				{
					break;
				}
			}
		}
		if((!oldDisabled)&&(disabled))
			this.removeMyAffectsFromLastMOB();
		checked=true;
	}

	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		layered=CMParms.parseSemicolons(newText.toUpperCase(),true).indexOf("LAYERED")>=0;
	}
	
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((affected instanceof Armor)&&(msg.source()==((Item)affected).owner()))
		{
			if((msg.targetMinor()==CMMsg.TYP_REMOVE)
			||(msg.sourceMinor()==CMMsg.TYP_WEAR)
			||(msg.sourceMinor()==CMMsg.TYP_WIELD)
			||(msg.sourceMinor()==CMMsg.TYP_HOLD)
			||(msg.sourceMinor()==CMMsg.TYP_DROP))
				checked=false;
			else
			{
				check(msg.source(),(Armor)affected);
				super.executeMsg(host,msg);
			}
		}
		else
			super.executeMsg(host,msg);
	}
	public boolean addMeIfNeccessary(Environmental source, Environmental target, boolean makeLongLasting, short maxTicks)
	{
		if(disabled&&checked) return false;
		return super.addMeIfNeccessary(source,target,makeLongLasting, maxTicks);
	}

	public String accountForYourself()
	{ return spellAccountingsWithMask("Grants "," to the wearer/wielder.");}

	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if(host instanceof Item)
		{
			myItem=(Item)host;

			boolean worn=(!myItem.amWearingAt(Wearable.IN_INVENTORY))
			&&((!myItem.amWearingAt(Wearable.WORN_FLOATING_NEARBY))||(myItem.fitsOn(Wearable.WORN_FLOATING_NEARBY)));
			
			if((lastMOB instanceof MOB)
			&&(((MOB)lastMOB).location()!=null)
			&&((myItem.owner()!=lastMOB)||(!worn)))
				removeMyAffectsFromLastMob();

			if((lastMOB==null)
			&&(worn)
			&&(myItem.owner()!=null)
			&&(myItem.owner() instanceof MOB)
			&&(((MOB)myItem.owner()).location()!=null))
			{
				if(myItem instanceof Armor)
					check((MOB)myItem.owner(),((Armor)myItem));
				addMeIfNeccessary(myItem.owner(),myItem.owner(),false,maxTicks);
			}
		}
		processing=false;
	}
}
