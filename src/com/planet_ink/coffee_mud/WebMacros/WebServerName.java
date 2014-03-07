package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.miniweb.interfaces.HTTPRequest;
import com.planet_ink.miniweb.util.MWThread;

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
public class WebServerName extends StdWebMacro {
	public String name() {
		return "WebServerName";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (Thread.currentThread() instanceof MWThread) {
			String fullThreadName = ((MWThread) Thread.currentThread())
					.getName();
			int x = fullThreadName.lastIndexOf('#');
			if (x > 0)
				return fullThreadName.substring(0, x);
		}
		return "MiniWebServer";
	}

}