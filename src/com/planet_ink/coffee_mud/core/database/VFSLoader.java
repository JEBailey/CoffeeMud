package com.planet_ink.coffee_mud.core.database;

import java.sql.ResultSet;

import com.planet_ink.coffee_mud.core.B64Encoder;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.Log;

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
public class VFSLoader {
	protected DBConnector DB = null;

	public VFSLoader(DBConnector newDB) {
		DB = newDB;
	}

	public CMFile.CMVFSDir DBReadDirectory() {
		DBConnection D = null;
		CMFile.CMVFSDir root = new CMFile.CMVFSDir(null, "");
		try {
			D = DB.DBFetch();
			if (D == null) {
				return null;
			}
			ResultSet R = D.query("SELECT * FROM CMVFS");
			while (R.next()) {
				String fname = DBConnections.getRes(R, "CMFNAM");
				int mask = (int) DBConnections.getLongRes(R, "CMDTYP");
				long time = DBConnections.getLongRes(R, "CMMODD");
				String author = DBConnections.getRes(R, "CMWHOM");
				root.add(new CMFile.CMVFSFile(fname, mask, time, author));
			}
		} catch (Exception sqle) {
			Log.errOut("VFSLoader", sqle);
			return null;
		} finally {
			DB.DBDone(D);
		}
		// log comment
		return root;
	}

	public CMFile.CMVFSFile DBRead(String filename) {
		DBConnection D = null;
		CMFile.CMVFSFile row = null;
		try {
			D = DB.DBFetch();
			ResultSet R = D.query("SELECT * FROM CMVFS WHERE CMFNAM='"
					+ filename + "'");
			if (R.next()) {
				String possFName = DBConnections.getRes(R, "CMFNAM");
				if (possFName.equalsIgnoreCase(filename)) {
					int bits = (int) DBConnections.getLongRes(R, "CMDTYP");
					long mod = DBConnections.getLongRes(R, "CMMODD");
					String author = DBConnections.getRes(R, "CMWHOM");
					String data = DBConnections.getRes(R, "CMDATA");
					row = new CMFile.CMVFSFile(filename, bits, mod, author);
					row.setData(B64Encoder.B64decode(data));
				}
			}
		} catch (Exception sqle) {
			Log.errOut("VFSLoader", sqle);
		} finally {
			DB.DBDone(D);
		}
		// log comment
		return row;
	}

	private String makeVBuf(Object data) {
		String buf = null;
		if (data == null)
			buf = "";
		else if (data instanceof String)
			buf = B64Encoder
					.B64encodeBytes(CMStrings.strToBytes((String) data));
		else if (data instanceof StringBuffer)
			buf = B64Encoder.B64encodeBytes(CMStrings
					.strToBytes(((StringBuffer) data).toString()));
		else if (data instanceof byte[])
			buf = B64Encoder.B64encodeBytes((byte[]) data);
		return buf;
	}

	public void DBCreate(String filename, int bits, String creator,
			long updateTime, Object data) {
		String buf = makeVBuf(data);
		if (buf == null) {
			Log.errOut("VFSLoader", "Unable to save " + filename
					+ " due to illegal data type: " + data.getClass().getName());
			return;
		}
		DB.updateWithClobs("INSERT INTO CMVFS (" + "CMFNAM, " + "CMDTYP, "
				+ "CMMODD, " + "CMWHOM, " + "CMDATA" + ") values (" + "'"
				+ filename + "'," + "" + (bits & CMFile.VFS_MASK_MASKSAVABLE)
				+ "," + "" + updateTime + "," + "'" + creator + "'," + "?"
				+ ")", buf);
	}

	public void DBUpSert(String filename, int bits, String creator,
			long updateTime, Object data) {
		DBConnection D = null;
		try {
			D = DB.DBFetch();
			ResultSet R = D.query("SELECT CMFNAM FROM CMVFS WHERE CMFNAM='"
					+ filename + "'");
			if (!R.next()) {
				R.close();
				DB.DBDone(D);
				D = null;
				DBCreate(filename, bits, creator, updateTime, data);
			} else {
				R.close();
				String buf = makeVBuf(data);
				if (buf == null) {
					Log.errOut("VFSLoader", "Unable to save " + filename
							+ " due to illegal data type: "
							+ data.getClass().getName());
					return;
				}
				DB.updateWithClobs("UPDATE CMVFS SET " + "CMDTYP="
						+ (bits & CMFile.VFS_MASK_MASKSAVABLE) + ", "
						+ "CMMODD=" + updateTime + "," + "CMWHOM='" + creator
						+ "', " + "CMDATA=? WHERE CMFNAM='" + filename + "'",
						buf);
			}
		} catch (Exception sqle) {
			Log.errOut("VFSLoader", sqle);
		} finally {
			DB.DBDone(D);
		}
	}

	public void DBDelete(String filename) {
		DBConnection D = null;
		try {
			D = DB.DBFetch();
			D.update("DELETE FROM CMVFS WHERE CMFNAM='" + filename + "'", 0);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
			if (DB.queryRows("SELECT * FROM CMVFS WHERE CMFNAM='" + filename
					+ "'") > 0)
				Log.errOut("Failed to delete virtual file " + filename + ".");
		} catch (Exception sqle) {
			Log.errOut("VFSLoader", sqle);
		} finally {
			DB.DBDone(D);
		}
	}

}
