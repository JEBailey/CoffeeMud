package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.exceptions.HTTPServerException;
import com.planet_ink.miniweb.interfaces.HTTPRequest;
import com.planet_ink.miniweb.interfaces.SimpleServletResponse;

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
public class FileData extends StdWebMacro {
	public String name() {
		return "FileData";
	}

	public boolean isAWebPath() {
		return true;
	}

	public boolean preferBinary() {
		return true;
	}

	public void setServletResponse(SimpleServletResponse response,
			final String filename) {
		String file = filename;
		if (file == null)
			file = "FileData";
		int x = file.lastIndexOf('/');
		if ((x >= 0) && (x < file.length() - 1))
			file = file.substring(x + 1);
		super.setServletResponse(response, file);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ file);
	}

	public String getFilename(HTTPRequest httpReq, String filename) {
		String path = httpReq.getUrlParameter("PATH");
		if (path == null)
			return filename;
		String file = httpReq.getUrlParameter("FILE");
		if (file == null)
			return filename;
		return path + "/" + file;
	}

	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		String filename = getFilename(httpReq, "");
		if (filename.length() == 0)
			return null;
		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if (M == null)
			return null;
		CMFile F = new CMFile(filename, M);
		if ((!F.exists()) || (!F.canRead()))
			return null;
		return F.raw();
	}

	public String runMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		return "[Unimplemented string method!]";
	}
}
