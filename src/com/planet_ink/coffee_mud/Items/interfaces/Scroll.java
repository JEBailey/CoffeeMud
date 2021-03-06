package com.planet_ink.coffee_mud.Items.interfaces;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;

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
public interface Scroll extends MiscMagic, Item, SpellHolder {
	public boolean useTheScroll(Ability A, MOB mob);

	public boolean isReadableScrollBy(String name);

	public void setReadableScrollBy(String name);

	public void readIfAble(MOB mob, Scroll me, String spellName);
}
