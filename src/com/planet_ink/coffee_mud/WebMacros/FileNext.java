package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.collections.XVector;
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
public class FileNext extends StdWebMacro {
	public String name() {
		return "FileNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String trimSlash(String path) {
		path = path.trim();
		while (path.startsWith("/"))
			path = path.substring(1);
		while (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		return path;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String path = httpReq.getUrlParameter("PATH");
		if (path == null)
			path = "";
		String last = httpReq.getUrlParameter("FILE");
		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if (M == null)
			return "[authentication error]";
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("FILE");
			return "";
		}
		String fileKey = "CMFSFILE_" + trimSlash(path);
		String pathKey = "DIRECTORYFILES_" + trimSlash(path);
		CMFile directory = (CMFile) httpReq.getRequestObjects().get(fileKey);
		if (directory == null) {
			directory = new CMFile(path, M);
			httpReq.getRequestObjects().put(fileKey, directory);
		}
		XVector fileList = new XVector();
		if ((directory.canRead()) && (directory.isDirectory())) {
			httpReq.addFakeUrlParameter("PATH", directory.getVFSPathAndName());
			CMFile[] dirs = (CMFile[]) httpReq.getRequestObjects().get(pathKey);
			if (dirs == null) {
				dirs = CMFile.getFileList(path, M, false, true);
				httpReq.getRequestObjects().put(pathKey, dirs);
				for (CMFile file : dirs) {
					String filepath = path.endsWith("/") ? path
							+ file.getName() : path + "/" + file.getName();
					httpReq.getRequestObjects().put(
							"CMFSFILE_" + trimSlash(filepath), file);
				}
			}
			for (int d = 0; d < dirs.length; d++)
				fileList.addElement(dirs[d].getName());
		}
		fileList.sort();
		String lastID = "";
		for (int q = 0; q < fileList.size(); q++) {
			String name = (String) fileList.elementAt(q);
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!name
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("FILE", name);
				return "";
			}
			lastID = name;
		}
		httpReq.addFakeUrlParameter("FILE", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}