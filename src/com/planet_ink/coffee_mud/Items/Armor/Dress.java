package com.planet_ink.coffee_mud.Items.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;


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
public class Dress extends StdArmor
{
	public String ID(){	return "Dress";}
	public Dress()
	{
		super();

		setName("a nice dress");
		setDisplayText("a nice dress has been left here.");
		setDescription("Well and neatly made, this plain dress would look fine on just about anyone.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(8);
		basePhyStats().setWeight(10);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}


}
