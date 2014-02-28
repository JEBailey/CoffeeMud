package com.planet_ink.coffee_mud.Abilities.Languages;

import java.util.List;
import java.util.Vector;

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

public class Draconic extends StdLanguage {
	public String ID() {
		return "Draconic";
	}

	public String name() {
		return "Draconic";
	}

	public static List<String[]> wordLists = null;

	public Draconic() {
		super();
	}

	public List<String[]> translationVector(String language) {
		if (wordLists == null) {
			String[] one = { "y" };
			String[] two = { "ve", "ov", "iv", "si", "es", "se" };
			String[] three = { "see", "sev", "ave", "ces", "ven", "sod" };
			String[] four = { "nirg", "avet", "sav`e", "choc", "sess", "sens",
					"vent", "vens", "sven", "yans", "vays" };
			String[] five = { "splut", "svets", "fruite", "dwagg", "vrers",
					"verrs", "srens", "swath", "senys", "varen" };
			String[] six = { "choccie", "svenren", "yorens", "vyrues",
					"whyrie", "vrysenso", "forin", "sinnes", "sessis",
					"uroven", "xorers", "nosees" };
			wordLists = new Vector<String[]>();
			wordLists.add(one);
			wordLists.add(two);
			wordLists.add(three);
			wordLists.add(four);
			wordLists.add(five);
			wordLists.add(six);
		}
		return wordLists;
	}
}
