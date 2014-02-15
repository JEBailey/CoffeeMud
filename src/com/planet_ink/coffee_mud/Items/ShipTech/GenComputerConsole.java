package com.planet_ink.coffee_mud.Items.ShipTech;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

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
public class GenComputerConsole extends StdComputerConsole
{
	public String ID(){	return "GenComputerConsole";}
	
	public GenComputerConsole()
	{
		super();
	}

	public boolean isGeneric(){return true;}

	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	public void setReadableText(String text){}
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		basePhyStats.setSensesMask(basePhyStats.sensesMask()|PhyStats.SENSE_ITEMREADABLE);
		recoverPhyStats();
	}
	private final static String[] MYCODES={"HASLOCK","HASLID","CAPACITY",
							  "CONTAINTYPES","RIDEBASIS","MOBSHELD",
							  "POWERCAP","ACTIVATED","POWERREM",
							  "MANUFACTURER","INSTFACT"};
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0: return ""+hasALock();
		case 1: return ""+hasALid();
		case 2: return ""+capacity();
		case 3: return ""+containTypes();
		case 4: return ""+rideBasis();
		case 5: return ""+riderCapacity();
		case 6: return ""+powerCapacity();
		case 7: return ""+activated();
		case 8: return ""+powerRemaining();
		case 9: return ""+getManufacturerName();
		case 10: return ""+getInstalledFactor();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0: setLidsNLocks(hasALid(),isOpen(),CMath.s_bool(val),false); break;
		case 1: setLidsNLocks(CMath.s_bool(val),isOpen(),hasALock(),false); break;
		case 2: setCapacity(CMath.s_parseIntExpression(val)); break;
		case 3: setContainTypes(CMath.s_parseBitLongExpression(Container.CONTAIN_DESCS,val)); break;
		case 4: setRideBasis(CMath.s_parseListIntExpression(Rideable.RIDEABLE_DESCS,val)); break;
		case 5: setRiderCapacity(CMath.s_parseIntExpression(val)); break;
		case 6: setPowerCapacity(CMath.s_parseLongExpression(val)); break;
		case 7: activate(CMath.s_bool(val)); break;
		case 8: setPowerRemaining(CMath.s_parseLongExpression(val)); break;
		case 9: setManufacturerName(val); break;
		case 10: setInstalledFactor(CMath.s_float(val)); break;
		default:
			CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
			break;
		}
	}
	protected int getCodeNum(String code){
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	public String[] getStatCodes()
	{
		if(codes!=null) return codes;
		String[] MYCODES=CMProps.getStatCodesList(GenComputerConsole.MYCODES,this);
		String[] superCodes=GenericBuilder.GENITEMCODES;
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenComputerConsole)) return false;
		String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}
