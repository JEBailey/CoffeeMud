package com.planet_ink.coffee_mud.core.intermud.i3.server;

import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.intermud.i3.packets.ImudServices;
import com.planet_ink.coffee_mud.core.intermud.i3.packets.ShutdownPacket;

/*
 * com.planet_ink.coffee_mud.core.intermud.i3.server.Server
 * Copyright (c) 1996 George Reese
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
 * The mudlib interface to the server.
 */
/**
 * The Server class is the mudlib's interface to the Imaginary Mud Server. It is
 * responsible with knowing all internal information about the server. Last
 * Update: 960921
 * 
 * @author George Reese
 * @version 1.0
 */
public class I3Server {
	static private ServerThread serverClient = null;
	static private boolean started = false;

	/**
	 * Creates a server thread if one has not yet been created.
	 * 
	 * @exception DatabaseException
	 *                thrown if the database is unreachable for some reason
	 * @exception ServerSecurityException
	 *                thrown if an attempt to call start() is made once the
	 *                server is running.
	 * @param mud
	 *            the name of the mud being started
	 */
	static public void start(String mud, int port, ImudServices imud) {
		try {
			if (started) {
				throw new ServerSecurityException(
						"Illegal attempt to start Server.");
			}
			started = true;
			serverClient = new ServerThread(mud, port, imud);
			Log.sysOut("I3Server", "InterMud3 Core (c)1996 George Reese");
			serverClient.start();
		} catch (Exception e) {
			serverClient = null;
			Log.errOut("I3Server", e);
		}
	}

	/**
	 * Returns a distinct copy of the class identified.
	 * 
	 * @exception ObjectLoadException
	 *                thrown when a problem occurs loading the object
	 * @param file
	 *            the name of the class being loaded
	 */
	static public ServerObject copyObject(String file)
			throws ObjectLoadException {
		return serverClient.copyObject(file);
	}

	static public ServerObject findObject(String file)
			throws ObjectLoadException {
		return serverClient.findObject(file);
	}

	static public ServerUser[] getInteractives() {
		return serverClient.getInteractives();
	}

	static public String getMudName() {
		return serverClient.getMudName();
	}

	static public int getPort() {
		return serverClient.getPort();
	}

	static public void shutdown() {
		try {
			try {
				ShutdownPacket shutdown = new ShutdownPacket();
				shutdown.send();
			} catch (Exception e) {
			}
			serverClient.shutdown();
			started = false;
		} catch (Exception e) {
		}
	}

	static public void removeObject(ServerObject ob) {
		if (!ob.getDestructed()) {
			return;
		}
		serverClient.removeObject(ob);
	}
}