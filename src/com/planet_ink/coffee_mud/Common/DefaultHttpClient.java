package com.planet_ink.coffee_mud.Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.HttpClient;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.collections.CaselessTreeMap;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

/* 
 Copyright 2013-2014 Bo Zimmerman

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

public class DefaultHttpClient implements HttpClient, Cloneable {
	public String ID() {
		return "DefaultHttpClient";
	}

	public String name() {
		return ID();
	}

	private static enum HState {
		PREHEAD, INHEAD, INBODY, PRECHUNK, INCHUNK, POSTINCHUNK, POSTCHUNK
	}

	private volatile long tickStatus = Tickable.STATUS_NOT;
	protected Map<String, String> reqHeaders = new CaselessTreeMap<String>();
	protected Map<String, List<String>> respHeaders = new CaselessTreeMap<List<String>>();
	protected Socket sock = null;
	protected OutputStream out = null;
	protected InputStream in = null;
	protected Method meth = Method.GET;
	protected int connectTimeout = 10000;
	protected int readTimeout = 10000;
	protected int maxReadBytes = 0;
	protected byte[] outBody = null;
	protected Integer respStatus = null;

	public CMObject newInstance() {
		try {
			return getClass().newInstance();
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public void initializeClass() {
	}

	public HttpClient header(String key, String value) {
		reqHeaders.put(key, value);
		return this;
	}

	public HttpClient method(Method meth) {
		if (meth != null) {
			this.meth = meth;
		}
		return this;
	}

	public HttpClient body(String body) {
		if (body != null) {
			this.outBody = body.getBytes();
		}
		return this;
	}

	public HttpClient body(byte[] body) {
		if (body != null) {
			this.outBody = body;
		}
		return this;
	}

	public HttpClient reset() {
		reqHeaders.clear();
		respHeaders.clear();
		respStatus = null;
		return this;
	}

	public HttpClient connectTimeout(int ms) {
		this.connectTimeout = ms;
		return this;
	}

	public HttpClient readTimeout(int ms) {
		this.readTimeout = ms;
		return this;
	}

	public HttpClient maxReadBytes(int bytes) {
		this.maxReadBytes = bytes;
		return this;
	}

	protected void conditionalHeader(String key, String value,
			List<String> clearSet) {
		if (!reqHeaders.containsKey(key)) {
			reqHeaders.put(key, value);
			clearSet.add(key);
		}
	}

	public int getResponseCode() {
		if (this.respStatus != null)
			return this.respStatus.intValue();
		return -1;
	}

	public Map<String, List<String>> getResponseHeaders() {
		return this.respHeaders;
	}

	public HttpClient doRequest(String url) throws IOException {
		respHeaders.clear();
		respStatus = null;
		outBody = null;
		if (url == null)
			throw new IOException("Bad url");
		final boolean ssl = url.toLowerCase().startsWith("https");
		if (ssl)
			throw new IOException("Unsupported: ssl");
		int x = url.indexOf("://");
		if (x >= 0)
			url = url.substring(x + 3);
		String host;
		String rest = "/";
		x = url.indexOf('/');
		if (x < 0)
			x = url.indexOf('?');
		if (x >= 0) {
			host = url.substring(0, x);
			rest = url.substring(x);
		} else
			host = url;
		int port = ssl ? 443 : 80;
		x = host.indexOf(':');
		if (x >= 0) {
			port = CMath.s_int(host.substring(x + 1), 80);
			host = host.substring(0, x);
		}
		List<String> onesToClear = new Vector<String>();
		conditionalHeader("Host", host, onesToClear);
		conditionalHeader("Connection", "Keep-Alive", onesToClear);
		conditionalHeader("Accept", "*/*", onesToClear);
		int len = (outBody != null) ? outBody.length : 0;
		conditionalHeader("Content-Length", "" + len, onesToClear);
		if (sock == null) {
			sock = new Socket();
			sock.connect(new InetSocketAddress(host, port), this.connectTimeout);
			in = sock.getInputStream();
			sock.setSoTimeout(10);
			out = sock.getOutputStream();
		}
		final IOException cleanException = new IOException(
				"Connection closed by remote host");
		try {
			while (in.read() != -1) {
			} /* clear the stream */
			throw cleanException;
		} catch (IOException e) {
			if (e == cleanException)
				throw e;
		}
		out.write((meth.toString() + " " + rest + " HTTP/1.1\n").getBytes());
		for (String key : reqHeaders.keySet())
			out.write((key + ": " + reqHeaders.get(key) + "\n").getBytes());
		for (String key : onesToClear)
			reqHeaders.remove(key);
		out.write("\n".getBytes());
		if (outBody != null)
			out.write(outBody);
		long nextReadTimeout = (this.readTimeout > 0) ? (System
				.currentTimeMillis() + this.readTimeout) : Long.MAX_VALUE;
		int lastC = -1;

		HState state = HState.PREHEAD;
		ByteArrayOutputStream bodyBuilder = new ByteArrayOutputStream();
		StringBuilder headBuilder = new StringBuilder();
		int c = 0;
		int maxBytes = this.maxReadBytes;
		int chunkSize = 0;
		while (c != -1) {
			try {
				lastC = c;
				c = in.read();
				if (this.readTimeout > 0)
					nextReadTimeout = (System.currentTimeMillis() + this.readTimeout);
			} catch (IOException e) {
				if (e instanceof java.net.SocketTimeoutException) {
					if (System.currentTimeMillis() > nextReadTimeout)
						throw e;
					continue;
				} else
					throw e;
			}
			switch (state) {
			case PREHEAD: {
				if ((c == '\n') && (lastC == '\r')) {
					state = HState.INHEAD;
					String s = headBuilder.toString();
					headBuilder.setLength(0);
					String[] parts = s.split(" ", 3);
					if (CMath.isInteger(parts[1]))
						respStatus = Integer.valueOf(CMath.s_int(parts[1]));
					else
						respStatus = Integer.valueOf(-1);
				} else if ((c != '\n') && (c != '\r'))
					headBuilder.append((char) c);
				break;
			}
			case INHEAD: {
				if ((c == '\n') && (lastC == '\r')) {
					if (headBuilder.length() == 0) {
						if (respHeaders.containsKey("Transfer-Encoding")
								&& (respHeaders.get("Transfer-Encoding")
										.contains("chunked")
										|| respHeaders.get("Transfer-Encoding")
												.contains("Chunked") || respHeaders
										.get("Transfer-Encoding").contains(
												"CHUNKED"))) {
							maxBytes = Integer.MAX_VALUE;
							state = HState.PRECHUNK;
						} else if (respHeaders.containsKey("Content-Length")) {
							List<String> l = respHeaders.get("Content-Length");
							for (String s : l)
								if (CMath.isInteger(s)) {
									int possMax = CMath.s_int(s);
									if ((maxBytes == 0) || (possMax < maxBytes))
										maxBytes = possMax;
								}
							state = HState.INBODY;
						} else {
							c = -1;
							break;
						}
					} else {
						String s = headBuilder.toString();
						x = s.indexOf(':');
						if (x > 0) {
							String key = s.substring(0, x).trim();
							String value = s.substring(x + 1).trim();
							List<String> list;
							if (respHeaders.containsKey(key))
								list = respHeaders.get(key);
							else {
								list = new ArrayList<String>();
								respHeaders.put(key, list);
							}
							list.add(value);
						}
					}
					headBuilder.setLength(0);
				} else if ((c != '\r') && (c != '\n'))
					headBuilder.append((char) c);
				break;
			}
			case INBODY: {
				bodyBuilder.write(c);
				if ((maxBytes == 0) || (bodyBuilder.size() >= maxBytes))
					c = -1;
				break;
			}
			case PRECHUNK: {
				if ((c == '\n') && (lastC == '\r')) {
					state = HState.INCHUNK;
					String szStr = headBuilder.toString().trim();
					x = szStr.indexOf(';');
					if (x >= 0)
						szStr = szStr.substring(0, x).trim();
					x = 0;
					while ((x < szStr.length()) && (szStr.charAt(x) == '0'))
						x++;
					if (x < szStr.length()) {
						chunkSize = Integer.parseInt(szStr.substring(x).trim(),
								16);
						if (chunkSize == 0)
							state = HState.POSTCHUNK;
					} else
						state = HState.POSTCHUNK;
					headBuilder.setLength(0);
				} else if ((c != '\r') && (c != '\n')) {
					headBuilder.append((char) c);
				}
				break;
			}
			case INCHUNK: {
				bodyBuilder.write(c);
				if ((--chunkSize) <= 0)
					state = HState.POSTINCHUNK;
				break;
			}
			case POSTINCHUNK: {
				if ((c == '\n') && (lastC == '\r'))
					state = HState.PRECHUNK;
				break;
			}
			case POSTCHUNK: {
				if ((c == '\n') && (lastC == '\r')) {
					if (headBuilder.length() == 0)
						c = -1;
					else {
						String s = headBuilder.toString();
						x = s.indexOf(':');
						if (x > 0) {
							String key = s.substring(0, x).trim();
							String value = s.substring(x + 1).trim();
							List<String> list;
							if (respHeaders.containsKey(key))
								list = respHeaders.get(key);
							else {
								list = new ArrayList<String>();
								respHeaders.put(key, list);
							}
							list.add(value);
						}
					}
					headBuilder.setLength(0);
				} else if ((c != '\r') && (c != '\n'))
					headBuilder.append((char) c);
				break;
			}
			}

		}
		this.outBody = bodyBuilder.toByteArray();
		return this;
	}

	public byte[] getRawUrl(final String urlStr, String cookieStr) {
		return getRawUrl(urlStr, cookieStr, 1024 * 1024 * 10, 10000);
	}

	public byte[] getRawUrl(final String urlStr) {
		return getRawUrl(urlStr, null, 1024 * 1024 * 10, 10000);
	}

	public byte[] getRawUrl(final String urlStr, final int maxLength,
			final int readTimeout) {
		return getRawUrl(urlStr, null, maxLength, readTimeout);
	}

	public int getResponseContentLength() {
		if (this.outBody != null)
			return this.outBody.length;
		return 0;
	}

	public InputStream getResponseBody() {
		if (this.outBody != null)
			return new ByteArrayInputStream(this.outBody);
		return new ByteArrayInputStream(new byte[0]);

	}

	public HttpClient doGet(String url) throws IOException {
		return this.method(Method.GET).doRequest(url);
	}

	public HttpClient doHead(String url) throws IOException {
		return this.method(Method.HEAD).doRequest(url);
	}

	public byte[] getRawUrl(final String urlStr, String cookieStr,
			final int maxLength, final int readTimeout) {
		HttpClient h = null;
		try {
			h = this.readTimeout(readTimeout).connectTimeout(readTimeout)
					.method(Method.GET);
			if ((cookieStr != null) && (cookieStr.length() > 0))
				h = h.header("Cookie", cookieStr);
			h.doRequest(urlStr);
			if (h.getResponseCode() == 302) {
				InputStream in = h.getResponseBody();
				int len = h.getResponseContentLength();
				if ((len > 0) && ((maxLength == 0) || (len <= maxLength))) {
					byte[] buffer = new byte[1024];
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((len = in.read(buffer)) != -1) {
						bout.write(buffer, 0, len);
					}
					return bout.toByteArray();
				}
			}

			if (h.getResponseCode() == 200) {
				InputStream in = h.getResponseBody();
				int len = h.getResponseContentLength();
				if ((len > 0) && ((maxLength == 0) || (len <= maxLength))) {
					byte[] buffer = new byte[1024];
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((len = in.read(buffer)) != -1) {
						bout.write(buffer, 0, len);
					}
					return bout.toByteArray();
				}
			}
		} catch (Exception e) {
			if (e.getMessage() == null)
				Log.errOut("HttpClient", e);
			else
				Log.errOut("HttpClient: " + e.getMessage() + "(" + urlStr + ")");
			return null;
		} finally {
			if (h != null)
				h.finished();
		}
		return null;
	}

	public void finished() {
		if (sock != null) {
			try {
				sock.shutdownInput();
				sock.shutdownOutput();
				sock.close();
			} catch (Exception e) {
			} finally {
				sock = null;
				in = null;
				out = null;
			}
		}
	}

	public Map<String, List<String>> getHeaders(final String urlStr) {
		HttpClient h = null;
		try {
			h = this.readTimeout(3000).connectTimeout(3000).method(Method.GET);
			h.doRequest(urlStr);
			return h.getResponseHeaders();
		} catch (Exception e) {
			if (e.getMessage() == null)
				Log.errOut("HttpClient", e);
			else
				Log.errOut("HttpClient: " + e.getMessage() + "(" + urlStr + ")");
			return null;
		} finally {
			if (h != null)
				h.finished();
		}
	}

	@Override
	public long getTickStatus() {
		return tickStatus;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID) {
		return false;
	}

	@Override
	public CMObject copyOf() {
		try {
			return (CMObject) this.clone();
		} catch (CloneNotSupportedException e) {
			return this;
		}
	}

	public int compareTo(CMObject o) {
		final int comp = CMClass.classID(this).compareToIgnoreCase(
				CMClass.classID(o));
		return (comp == 0) ? ((this == o) ? 0 : 1) : comp;
	}
}
