package com.planet_ink.coffee_mud.Libraries.interfaces;
import java.util.Map;

import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public interface ProtocolLibrary extends CMLibrary
{
	public String msp(final String soundName, final int priority);
	
	public String[] mxpImagePath(String fileName);
	public String mxpImage(final Environmental E, final String parms);
	public String mxpImage(final Environmental E, final String parms, final String pre, final String post);
	public String getDefaultMXPImage(final Object O);
	
	public byte[] processMsdp(final Session session, final char[] data, final int dataSize, final Map<Object,Object> reportables);
	public byte[] pingMsdp(final Session session, final Map<Object,Object> reportables);

	public byte[] processGmcp(final Session session, final char[] data, final int dataSize, final Map<String,Double> supportables);
	public byte[] buildGmcpResponse(String json);
	public byte[] pingGmcp(final Session session, final Map<String,Long> reporteds, final Map<String,Double> supportables);
}
