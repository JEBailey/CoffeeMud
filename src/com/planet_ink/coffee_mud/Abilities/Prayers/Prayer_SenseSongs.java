package com.planet_ink.coffee_mud.Abilities.Prayers;

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
public class Prayer_SenseSongs extends Prayer_SenseProfessions
{
	public String ID() { return "Prayer_SenseSongs"; }
	public String name(){ return "Sense Songs";}
	protected int senseWhat() { return ACODE_SONG; }
	protected String senseWhatStr() { return "songs"; }
}
