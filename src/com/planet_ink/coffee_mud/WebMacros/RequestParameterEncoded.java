package com.planet_ink.coffee_mud.WebMacros;

import java.net.URLEncoder;

import com.planet_ink.coffee_mud.core.Log;
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
public class RequestParameterEncoded extends RequestParameter {
	public String name() {
		return "RequestParameterEncoded";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {

		String str = super.runMacro(httpReq, parm);
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (java.io.UnsupportedEncodingException ex) {
			Log.errOut(name(), "Wrong Encoding");
		}
		return str;
	}
}