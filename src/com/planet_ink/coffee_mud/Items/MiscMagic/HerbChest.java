package com.planet_ink.coffee_mud.Items.MiscMagic;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMClass;

/*
Copyright 2008-2014 Bo Zimmerman

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
public class HerbChest extends BagOfHolding {
	public String ID(){	return "HerbChest";}
	public HerbChest() {
		super();
		setName("a small chest");
		setDisplayText("a small chest with many tiny drawers stands here.");
		setDescription("The most common magical item in the world, this carefully crafted chest is designed to help alchemists of the world carry their herbal supplies with them everywhere.");
		secretIdentity="An Alchemist's Herb Chest";
		setContainTypes(RawMaterial.RESOURCE_HERBS);
		capacity=500;
		baseGoldValue=0;
		material=RawMaterial.RESOURCE_REDWOOD;
		Ability A=CMClass.getAbility("Prop_HaveZapper");
		if(A!=null) {
			A.setMiscText("+SYSOP -MOB -anyclass +alchemist");
			addNonUninvokableEffect(A);
		}
	}
}
