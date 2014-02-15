package com.planet_ink.coffee_mud.Abilities.Druid;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;

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

public class Druid_ShapeShift4 extends Druid_ShapeShift
{
	public String ID() { return "Druid_ShapeShift4"; }
	public String name(){ return "Fourth Totem";}
	public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	public String[] triggerStrings(){return empty;}


}
