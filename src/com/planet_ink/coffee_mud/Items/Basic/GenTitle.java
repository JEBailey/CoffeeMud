package com.planet_ink.coffee_mud.Items.Basic;
import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class GenTitle extends StdTitle
{
	public String ID(){	return "GenTitle";}
	protected String readableText="";

	public GenTitle()
	{
		super();
		setName("a generic title");
		basePhyStats.setWeight(2);
		setDisplayText("a generic title sits here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		recoverPhyStats();
	}

	public boolean isGeneric(){return true;}

	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	public String readableText(){return readableText;}
	public void setReadableText(String text){readableText=text;}
	public String landPropertyID(){return readableText;}
	public void setLandPropertyID(String landID){
		readableText=landID;
		updateTitleName();
	}
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
	}
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code,val);
	}
	private static String[] codes=null;
	public String[] getStatCodes()
	{
		if(codes==null)
			codes=CMProps.getStatCodesList(GenericBuilder.GENITEMCODES,this);
		return codes; 
	}
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenTitle)) return false;
		for(int i=0;i<getStatCodes().length;i++)
			if(!E.getStat(getStatCodes()[i]).equals(getStat(getStatCodes()[i])))
				return false;
		return true;
	}
}
