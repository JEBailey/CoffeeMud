package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

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
public class BaseCharClassName extends StdWebMacro {
	public String name() {
		return "BaseCharClassName";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		String last = httpReq.getUrlParameter("BASECLASS");
		if (last == null)
			return " @break@";
		if (last.length() > 0) {
			java.util.Map<String, String> parms = parseParms(parm);
			CharClass C = CMClass.getCharClass(last);
			if (C != null) {
				if (parms.containsKey("PLURAL"))
					return clearWebMacros(CMLib.english().makePlural(C.name()));
				else
					return clearWebMacros(C.name());
			}
			for (Enumeration e = CMClass.charClasses(); e.hasMoreElements();) {
				C = (CharClass) e.nextElement();
				if (C.baseClass().equalsIgnoreCase(last))
					if (parms.containsKey("PLURAL"))
						return clearWebMacros(CMLib.english().makePlural(
								C.baseClass()));
					else
						return clearWebMacros(C.baseClass());
			}
		}
		return "";
	}
}
