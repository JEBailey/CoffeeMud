package com.planet_ink.coffee_mud.Items.Basic;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

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
public class GenMobilePortal extends GenPortal implements Rideable, Exit
{
	public String ID(){ return "GenMobilePortal";}
	
	protected StdPortal myStationaryPortal=null;
	
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_DISMOUNT:
			break;
		case CMMsg.TYP_ENTER:
		{
			Room R=CMLib.map().roomLocation(this);
			if((myStationaryPortal!=null)
			&&(!myStationaryPortal.amDestroyed()))
				myStationaryPortal.setReadableText(CMLib.map().getExtendedRoomID(R));
			else
			{
				myStationaryPortal=null;
				Room destR=getDestinationRoom();
				Vector choices=new Vector();
				for(int i=0;i<destR.numItems();i++)
				{
					Item I=destR.getItem(i);
					if((I!=null)&&(I instanceof StdPortal))
						choices.addElement(I);
				}
				MOB M=null;
				for(int m=0;m<destR.numInhabitants();m++)
				{
					M=destR.fetchInhabitant(m);
					if(M!=null)
						for(int i=0;i<M.numItems();i++)
						{
							Item I=M.getItem(i);
							if((I!=null)&&(I instanceof StdPortal))
								choices.addElement(I);
						}
				}
				if(choices.size()>0)
				{
					if(choices.size()==1)
						myStationaryPortal=(StdPortal)choices.firstElement();
					else
					{
						if(((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))&&(secretIdentity().length()>0))
						for(int i=0;i<choices.size();i++)
							if(((Item)choices.elementAt(i)).secretIdentity().equals(secretIdentity()))
							{ myStationaryPortal=(StdPortal)choices.elementAt(i); break;}
						if((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))
						for(int i=0;i<choices.size();i++)
							if(((Item)choices.elementAt(i)).Name().equals(Name()))
							{ myStationaryPortal=(StdPortal)choices.elementAt(i); break;}
						if((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))
							myStationaryPortal=(StdPortal)choices.firstElement();
					}
				}
			}
			break;
		}
		}
	}
   
}
