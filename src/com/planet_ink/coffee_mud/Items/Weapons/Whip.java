package com.planet_ink.coffee_mud.Items.Weapons;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;


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
public class Whip extends StdWeapon
{
	public String ID(){	return "Whip";}
	public Whip()
	{
		super();

		setName("a long leather whip");
		setDisplayText("a long leather whip has been dropped by someone.");
		setDescription("Weaved of leather with a nasty little barb at the end.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(2);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(2);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
		weaponType=Weapon.TYPE_SLASHING;//?????????
		weaponClassification=Weapon.CLASS_FLAILED;
	}


}
