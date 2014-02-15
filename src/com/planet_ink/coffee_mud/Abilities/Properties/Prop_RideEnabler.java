package com.planet_ink.coffee_mud.Abilities.Properties;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
import com.planet_ink.coffee_mud.core.interfaces.Rider;

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
public class Prop_RideEnabler extends Prop_HaveEnabler
{
	public String ID() { return "Prop_RideEnabler"; }
	public String name(){ return "Granting skills when ridden";}
	protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS;}
	protected Vector lastRiders=new Vector();
	
	public String accountForYourself()
	{ return spellAccountingsWithMask("Grants "," to those mounted.");}

	public int triggerMask() { return TriggeredAffect.TRIGGER_MOUNT; }

	public void setMiscText(String newText)
	{ 
		super.setMiscText(newText);
		lastRiders=new Vector();
	}
	
	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if(affected instanceof Rideable)
		{
			Rideable RI=(Rideable)affected;
			for(int r=0;r<RI.numRiders();r++)
			{
				Rider R=RI.fetchRider(r);
				if(R instanceof MOB)
				{
					MOB M=(MOB)R;
					if((!lastRiders.contains(M))&&(RI.amRiding(M)))
					{
						if(addMeIfNeccessary(M,M,false,maxTicks))
							lastRiders.add(M);
					}
				}
			}
			for(int i=lastRiders.size()-1;i>=0;i--)
			{
				MOB M=(MOB)lastRiders.elementAt(i);
				if(!RI.amRiding(M))
				{
					removeMyAffectsFrom(M);
					while(lastRiders.contains(M))
						lastRiders.removeElement(M);
				}
			}
		}
		processing=false;
	}
}
