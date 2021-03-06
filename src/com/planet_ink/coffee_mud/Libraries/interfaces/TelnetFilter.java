package com.planet_ink.coffee_mud.Libraries.interfaces;

import java.util.Hashtable;

import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public interface TelnetFilter extends CMLibrary {
	public final static String hexStr = "0123456789ABCDEF";
	public final static int HISHER = 0;
	public final static int HIMHER = 1;
	public final static int NAME = 2;
	public final static int NAMESELF = 3;
	public final static int HESHE = 4;
	public final static int ISARE = 5;
	public final static int HASHAVE = 6;
	public final static int YOUPOSS = 7;
	public final static int HIMHERSELF = 8;
	public final static int HISHERSELF = 9;
	public final static int SIRMADAM = 10;
	public final static int ISARE2 = 11;
	public final static int NAMENOART = 12;
	public final static int ACCOUNTNAME = 13;
	public final static String[] FILTER_DESCS = { "-HIS-HER", "-HIM-HER",
			"-NAME", "-NAMESELF", "-HE-SHE", "-IS-ARE", "-HAS-HAVE",
			"-YOUPOSS", "-HIM-HERSELF", "-HIS-HERSELF", "-SIRMADAM", "IS-ARE",
			"-NAMENOART", "-ACCOUNTNAME" };

	public Hashtable<Object, Integer> getTagTable();

	public String simpleOutFilter(String msg);

	// no word-wrapping, text filtering or ('\','n') -> '\n' translations
	// (it's not a member of the interface either so probably shouldn't be
	// public)
	public String colorOnlyFilter(String msg, Session S);

	public String[] wrapOnlyFilter(String msg, int wrap);

	public String getLastWord(StringBuffer buf, int lastSp, int lastSpace);

	public String fullOutFilter(Session S, MOB mob, Physical source,
			Environmental target, Environmental tool, String msg,
			boolean wrapOnly);

	public String simpleInFilter(StringBuilder input, boolean permitMXPTags);

	public String simpleInFilter(StringBuilder input);

	public String fullInFilter(String input);

	public String safetyFilter(String s);
}
