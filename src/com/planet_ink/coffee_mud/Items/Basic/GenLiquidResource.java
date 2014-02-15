package com.planet_ink.coffee_mud.Items.Basic;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class GenLiquidResource extends GenDrink implements RawMaterial, Drink
{
	public String ID(){	return "GenLiquidResource";}
	public GenLiquidResource()
	{
		super();
		setName("a puddle of resource thing");
		setDisplayText("a puddle of resource sits here.");
		setDescription("");
		setMaterial(RawMaterial.RESOURCE_FRESHWATER);
		disappearsAfterDrinking=false;
		basePhyStats().setWeight(0);
		setCapacity(0);
		recoverPhyStats();
	}
	protected static Ability rot=null;

	public void setMaterial(int newValue)
	{
		super.setMaterial(newValue);
		decayTime=0;
	}

	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if(rot==null)
		{
			rot=CMClass.getAbility("Prayer_Rot");
			if(rot==null) return;
			rot.setAffectedOne(null);
		}
		rot.executeMsg(this,msg);
	}

	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(rot==null)
		{
			rot=CMClass.getAbility("Prayer_Rot");
			if(rot==null) return true;
			rot.setAffectedOne(null);
		}
		if(!rot.okMessage(this,msg))
			return false;
		return super.okMessage(host,msg);
	}

	protected String domainSource=null;
	public String domainSource(){return domainSource;}
	public void setDomainSource(String src){domainSource=src;}
	public boolean rebundle(){return false;}//CMLib.materials().rebundle(this);}
	public void quickDestroy(){ CMLib.materials().quickDestroy(this);}
}
