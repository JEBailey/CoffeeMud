package com.planet_ink.coffee_mud.Abilities.Songs;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
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
public class Song_Valor extends Song
{
	public String ID() { return "Song_Valor"; }
	public String name(){ return "Valor";}
	public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker!=null)
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
											+invoker().charStats().getStat(CharStats.STAT_CHARISMA)
											+super.adjustedLevel(invoker(),0));
	}
}
