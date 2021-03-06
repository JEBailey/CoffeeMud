package com.planet_ink.coffee_mud.core.intermud.cm1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

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
public class CM1Client {
	private String host;
	private int port;
	private Socket sock = null;
	private BufferedReader br = null;
	private BufferedWriter bw = null;

	public CM1Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public final static int s_int(final String INT) {
		try {
			return Integer.parseInt(INT);
		} catch (Exception e) {
			return 0;
		}
	}

	public synchronized List<String> transactMessages(String command) {
		LinkedList<String> list = new LinkedList<String>();
		if (command.trim().length() == 0)
			return list;
		try {
			if ((command.indexOf('\n') < 0) && (command.indexOf('\r') < 0))
				bw.write(command + "\n");
			else {
				bw.write("BLOCK\n");
				String s = br.readLine();
				if (!s.startsWith("[OK /BLOCK:"))
					return list;
				String eob = s.substring(11, s.length() - 1);
				bw.write(command + eob);
			}
			String s = br.readLine();
			if (s == null)
				return list;
			if (s.startsWith("[OK ")) {
				s = s.substring(4, s.length() - 1);
				if (s.startsWith("/MESSAGES:")) {
					int num = s_int(s.substring(9));
					for (int i = 0; i < num; i++)
						list.add(br.readLine());
				} else
					list.add(s);
			} else if (s.startsWith("[FAIL "))
				list.add(s);
			else if (s.startsWith("[BLOCK ")) {
				String eob = s.substring(7, s.length() - 1);
				StringBuilder str = new StringBuilder("");
				while (!str.toString().endsWith(eob))
					str.append((char) br.read());
				list.add(str.toString().substring(0,
						str.length() - eob.length()));
			}
		} catch (Exception e) {

		}
		return list;
	}

	public synchronized String transact(String command) {
		List<String> list = transactMessages(command);
		if (list.size() == 0)
			return "";
		return list.get(0);
	}

	public synchronized boolean connect() {
		try {
			sock = new Socket(host, port);
			br = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
			long timeout = System.currentTimeMillis() + (3 * 1000);
			String s = br.readLine();
			while ((System.currentTimeMillis() < timeout)
					&& (!s.startsWith("CONNECTED TO")))
				s = br.readLine();
			if (System.currentTimeMillis() > timeout)
				return true;
			bw.close();
			bw = null;
			br.close();
			br = null;
			sock.close();
			sock = null;
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
