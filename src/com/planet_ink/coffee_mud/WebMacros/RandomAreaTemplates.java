package com.planet_ink.coffee_mud.WebMacros;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.Resources;
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
@SuppressWarnings("unchecked")
public class RandomAreaTemplates extends StdWebMacro {
	public String name() {
		return "RandomAreaTemplates";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		MOB M = Authenticate.getAuthenticatedMob(httpReq);
		if (M == null)
			return "[authentication error]";
		try {
			String last = httpReq.getUrlParameter("RTEMPLATE");
			if (parms.containsKey("NEXT")) {
				if (parms.containsKey("RESET")) {
					if (last != null)
						httpReq.removeUrlParameter("RTEMPLATE");
					return "";
				}
				if (last == null)
					return " @break@";
				List<String> fileList = (List<String>) httpReq
						.getRequestObjects().get("RANDOMAREATEMPLATESLIST");
				if (fileList == null) {
					fileList = new ArrayList<String>();
					List<String> templateDirs = new LinkedList<String>();
					templateDirs.add("");
					while (templateDirs.size() > 0) {
						String templateDirPath = templateDirs.remove(0);
						CMFile templateDir = new CMFile(
								Resources.buildResourcePath("randareas/"
										+ templateDirPath), M);
						for (CMFile file : templateDir.listFiles()) {
							if (file.isDirectory() && file.canRead())
								templateDirs.add(templateDirPath
										+ file.getName() + "/");
							else
								fileList.add(templateDirPath + file.getName());
						}
					}
					httpReq.getRequestObjects().put("RANDOMAREATEMPLATESLIST",
							fileList);
				}
				String lastID = "";
				for (Iterator<String> r = fileList.iterator(); r.hasNext();) {
					String RC = r.next();
					if ((last.length() > 0) && (last.equals(lastID))
							&& (!RC.equals(lastID))) {
						httpReq.addFakeUrlParameter("RTEMPLATE", RC);
						return "";
					}
					lastID = RC;
				}
				httpReq.addFakeUrlParameter("RTEMPLATE", "");
				if (parms.containsKey("EMPTYOK"))
					return "<!--EMPTY-->";
				return " @break@";
			}
		} catch (Exception e) {
			return "[an error occurred performing the last operation]";
		}
		return "";
	}
}
