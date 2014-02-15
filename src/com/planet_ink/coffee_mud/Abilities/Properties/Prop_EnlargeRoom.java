package com.planet_ink.coffee_mud.Abilities.Properties;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Prop_EnlargeRoom extends Property
{
	public String ID() { return "Prop_EnlargeRoom"; }
	public String name(){ return "Change a rooms movement requirements";}
	protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	public String accountForYourself()
	{ return "Enlarged";	}

	public long flags(){return Ability.FLAG_ADJUSTER;}

	protected double dval(String s)
	{
		if(s.indexOf('.')>=0)
			return CMath.s_double(s);
		return CMath.s_int(s);
	}

	protected int ival(String s)
	{
		return (int)Math.round(dval(s));
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(text().length()>0)
		{
			int weight=affectableStats.weight();
			switch(text().charAt(0))
			{
			case '+':
				affectableStats.setWeight(weight+ival(text().substring(1).trim()));
				break;
			case '-':
				affectableStats.setWeight(weight-ival(text().substring(1).trim()));
				break;
			case '*':
				affectableStats.setWeight((int)Math.round(CMath.mul(weight,dval(text().substring(1).trim()))));
				break;
			case '/':
				affectableStats.setWeight((int)Math.round(CMath.div(weight,dval(text().substring(1).trim()))));
				break;
			default:
				affectableStats.setWeight(ival(text()));
				break;
			}
		}
	}
}
