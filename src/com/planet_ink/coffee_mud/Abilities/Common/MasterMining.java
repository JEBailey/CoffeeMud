package com.planet_ink.coffee_mud.Abilities.Common;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;

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

public class MasterMining extends Mining
{
	public String ID() { return "MasterMining"; }
	public String name(){ return "Master Mining";}
	private static final String[] triggerStrings = {"MMINE","MMINING","MASTERMINE","MASTERMINING"};
	public String[] triggerStrings(){return triggerStrings;}
	
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(125,mob,level,37);
	}
	protected int baseYield() { return 3; }

}
