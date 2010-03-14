package com.planet_ink.coffee_mud.core.database;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import java.sql.*;
import java.util.*;


/* 
   Copyright 2000-2010 Bo Zimmerman

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
public class JournalLoader
{
	protected DBConnector DB=null;
	public JournalLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	
	public synchronized int DBCount(String Journal, String from, String to)
	{
		int ct=0;
		DBConnection D=null;
		try
		{
			D=DB.DBFetch();
			ResultSet R=D.query("SELECT * FROM CMJRNL WHERE CMJRNL='"+Journal+"'");
			while(R.next())
			{
				if((from!=null)&&(!from.equalsIgnoreCase(DBConnections.getRes(R,"CMFROM"))))
				   continue;
				if((to!=null)&&(!to.equalsIgnoreCase(DBConnections.getRes(R,"CMTONM"))))
				   continue;
				ct++;
			}
			DB.DBDone(D);
		}
		catch(Exception sqle)
		{
			Log.errOut("Journal",sqle);
			if(D!=null) DB.DBDone(D);
			return ct;
		}
		return ct;
	}
    
    public String DBGetRealName(String possibleName)
    {
        DBConnection D=null;
        String realName=null;
        try
        {
            D=DB.DBFetch();
            ResultSet R=D.query("SELECT * FROM CMJRNL WHERE CMJRNL='"+possibleName+"'");
            if(R.next())
            {
                realName=DBConnections.getRes(R,"CMJRNL");
                if(realName.length()==0)
                {
                    DB.DBDone(D);
                    return realName=null;
                }
            }
            DB.DBDone(D);
        }
        catch(Exception sqle)
        {
            Log.errOut("Journal",sqle);
            if(D!=null) DB.DBDone(D);
        }
        return realName;
    }
	
	public long DBReadNewJournalDate(String Journal, String name)
	{
		
		Hashtable TABLE=(Hashtable)Resources.getResource("JOURNALDATECACHE");
		if(TABLE==null)
		{
			TABLE=new Hashtable();
			Resources.submitResource("JOURNALDATECACHE",TABLE);
		}
		synchronized(TABLE)
		{
			Hashtable H=(Hashtable)TABLE.get(Journal);
			if(H!=null)
			{
				Long l=(Long)H.get(name);
				Long l2=(Long)H.get("ALL");
				if((l!=null)&&(l2==null)) return l.longValue();
				if((l2!=null)&&(l==null)) return l2.longValue();
				if((l!=null)&&(l2!=null)) return l.longValue()>l2.longValue()?l.longValue():l2.longValue();
				return 0;
			}
			Vector<JournalsLibrary.JournalEntry> V=DBReadJournalMsgs(Journal);
			H=new Hashtable();
			TABLE.put(Journal,H);
			if(V==null) return 0;
			if(V.size()==0) return 0;
			for(int v=0;v<V.size();v++)
			{
				JournalsLibrary.JournalEntry E=(JournalsLibrary.JournalEntry)V.elementAt(v);
				String to=E.to;
				long compdate=E.update;
				if(to.equalsIgnoreCase("all"))
				{
					Long l2=(Long)H.get("ALL");
					if((l2==null)||(l2.longValue()<compdate))
					{
						if(H.containsKey("ALL")) H.remove("ALL");
						H.put("ALL",Long.valueOf(compdate));
					}
				}
				else
				{
					Long l2=(Long)H.get(to);
					if((l2==null)||(l2.longValue()<compdate))
					{
						if(H.containsKey(to)) H.remove(to);
						H.put(to,Long.valueOf(compdate));
					}
					String from=E.from;
					l2=(Long)H.get(from); // from
					if((l2==null)||(l2.longValue()<compdate))
					{
						if(H.containsKey(from)) H.remove(from);
						H.put(from,Long.valueOf(compdate));
					}
				}
			}
			return DBReadNewJournalDate(Journal,name);
		}
	}
	
	public synchronized Vector DBReadJournals()
	{
		DBConnection D=null;
		Vector journals = new Vector();
		try
		{
			D=DB.DBFetch();
			ResultSet R=D.query("SELECT * FROM CMJRNL");
			while(R.next())
			{
				String which=DBConnections.getRes(R,"CMJRNL");
				if(!journals.contains(which)) {
				    if((which.toUpperCase().startsWith("SYSTEM_"))
				    &&(journals.size()>0))
				    	journals.insertElementAt(which,0);
				    else
				    	journals.addElement(which);
				}
			}
			DB.DBDone(D);
		}
		catch(Exception sqle)
		{
			Log.errOut("Journal",sqle);
			if(D!=null) DB.DBDone(D);
			return null;
		}
		return journals;
	}
	
	protected JournalsLibrary.JournalEntry DBReadJournalEntry(ResultSet R)
	{
		JournalsLibrary.JournalEntry entry=new JournalsLibrary.JournalEntry();
		entry.key=DBConnections.getRes(R,"CMJKEY");
		entry.from=DBConnections.getRes(R,"CMFROM");
		String dateStr = DBConnections.getRes(R,"CMDATE");
		entry.to=DBConnections.getRes(R,"CMTONM");
		entry.subj=DBConnections.getRes(R,"CMSUBJ");
		entry.parent=DBConnections.getRes(R,"CMPART");
		entry.attributes=CMath.s_long(DBConnections.getRes(R,"CMATTR"));
		entry.subj=DBConnections.getRes(R,"CMDATA");
		entry.msg=DBConnections.getRes(R,"CMMSGT");
		
		int datestrdex=dateStr.indexOf("/");
		if(datestrdex>=0)
		{
			entry.update=CMath.s_long(dateStr.substring(datestrdex+1));
			entry.date=CMath.s_long(dateStr.substring(0,datestrdex));
		}
		else
		{
			entry.date=CMath.s_long(dateStr);
			long realUpdate=CMath.s_long(DBConnections.getRes(R,"CMUPTM"));
			if(realUpdate > entry.date)
				entry.update=realUpdate;
			else
				entry.update=entry.date;
		}
		
		String subject=entry.subj.toUpperCase();
		if((subject.startsWith("MOTD"))
		||(subject.startsWith("MOTM"))
		||(subject.startsWith("MOTY")))
		{
			char c=subject.charAt(3);
			entry.subj=entry.subj.substring(4);
			long last=entry.date;
			if(c=='D') last=last+TimeManager.MILI_DAY;
			else
			if(c=='M') last=last+TimeManager.MILI_MONTH;
			else
			if(c=='Y') last=last+TimeManager.MILI_YEAR;
			entry.update=last;
		}
		return entry;
	}
	
	public synchronized Vector<JournalsLibrary.JournalEntry> DBReadJournalMsgs(String Journal)
	{
		Vector<JournalsLibrary.JournalEntry> journal=new Vector<JournalsLibrary.JournalEntry>();
		//Resources.submitResource("JOURNAL_"+Journal);
		DBConnection D=null;
		try
		{
			D=DB.DBFetch();
			String str="SELECT * FROM CMJRNL WHERE CMJRNL='"+Journal+"'";
			ResultSet R=D.query(str);
			while(R.next())
			{
				JournalsLibrary.JournalEntry entry = DBReadJournalEntry(R); 
				journal.addElement(entry);
			}
			DB.DBDone(D);
		}
		catch(Exception sqle)
		{
			Log.errOut("Journal",sqle);
			if(D!=null) DB.DBDone(D);
			return null;
		}
		Collections.sort(journal);
		return journal;
	}

	public int getFirstMsgIndex(Vector journal, String from, String to, String subj)
	{
		if(journal==null) return -1;
		for(int i=0;i<journal.size();i++)
		{
			JournalsLibrary.JournalEntry E=(JournalsLibrary.JournalEntry)journal.elementAt(i);
			if((from!=null)&&(!(E.from).equalsIgnoreCase(from)))
				continue;
			if((to!=null)&&(!(E.to).equalsIgnoreCase(to)))
				continue;
			if((subj!=null)&&(!(E.subj).equalsIgnoreCase(subj)))
				continue;
			return i;
		}
		return -1;
	}
	
	public synchronized void DBDelete(String oldkey)
	{
		DB.update("DELETE FROM CMJRNL WHERE CMJKEY='"+oldkey+"'");
	}
	
	public synchronized void DBUpdateJournal(String key, String subject, String msg)
	{
		DB.update("UPDATE CMJRNL SET CMSUBJ='"+subject+"', CMMSGT='"+msg+"' WHERE CMJKEY='"+key+"'");
	}
	
	public synchronized void DBDeletePlayerData(String name)
	{
		DBConnection D=null;
		try
		{
			D=DB.DBFetch();
			D.update("DELETE FROM CMJRNL WHERE CMTONM='"+name+"'",0);
		}
		catch(Exception sqle)
		{
			Log.errOut("JournalLoader",sqle.getMessage());
		}
		if(D!=null) DB.DBDone(D);
		
	}
	
	public synchronized void DBDelete(String Journal, int which)
	{
		if((which >=0)&&(which < Integer.MAX_VALUE))
		{
			Vector journal=DBReadJournalMsgs(Journal);
			if(journal==null) return;
			if(which>=journal.size()) return;
			JournalsLibrary.JournalEntry entry=(JournalsLibrary.JournalEntry)journal.elementAt(which);
			String oldkey=entry.key;
			DB.update("DELETE FROM CMJRNL WHERE CMJKEY='"+oldkey+"'");
		}
		else
		if(which==Integer.MAX_VALUE)
		{
			DB.update("DELETE FROM CMJRNL WHERE CMJRNL='"+Journal+"'");
		}
	}
	
	public void updateJournalDateCacheIfNecessary(Hashtable H, String to, String from, long date)
	{
		if(to.equalsIgnoreCase("all"))
		{
			Long l2=(Long)H.get("ALL");
			if((l2==null)||(l2.longValue()<System.currentTimeMillis()))
			{
				if(H.containsKey("ALL")) H.remove("ALL");
				H.put("ALL",Long.valueOf(System.currentTimeMillis()));
			}
		}
		else
		{
			Long l2=(Long)H.get(to);
			if((l2==null)||(l2.longValue()<System.currentTimeMillis()))
			{
				if(H.containsKey(to)) H.remove(to);
				H.put(to,Long.valueOf(System.currentTimeMillis()));
			}
			l2=(Long)H.get(from);
			if((l2==null)||(l2.longValue()<System.currentTimeMillis()))
			{
				if(H.containsKey(from)) H.remove(from);
				H.put(from,Long.valueOf(System.currentTimeMillis()));
			}
		}
	}
	
	public synchronized void DBWrite(String Journal, 
									 String from, 
									 String to, 
									 String subject, 
									 String message, 
									 int which)
	{
		String date=System.currentTimeMillis()+"";
		if(which>=0)
		{
			Vector<JournalsLibrary.JournalEntry> journal=DBReadJournalMsgs(Journal);
			if(journal==null) return;
			if(which>=journal.size()) return;
			JournalsLibrary.JournalEntry entry=(JournalsLibrary.JournalEntry)journal.elementAt(which);
			long olddate=entry.date;
			String oldkey=entry.key;
			String oldmsg=entry.msg;
			message=oldmsg+JournalsLibrary.JOURNAL_BOUNDARY
                          +"^yReply from^N: "+from+"%0D"
                          +"^yDate/Time ^N: "+CMLib.time().date2String(System.currentTimeMillis())+"%0D"
                          +message;
			DB.update("UPDATE CMJRNL SET CMDATE='"+olddate+"/"+date+"', CMMSGT='"+message+"' WHERE CMJKEY='"+oldkey+"'");
			
			Hashtable TABLE=(Hashtable)Resources.getResource("JOURNALDATECACHE");
			if(TABLE!=null)
			{
				synchronized(TABLE)
				{
					Hashtable H=(Hashtable)TABLE.get(Journal);
					if(H!=null)
						updateJournalDateCacheIfNecessary(H, entry.to, entry.from, System.currentTimeMillis());
				}
			}
		}
		else
		{
			if(subject.length()>255) subject=subject.substring(0,255);
			DB.update(
			"INSERT INTO CMJRNL ("
			+"CMJKEY, "
			+"CMJRNL, "
			+"CMFROM, "
			+"CMDATE, "
			+"CMTONM, "
			+"CMSUBJ, "
			+"CMMSGT "
			+") VALUES ('"
			+(Journal+from+date+Math.random())
			+"','"+Journal
			+"','"+from
			+"','"+date
			+"','"+to
			+"','"+subject
			+"','"+message+"')");
			
			Hashtable TABLE=(Hashtable)Resources.getResource("JOURNALDATECACHE");
			if(TABLE!=null)
			{
				synchronized(TABLE)
				{
					Hashtable H=(Hashtable)TABLE.get(Journal);
					if(H!=null)
						updateJournalDateCacheIfNecessary(H,to,from,System.currentTimeMillis());
				}
			}
		}
	}
}
