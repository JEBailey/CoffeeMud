package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import java.util.*;

/* 
   Copyright 2004-2006 Robert Little 
	http://www.tttgames.divineright.org
	 The Looking Glass RPG
   www.tttgames.divineright.org  host: divineright.org port: 7000
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
public class Prop_IceBox extends Property
{
	public String ID() { return "Prop_IceBox"; }
	public String name(){ return "Works like an ice box";}
	protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_PUT:
			if(affected instanceof Item)
			{
				if(msg.amITarget(affected)&&(msg.tool() instanceof Decayable))
					((Decayable)msg.tool()).setDecayTime(Long.MAX_VALUE);
			}
			break;
		case CMMsg.TYP_GET:
			if((msg.target() instanceof Decayable)&&(msg.target() instanceof Item))
			{
				if((affected instanceof Item)
				&&(((Item)msg.target()).container()==affected))
					((Decayable)msg.tool()).setDecayTime(0); // will cause a recalc on next msg
				if((affected instanceof Room)
				&&(((Item)msg.target()).owner()==affected))
					((Decayable)msg.tool()).setDecayTime(0); // will cause a recalc on next msg
			}
			break;
		case CMMsg.TYP_DROP:
			if(affected instanceof Room)
			{
				if(msg.target() instanceof Decayable)
					((Decayable)msg.target()).setDecayTime(Long.MAX_VALUE);
			}
			break;
		}
		return true;
	}	
}
