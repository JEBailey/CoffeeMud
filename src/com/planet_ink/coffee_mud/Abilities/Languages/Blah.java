package com.planet_ink.coffee_mud.Abilities.Languages;

import java.util.List;

import com.planet_ink.coffee_mud.core.CMLib;

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

public class Blah extends StdLanguage {
	public String ID() {
		return "Blah";
	}

	public String name() {
		return "Blah";
	}

	public static List<String[]> wordLists = null;
	private static boolean mapped = false;

	public Blah() {
		super();
		if (!mapped) {
			mapped = true;
			CMLib.ableMapper().addCharAbilityMapping("Archon", 1, ID(), false);
		}
	}

	public List<String[]> translationVector(String language) {
		return wordLists;
	}

	public String translate(String language, String word) {
		return fixCase(word, "blah");
	}
}
