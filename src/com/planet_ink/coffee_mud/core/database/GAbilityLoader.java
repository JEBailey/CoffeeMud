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
public class GAbilityLoader
{
	protected DBConnector DB=null;
	public GAbilityLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public List<DatabaseEngine.AckRecord> DBReadAbilities()
	{
		DBConnection D=null;
		Vector<DatabaseEngine.AckRecord> rows=new Vector<DatabaseEngine.AckRecord>();
		try
		{
			D=DB.DBFetch();
			ResultSet R=D.query("SELECT * FROM CMGAAC");
			while(R.next())
				rows.addElement(new DatabaseEngine.AckRecord(DBConnections.getRes(R,"CMGAID"), DBConnections.getRes(R,"CMGAAT"), DBConnections.getRes(R,"CMGACL")));
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
	public void DBCreateAbility(String classID, String typeClass, String data)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMGAAC ("
		 +"CMGAID, "
		 +"CMGAAT, "
		 +"CMGACL "
		 +") values ("
		 +"'"+classID+"',"
		 +"?,"
		 +"'"+typeClass+"'"
		 +")", 
		 data+" ");
	}
	public void DBDeleteAbility(String classID)
	{
		DB.update("DELETE FROM CMGAAC WHERE CMGAID='"+classID+"'");
	}
}
