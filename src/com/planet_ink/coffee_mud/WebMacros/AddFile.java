package com.planet_ink.coffee_mud.WebMacros;

import java.util.Vector;

import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.miniweb.http.HTTPException;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AddFile extends StdWebMacro {
	public String name() {
		return "AddFile";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		if ((parms == null) || (parms.size() == 0))
			return "";
		StringBuffer buf = new StringBuffer("");
		boolean webify = false;
		Vector V = new Vector();
		V.addAll(parms.values());
		for (int v = V.size() - 1; v >= 0; v--) {
			String file = (String) V.elementAt(v);
			if (file.length() > 0) {
				try {
					if (file.equalsIgnoreCase("webify"))
						webify = true;
					else if (webify)
						buf.append(webify(new StringBuffer(new String(
								getHTTPFileData(httpReq, file)))));
					else
						buf.append(new String(getHTTPFileData(httpReq, file)));
				} catch (HTTPException e) {
					Log.warnOut("Failed " + name() + " " + file);
				}
			}
		}
		return buf.toString();
	}
}
