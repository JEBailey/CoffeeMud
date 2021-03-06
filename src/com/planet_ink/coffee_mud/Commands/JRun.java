package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;

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
@SuppressWarnings("rawtypes")
public class JRun extends StdCommand {
	public JRun() {
	}

	private final String[] access = { "JRUN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("jrun filename1 parm1 parm2 ...");
			return false;
		}
		commands.removeElementAt(0);

		String fn = (String) commands.elementAt(0);
		StringBuffer ft = new CMFile(fn, mob, CMFile.FLAG_LOGERRORS).text();
		if ((ft == null) || (ft.length() == 0)) {
			mob.tell("File '" + fn + "' could not be found.");
			return false;
		}
		commands.removeElementAt(0);
		Context cx = Context.enter();
		try {
			JScriptWindow scope = new JScriptWindow(mob, commands);
			cx.initStandardObjects(scope);
			scope.defineFunctionProperties(JScriptWindow.functions,
					JScriptWindow.class, ScriptableObject.DONTENUM);
			cx.evaluateString(scope, ft.toString(), "<cmd>", 1, null);
		} catch (Exception e) {
			mob.tell("JavaScript error: " + e.getMessage());
		}
		Context.exit();
		return false;
	}

	protected static class JScriptWindow extends ScriptableObject {
		public String getClassName() {
			return "JScriptWindow";
		}

		static final long serialVersionUID = 45;
		MOB s = null;
		Vector v = null;

		public MOB mob() {
			return s;
		}

		public int numParms() {
			return (v == null) ? 0 : v.size();
		}

		public String getParm(int i) {
			if (v == null)
				return "";
			if ((i < 0) || (i >= v.size()))
				return "";
			return (String) v.elementAt(i);
		}

		public static String[] functions = { "mob", "numParms", "getParm",
				"getParms", "toJavaString" };

		public String getParms() {
			return (v == null) ? "" : CMParms.combineWithQuotes(v, 0);
		}

		public JScriptWindow(MOB executor, Vector parms) {
			s = executor;
			v = parms;
		}

		public String toJavaString(Object O) {
			return Context.toString(O);
		}
	}

	public boolean canBeOrdered() {
		return false;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowed(mob, mob.location(),
				CMSecurity.SecFlag.JSCRIPTS);
	}

	public int compareTo(CMObject o) {
		return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
	}

}
