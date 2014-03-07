package com.planet_ink.coffee_mud.WebMacros;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.planet_ink.coffee_mud.WebMacros.interfaces.WebMacro;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.PairSVector;
import com.planet_ink.coffee_mud.core.collections.XHashtable;
import com.planet_ink.coffee_mud.core.exceptions.HTTPServerException;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.miniweb.http.HTTPException;
import com.planet_ink.miniweb.http.HTTPMethod;
import com.planet_ink.miniweb.http.MIMEType;
import com.planet_ink.miniweb.http.MultiPartData;
import com.planet_ink.miniweb.interfaces.DataBuffers;
import com.planet_ink.miniweb.interfaces.HTTPRequest;
import com.planet_ink.miniweb.interfaces.SimpleServletResponse;
import com.planet_ink.miniweb.util.MWThread;
import com.planet_ink.miniweb.util.MiniWebConfig;

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
public class StdWebMacro implements WebMacro {
	public String ID() {
		return name();
	}

	public String name() {
		return "UNKNOWN";
	}

	public boolean isAWebPath() {
		return false;
	}

	public boolean preferBinary() {
		return false;
	}

	public boolean isAdminMacro() {
		return false;
	}

	public CMObject newInstance() {
		return this;
	}

	public void initializeClass() {
	}

	public CMObject copyOf() {
		return this;
	}

	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		return runMacro(httpReq, parm).getBytes();
	}

	public String runMacro(HTTPRequest httpReq, String parm)
			throws HTTPServerException {
		return "[Unimplemented macro!]";
	}

	public int compareTo(CMObject o) {
		return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
	}

	public String getFilename(HTTPRequest httpReq, String filename) {
		return filename;
	}

	public void setServletResponse(SimpleServletResponse response,
			final String filename) {
		response.setHeader("Content-Type", MIMEType.getMIMEType(filename)
				.getType());
	}

	protected StringBuffer colorwebifyOnly(StringBuffer s) {
		if (s == null)
			return null;
		int i = 0;
		String[] lookup = CMLib.color().standardHTMLlookups();
		while (i < s.length()) {
			if (s.charAt(i) == '^') {
				if (i < (s.length() - 1)) {
					final char c = s.charAt(i + 1);
					// TODO: handle ~ and # here?
					String code = lookup[c];
					if (code != null) {
						s.delete(i, i + 2);
						if (code.startsWith("<")) {
							s.insert(i, code + ">");
							i += code.length();
						} else {
							s.insert(i, code);
							i += code.length() - 1;
						}
					} else if (c == '?') {
						s.delete(i, i + 2);
						s.insert(i, "</FONT>");
						i += 7;
					}
				}
			}
			i++;
		}
		return s;
	}

	protected StringBuffer webify(StringBuffer s) {
		if (s == null)
			return null;
		int i = 0;
		while (i < s.length()) {
			switch (s.charAt(i)) {
			case '\n':
			case '\r':
				if ((i < s.length() - 1)
						&& (s.charAt(i + 1) != s.charAt(i))
						&& ((s.charAt(i + 1) == '\r') || (s.charAt(i + 1) == '\n'))) {
					s.delete(i, i + 2);
					s.insert(i, "<BR>");
					i += 3;
				} else {
					s.delete(i, i + 1);
					s.insert(i, "<BR>");
					i += 3;
				}
				break;
			case ' ':
				s.setCharAt(i, '&');
				s.insert(i + 1, "nbsp;");
				i += 5;
				break;
			case '>':
				s.setCharAt(i, '&');
				s.insert(i + 1, "gt;");
				i += 3;
				break;
			case '<':
				s.setCharAt(i, '&');
				s.insert(i + 1, "lt;");
				i += 3;
				break;
			}
			i++;
		}
		s = colorwebifyOnly(s);
		return s;
	}

	protected String clearWebMacros(String s) {
		return CMLib.webMacroFilter().clearWebMacros(s);
	}

	protected String clearWebMacros(StringBuffer s) {
		return CMLib.webMacroFilter().clearWebMacros(s);
	}

	protected StringBuilder helpHelp(StringBuilder s) {
		return helpHelp(s, 70);
	}

	protected StringBuilder helpHelp(StringBuilder s, int limit) {
		if (s != null) {
			String[] lookup = CMLib.color().standardHTMLlookups();
			s = new StringBuilder(s.toString());
			int x = s.toString().indexOf("\n\r");
			while (x >= 0) {
				s.replace(x, x + 2, "<BR>");
				x = s.toString().indexOf("\n\r");
			}
			x = s.toString().indexOf("\r\n");
			while (x >= 0) {
				s.replace(x, x + 2, "<BR>");
				x = s.toString().indexOf("\r\n");
			}

			int count = 0;
			x = 0;
			int lastSpace = 0;
			// TODO: limit should adjust or lastspace should -- something is
			// wrong RIGHT HERE!
			while ((x >= 0) && (x < s.length())) {
				count++;
				switch (s.charAt(x)) {
				case ' ':
					lastSpace = x;
					break;
				case '<':
					if ((x <= s.length() - 4)
							&& (s.substring(x, x + 4).equalsIgnoreCase("<BR>"))) {
						count = 0;
						x = x + 3;
						lastSpace = x + 4;
					} else {
						s.setCharAt(x, '&');
						s.insert(x + 1, "lt;");
						x += 3;
					}
					break;
				case '-':
					if ((x > 4) && (s.charAt(x - 1) == '-')
							&& (s.charAt(x - 2) == '-')
							&& (s.charAt(x - 3) == '-')) {
						count = 0;
						lastSpace = x;
					}
					break;
				case '!':
					if ((x > 4) && (s.charAt(x - 1) == ' ')
							&& (s.charAt(x - 2) == ' ')
							&& (s.charAt(x - 3) == ' ')) {
						count = 0;
						lastSpace = x;
					}
					break;
				case '^':
					if (x < (s.length() - 1)) {
						char c = s.charAt(x + 1);
						String code = lookup[c];
						if (code != null) {
							s.delete(x, x + 2);
							if (code.startsWith("<")) {
								s.insert(x, code + ">");
								x += code.length();
							} else {
								s.insert(x, code);
								x += code.length() - 1;
							}
						}
						count--;
					}
					break;
				}
				if (count == limit) {
					// int brx=s.indexOf("<BR>",lastSpace);
					// if((brx<0)||(brx>lastSpace+12))
					s.replace(lastSpace, lastSpace + 1, "<BR>");
					lastSpace = lastSpace + 4;
					x = lastSpace;
					count = 0;
				} else
					x++;
			}
			return s;
		}
		return new StringBuilder("");
	}

	protected PairSVector<String, String> parseOrderedParms(String parm) {
		PairSVector<String, String> requestParms = new PairSVector<String, String>();
		if ((parm != null) && (parm.length() > 0)) {
			int lastDex = 0;
			CharSequence varSeq = null;
			for (int i = 0; i < parm.length(); i++) {
				switch (parm.charAt(i)) {
				case '&': {
					if (varSeq == null)
						requestParms.add(parm.substring(lastDex, i)
								.toUpperCase().trim(),
								parm.substring(lastDex, i).trim());
					else
						requestParms.add(
								varSeq.toString().trim().toUpperCase(), parm
										.substring(lastDex, i).trim());
					lastDex = i + 1;
					varSeq = null;
					break;
				}
				case '=': {
					if (varSeq == null) {
						varSeq = parm.subSequence(lastDex, i);
						lastDex = i + 1;
					}
					break;
				}
				}
			}
			final int i = parm.length();
			if (varSeq == null)
				requestParms.add(parm.substring(lastDex, i).trim()
						.toUpperCase(), parm.substring(lastDex, i).trim());
			else
				requestParms.add(varSeq.toString().trim().toUpperCase(), parm
						.substring(lastDex, i).trim());
		}
		return requestParms;
	}

	protected String htmlIncomingFilter(String buf) {
		return htmlIncomingFilter(new StringBuffer(buf)).toString();
	}

	protected StringBuffer htmlIncomingFilter(StringBuffer buf) {
		int loop = 0;

		while (buf.length() > loop) {
			if ((buf.charAt(loop) == '&') && (loop < buf.length() - 3)) {
				int endloop = loop + 1;
				while ((endloop < buf.length()) && (endloop < loop + 10)
						&& (buf.charAt(endloop) != ';'))
					endloop++;
				if (endloop < buf.length()) {
					String s = buf.substring(loop, endloop + 1);
					if (s.equalsIgnoreCase("&gt;")) {
						buf.setCharAt(loop, '>');
						buf.delete(loop + 1, endloop + 1);
					} else if (s.equalsIgnoreCase("&lt;")) {
						buf.setCharAt(loop, '<');
						buf.delete(loop + 1, endloop + 1);
					} else if (s.equalsIgnoreCase("&amp;")) {
						buf.setCharAt(loop, '&');
						buf.delete(loop + 1, endloop + 1);
					} else if (s.equalsIgnoreCase("&quot;")) {
						buf.setCharAt(loop, '\"');
						buf.delete(loop + 1, endloop + 1);
					}
				}
			}
			loop++;
		}
		return buf;
	}

	protected String htmlOutgoingFilter(String buf) {
		return htmlOutgoingFilter(new StringBuffer(buf)).toString();
	}

	protected StringBuffer htmlOutgoingFilter(StringBuffer buf) {
		int loop = 0;

		while (buf.length() > loop) {
			switch (buf.charAt(loop)) {
			case '>':
				buf.delete(loop, loop + 1);
				buf.insert(loop, "&gt;".toCharArray());
				loop += 3;
				break;
			case '"':
				buf.delete(loop, loop + 1);
				buf.insert(loop, "&quot;".toCharArray());
				loop += 5;
				break;
			case '&':
				if ((loop + 3 >= buf.length())
						|| ((!buf.substring(loop, loop + 3).equalsIgnoreCase(
								"lt;"))
								&& (!buf.substring(loop, loop + 3)
										.equalsIgnoreCase("amp;"))
								&& (!buf.substring(loop, loop + 3)
										.equalsIgnoreCase("quot;")) && (!buf
								.substring(loop, loop + 3).equalsIgnoreCase(
										"gt;")))) {
					buf.delete(loop, loop + 1);
					buf.insert(loop, "&amp;".toCharArray());
					loop += 4;
				} else
					loop++;
				break;
			case '<':
				buf.delete(loop, loop + 1);
				buf.insert(loop, "&lt;".toCharArray());
				loop += 3;
				break;
			default:
				loop++;
			}
		}
		return buf;
	}

	protected byte[] getHTTPFileData(final HTTPRequest httpReq,
			final String file) throws HTTPException {
		if (Thread.currentThread() instanceof MWThread) {
			MiniWebConfig config = ((MWThread) Thread.currentThread())
					.getConfig();
			HTTPRequest newReq = new HTTPRequest() {
				final Hashtable<String, String> params = new XHashtable<String, String>(
						httpReq.getUrlParametersCopy());

				@Override
				public String getHost() {
					return httpReq.getHost();
				}

				@Override
				public String getUrlPath() {
					return file;
				}

				@Override
				public String getFullRequest() {
					return httpReq.getMethod().name() + " " + getUrlPath();
				}

				@Override
				public String getUrlParameter(String name) {
					return params.get(name.toLowerCase());
				}

				@Override
				public Map<String, String> getUrlParametersCopy() {
					return new XHashtable<String, String>(params);
				}

				@Override
				public boolean isUrlParameter(String name) {
					return params.containsKey(name.toLowerCase());
				}

				@Override
				public Set<String> getUrlParameters() {
					return params.keySet();
				}

				@Override
				public HTTPMethod getMethod() {
					return httpReq.getMethod();
				}

				@Override
				public String getHeader(String name) {
					return httpReq.getHeader(name);
				}

				@Override
				public InetAddress getClientAddress() {
					return httpReq.getClientAddress();
				}

				@Override
				public int getClientPort() {
					return httpReq.getClientPort();
				}

				@Override
				public InputStream getBody() {
					return httpReq.getBody();
				}

				@Override
				public String getCookie(String name) {
					return httpReq.getCookie(name);
				}

				@Override
				public Set<String> getCookieNames() {
					return httpReq.getCookieNames();
				}

				@Override
				public List<MultiPartData> getMultiParts() {
					return httpReq.getMultiParts();
				}

				@Override
				public double getSpecialEncodingAcceptability(String type) {
					return httpReq.getSpecialEncodingAcceptability(type);
				}

				@Override
				public String getFullHost() {
					return httpReq.getFullHost();
				}

				@Override
				public List<int[]> getRangeAZ() {
					return httpReq.getRangeAZ();
				}

				@Override
				public void addFakeUrlParameter(String name, String value) {
					params.put(name.toUpperCase(), value);
				}

				@Override
				public void removeUrlParameter(String name) {
					params.remove(name.toUpperCase());
				}

				@Override
				public Map<String, Object> getRequestObjects() {
					return httpReq.getRequestObjects();
				}

				@Override
				public float getHttpVer() {
					return httpReq.getHttpVer();
				}
			};

			DataBuffers data = config.getFileGetter().getFileData(newReq);
			return data.flushToBuffer().array();
		}
		return new byte[0];
	}

	protected File grabFile(final HTTPRequest httpReq, String filename) {
		if (Thread.currentThread() instanceof MWThread) {
			filename = filename.replace(File.separatorChar, '/');
			if (!filename.startsWith("/"))
				filename = '/' + filename;
			final String file = filename;
			MiniWebConfig config = ((MWThread) Thread.currentThread())
					.getConfig();
			HTTPRequest newReq = new HTTPRequest() {
				public final Hashtable<String, String> params = new XHashtable<String, String>(
						httpReq.getUrlParametersCopy());

				@Override
				public String getHost() {
					return httpReq.getHost();
				}

				@Override
				public String getUrlPath() {
					return file;
				}

				@Override
				public String getFullRequest() {
					return httpReq.getMethod().name() + " " + getUrlPath();
				}

				@Override
				public String getUrlParameter(String name) {
					return params.get(name.toUpperCase());
				}

				@Override
				public boolean isUrlParameter(String name) {
					return params.containsKey(name.toUpperCase());
				}

				@Override
				public Map<String, String> getUrlParametersCopy() {
					return new XHashtable<String, String>(params);
				}

				@Override
				public Set<String> getUrlParameters() {
					return params.keySet();
				}

				@Override
				public HTTPMethod getMethod() {
					return httpReq.getMethod();
				}

				@Override
				public String getHeader(String name) {
					return httpReq.getHeader(name);
				}

				@Override
				public InetAddress getClientAddress() {
					return httpReq.getClientAddress();
				}

				@Override
				public int getClientPort() {
					return httpReq.getClientPort();
				}

				@Override
				public InputStream getBody() {
					return httpReq.getBody();
				}

				@Override
				public String getCookie(String name) {
					return httpReq.getCookie(name);
				}

				@Override
				public Set<String> getCookieNames() {
					return httpReq.getCookieNames();
				}

				@Override
				public List<MultiPartData> getMultiParts() {
					return httpReq.getMultiParts();
				}

				@Override
				public double getSpecialEncodingAcceptability(String type) {
					return httpReq.getSpecialEncodingAcceptability(type);
				}

				@Override
				public String getFullHost() {
					return httpReq.getFullHost();
				}

				@Override
				public List<int[]> getRangeAZ() {
					return httpReq.getRangeAZ();
				}

				@Override
				public void addFakeUrlParameter(String name, String value) {
					params.put(name.toUpperCase(), value);
				}

				@Override
				public void removeUrlParameter(String name) {
					params.remove(name.toUpperCase());
				}

				@Override
				public Map<String, Object> getRequestObjects() {
					return httpReq.getRequestObjects();
				}

				@Override
				public float getHttpVer() {
					return httpReq.getHttpVer();
				}
			};

			return config.getFileGetter().assembleFileRequest(newReq);
		}
		return null;
	}

	protected java.util.Map<String, String> parseParms(String parm) {
		final Map<String, String> requestParms = new Hashtable<String, String>();
		final PairSVector<String, String> requestParsed = parseOrderedParms(parm);
		for (final Pair<String, String> P : requestParsed){
			requestParms.put(P.first, P.second);
		}
		return requestParms;
	}
}