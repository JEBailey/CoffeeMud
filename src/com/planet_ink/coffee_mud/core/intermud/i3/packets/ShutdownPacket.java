package com.planet_ink.coffee_mud.core.intermud.i3.packets;
import java.util.Vector;

import com.planet_ink.coffee_mud.core.intermud.i3.server.I3Server;

/**
 * Copyright (c) 2010-2014 Bo Zimmerman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
@SuppressWarnings("rawtypes")
public class ShutdownPacket extends Packet 
{
	public ShutdownPacket()
	{
		super();
		type = Packet.SHUTDOWN;
	}
	
	public ShutdownPacket(Vector v)
	{
		super(v);
		type = Packet.SHUTDOWN;
		target_mud=Intermud.getNameServer().name;
	}

	public void send() throws InvalidPacketException 
	{
		super.send();
	}

	public String toString() 
	{
		return "({\"shutdown\",5,\""+I3Server.getMudName()+"\",0,\""+target_mud+"\",0,0,})";
	}
}
