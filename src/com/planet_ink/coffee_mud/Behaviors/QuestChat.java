package com.planet_ink.coffee_mud.Behaviors;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Quest;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class QuestChat extends MudChat {

	public String ID() {
		return "QuestChat";
	}

	private Map<String, List<String>> alreadySaid = new Hashtable<String, List<String>>();
	private String myQuestName = null;

	public void registerDefaultQuest(String questName) {
		myQuestName = questName;
	}

	protected boolean match(MOB speaker, String expression, String message,
			String[] rest) {
		if (expression.indexOf("::") >= 0) {
			int x = expression.length() - 1;
			char c = ' ';
			boolean coded = false;
			while (x >= 0) {
				c = expression.charAt(x);
				if ((c == ':') && (x > 0) && (expression.charAt(x - 1) == ':')) {
					if (coded) {
						String codeStr = expression.substring(x + 2)
								.toUpperCase().trim();
						expression = expression.substring(0, x - 1).trim();
						List<String> V = alreadySaid.get(speaker.Name()
								.toUpperCase());
						if (V == null) {
							V = new Vector();
							alreadySaid.put(speaker.Name().toUpperCase(), V);
						} else if (V.contains(codeStr))
							return false;
						if (super.match(speaker, expression, message, rest)) {
							V.add(codeStr);
							if ((myQuestName != null)
									&& (myQuestName.length() > 0)) {
								Quest myQuest = CMLib.quests().fetchQuest(
										myQuestName);
								if (myQuest != null) {
									String stat = myQuest.getStat("CHAT:"
											+ speaker.Name().toUpperCase());
									if (stat.length() > 0)
										stat += " ";
									myQuest.setStat("CHAT:"
											+ speaker.Name().toUpperCase(),
											stat + codeStr);
								}
							}
							return true;
						}
						return false;
					}
					break;
				}
				coded = true;
				x--;
			}
		}
		return super.match(speaker, expression, message, rest);
	}
}