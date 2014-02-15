package com.planet_ink.coffee_mud.core.intermud.cm1.commands;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.interfaces.PhysicalAgent;
import com.planet_ink.coffee_mud.core.intermud.cm1.RequestHandler;

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
public class Target extends CM1Command
{
	public String getCommandWord(){ return "TARGET";}
	public Target(RequestHandler req, String parameters) {
		super(req, parameters);
	}

	public void run()
	{
		try
		{
			PhysicalAgent P=getTarget(parameters);
			if(P!=null)
			{
				req.setTarget(P);
				req.sendMsg("[OK]");
				return;
			}
			req.sendMsg("[FAIL]");
		}
		catch(Exception ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
	public boolean passesSecurityCheck(MOB user, PhysicalAgent target){return true;} // anybody can target, its harmless
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: TARGET USER, <NAME>, <NAME>@<LOCATION>, @<LOCATION>";
	}
}
