package com.planet_ink.coffee_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine;
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
public class GCClassLoader
{
	protected DBConnector DB=null;
	public GCClassLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	
	public List<DatabaseEngine.AckRecord> DBReadClasses()
	{
		DBConnection D=null;
		Vector<DatabaseEngine.AckRecord> rows=new Vector<DatabaseEngine.AckRecord>();
		try
		{
			D=DB.DBFetch();
			ResultSet R=D.query("SELECT * FROM CMCCAC");
			while(R.next())
			{
				DatabaseEngine.AckRecord ack = new DatabaseEngine.AckRecord(
					DBConnections.getRes(R,"CMCCID"),
					DBConnections.getRes(R,"CMCDAT"),
					"GenCharClass");
				rows.addElement(ack);
			}
		}
		catch(Exception sqle)
		{
			Log.errOut("DataLoader",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
		return rows;
	}
	
	public void DBDeleteClass(String classID)
	{
		DB.update("DELETE FROM CMCCAC WHERE CMCCID='"+classID+"'");
	}
	
	public void DBCreateClass(String classID, String data)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMCCAC ("
		 +"CMCCID, "
		 +"CMCDAT "
		 +") values ("
		 +"'"+classID+"',"
		 +"?"
		 +")", 
		 data+" ");
	}
}
