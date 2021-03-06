package com.planet_ink.coffee_mud.WebMacros;

import java.io.File;
import java.util.Vector;

import com.planet_ink.coffee_mud.core.CMLib;
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
public class AddRandomFileFromDir extends StdWebMacro {
	public String name() {
		return "AddRandomFileFromDir";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		if ((parms == null) || (parms.size() == 0))
			return "";
		StringBuffer buf = new StringBuffer("");
		Vector fileList = new Vector();
		boolean LINKONLY = false;
		for (String val : parms.values())
			if (val.equalsIgnoreCase("LINKONLY"))
				LINKONLY = true;
		for (String filePath : parms.values()) {
			if (filePath.equalsIgnoreCase("LINKONLY"))
				continue;
			File directory = grabFile(httpReq, filePath);
			if ((!filePath.endsWith("/")) && (!filePath.endsWith("/")))
				filePath += "/";
			if ((directory != null) && (directory.canRead())
					&& (directory.isDirectory())) {
				String[] list = directory.list();
				for (int l = 0; l < list.length; l++)
					fileList.addElement(filePath + list[l]);
			} else
				Log.sysOut("AddRFDir", "Directory error: " + filePath);
		}
		if (fileList.size() == 0)
			return buf.toString();

		try {
			if (LINKONLY)
				buf.append((String) fileList.elementAt(CMLib.dice().roll(1,
						fileList.size(), -1)));
			else
				buf.append(new String(getHTTPFileData(
						httpReq,
						(String) fileList.elementAt(CMLib.dice().roll(1,
								fileList.size(), -1)))));
		} catch (HTTPException e) {
			Log.warnOut("Failed "
					+ name()
					+ " "
					+ (String) fileList.elementAt(CMLib.dice().roll(1,
							fileList.size(), -1)));
		}
		return buf.toString();
	}
}
