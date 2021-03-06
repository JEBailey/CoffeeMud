package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class AreaXML extends StdWebMacro {
	public String name() {
		return "AreaXML";
	}

	public boolean isAWebPath() {
		return true;
	}

	public boolean preferBinary() {
		return true;
	}

	public void setServletResponse(SimpleServletResponse response,
			final String filename) {
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename);
		response.setHeader("Content-Type", "application/cmare");
	}

	public String getFilename(HTTPRequest httpReq, String filename) {
		MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if (mob == null)
			return "area.xml";
		Area pickedA = getLoggedArea(httpReq, mob);
		if (pickedA == null)
			return "area.xml";
		String fileName = "";
		if (pickedA.getArchivePath().length() > 0)
			fileName = pickedA.getArchivePath();
		else
			fileName = pickedA.Name();
		if (fileName.indexOf('.') < 0)
			fileName = fileName + ".cmare";
		return fileName;
	}

	protected Area getLoggedArea(HTTPRequest httpReq, MOB mob) {
		String AREA = httpReq.getUrlParameter("AREA");
		if (AREA == null)
			return null;
		if (AREA.length() == 0)
			return null;
		Area A = CMLib.map().getArea(AREA);
		if (A == null)
			return null;
		if (CMSecurity.isASysOp(mob) || A.amISubOp(mob.Name()))
			return A;
		return null;
	}

	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if (mob == null)
			return null;
		Area pickedA = getLoggedArea(httpReq, mob);
		if (pickedA == null)
			return null;
		Command C = CMClass.getCommand("Export");
		if (C == null)
			return null;
		Object resultO = null;
		try {
			resultO = C.executeInternal(mob, 0, "AREA", "DATA", "MEMORY",
					Integer.valueOf(4), null, pickedA, mob.location());
		} catch (Exception e) {
			return null;
		}
		if (!(resultO instanceof String))
			return null;
		return ((String) resultO).getBytes();
	}

	public String runMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		return "[Unimplemented string method!]";
	}
}
