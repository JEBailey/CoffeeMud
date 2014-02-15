package com.planet_ink.coffee_mud.Abilities.Songs;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Song_Serenity extends Song
{
	public String ID() { return "Song_Serenity"; }
	public String name(){ return "Serenity";}
	public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}
	protected boolean maliciousButNotAggressiveFlag(){return true;}

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return super.okMessage(myHost,msg);
		if(!(affected instanceof MOB)) return super.okMessage(myHost,msg);
		if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(CMLib.flags().canBeHeardSpeakingBy(invoker,msg.source()))
		&&(msg.target()!=null))
		{
			msg.source().makePeace();
			msg.source().tell("You feel too peaceful to fight.");
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
