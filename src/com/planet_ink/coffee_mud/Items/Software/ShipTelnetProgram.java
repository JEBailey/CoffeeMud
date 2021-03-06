package com.planet_ink.coffee_mud.Items.Software;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import com.planet_ink.coffee_mud.Items.interfaces.ArchonOnly;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;

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
public class ShipTelnetProgram extends GenShipProgram implements ArchonOnly {
	public String ID() {
		return "StdShipTelnetProgram";
	}

	protected Socket sock = null;
	protected BufferedInputStream reader = null;
	protected BufferedWriter writer = null;
	protected volatile long nextPowerCycleTmr = System.currentTimeMillis()
			+ (8 * 1000);

	public ShipTelnetProgram() {
		super();
		setName("a telnet disk");
		setDisplayText("a small disk sits here.");
		setDescription("It appears to be a telnet program.");

		material = RawMaterial.RESOURCE_STEEL;
		baseGoldValue = 1000;
		recoverPhyStats();
	}

	public String getParentMenu() {
		return "";
	}

	public String getInternalName() {
		return "TELNET";
	}

	public boolean isActivationString(String word) {
		return isCommandString(word, false);
	}

	public boolean isDeActivationString(String word) {
		return isCommandString(word, false);
	}

	public void onDeactivate(MOB mob, String message) {
		shutdown();
		super.addScreenMessage("Telnet connection closed.");
	}

	public boolean isCommandString(String word, boolean isActive) {
		if (!isActive) {
			word = word.toUpperCase();
			return (word.startsWith("TELNET ") || word.equals("TELNET"));
		} else {
			return true;
		}
	}

	public String getActivationMenu() {
		return "TELNET [HOST] [PORT]: Telnet Network Software";
	}

	protected void shutdown() {
		currentScreen = "";
		synchronized (this) {
			try {
				try {
					if (sock != null) {
						sock.shutdownInput();
						sock.shutdownOutput();
					}
					if (reader != null)
						reader.close();
					if (writer != null)
						writer.close();
				} catch (Exception e) {
				} finally {
					sock.close();
				}
			} catch (Exception e) {
			} finally {
				sock = null;
				reader = null;
				writer = null;
			}
		}
	}

	public boolean checkActivate(MOB mob, String message) {
		List<String> parsed = CMParms.parse(message);
		if (parsed.size() != 3) {
			mob.tell("Incorrect usage, try: TELNET [HOST] [PORT]");
			return false;
		}
		try {
			shutdown();
			synchronized (this) {
				sock = new Socket(parsed.get(1), CMath.s_int(parsed.get(2)));
				sock.setSoTimeout(1);
				reader = new BufferedInputStream(sock.getInputStream());
				writer = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
			}
			currentScreen = "Connected to " + parsed.get(1) + " at port "
					+ parsed.get(2);
			fillWithData();
			return true;
		} catch (Exception e) {
			mob.tell("Telnet software failure: " + e.getMessage());
			return false;
		}
	}

	public boolean checkDeactivate(MOB mob, String message) {
		shutdown();
		return true;
	}

	public boolean checkTyping(MOB mob, String message) {
		synchronized (this) {
			if (sock != null)
				return true;
		}
		mob.tell("Software failure.");
		super.forceUpMenu();
		super.forceNewMenuRead();
		return true;
	}

	public boolean checkPowerCurrent(int value) {
		nextPowerCycleTmr = System.currentTimeMillis() + (8 * 1000);
		return true;
	}

	public void fillWithData() {
		try {
			synchronized (this) {
				if (reader != null) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while (reader.available() > 0) {
						int c = reader.read();
						if (c == -1)
							throw new IOException("Received EOF");
						if (c != 0)
							bout.write(c);
					}
					if (bout.size() > 0)
						super.addScreenMessage(new String(bout.toByteArray(),
								"UTF-8"));

				}
			}
		} catch (java.net.SocketTimeoutException se) {

		} catch (Exception e) {
			super.addScreenMessage("*** Telnet disconnected: " + e.getMessage()
					+ " ***");
			super.forceNewMessageScan();
			shutdown();
			super.forceUpMenu();
			super.forceNewMenuRead();
		}
	}

	public void onTyping(MOB mob, String message) {
		synchronized (this) {
			if (writer != null) {
				try {
					writer.write(message + "\n\r");
					writer.flush();
				} catch (IOException e) {
					super.addScreenMessage("*** Telnet disconnected: "
							+ e.getMessage() + " ***");
					super.forceNewMessageScan();
					shutdown();
					super.forceUpMenu();
					super.forceNewMenuRead();
				}
			}
		}
	}

	public void onPowerCurrent(int value) {
		if (value > 0)
			fillWithData();
		if ((container() instanceof Electronics.Computer)
				&& (((Electronics.Computer) container()).getCurrentReaders()
						.size() == 0)) {
			this.shutdown();
		} else if (System.currentTimeMillis() > nextPowerCycleTmr) {
			this.shutdown();
		}
	}
}
