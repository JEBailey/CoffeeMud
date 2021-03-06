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

public class Druidic extends StdLanguage {
	public String ID() {
		return "Druidic";
	}

	public String name() {
		return "Druidic";
	}

	public static List<String[]> wordLists = null;

	public Druidic() {
		super();
	}

	public List<String[]> translationVector(String language) {
		if (wordLists == null) {
			String[] one = { "" };
			String[] two = { "hissssss", "hoo", "caw", "arf", "bow-wow",
					"bzzzzzz", "grunt", "bawl" };
			String[] three = { "chirp", "tweet", "mooooo", "oink", "quack",
					"tweet", "bellooooow", "cackle", "hooooowwwwl", "!dook!" };
			String[] four = { "ruff", "meow", "grrrrowl", "roar", "cluck",
					"honk", "gibber", "hoot", "snort", "groooan", "trill",
					"snarl" };
			String[] five = { "croak", "bark", "blub-blub", "cuckoo", "squeak",
					"peep", "screeech!", "twitter", "cherp", "wail" };
			String[] six = { "hummmmmm", "bleat", "*whistle*", "yelp", "neigh",
					"whinny", "growl", "screeaam!!" };
			String[] seven = { "gobble-gobble", "ribbit", "b-a-a-a-h",
					"n-a-a-a-y", "heehaw", "cock-a-doodle-doo" };
			wordLists = new Vector<String[]>();
			wordLists.add(one);
			wordLists.add(two);
			wordLists.add(three);
			wordLists.add(four);
			wordLists.add(five);
			wordLists.add(six);
			wordLists.add(seven);
		}
		return wordLists;
	}
}
