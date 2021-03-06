package com.planet_ink.coffee_mud.Exits;

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
public class StdClosedDoorway extends StdExit {
	public String ID() {
		return "StdClosedDoorway";
	}

	public String Name() {
		return "a door";
	}

	public String displayText() {
		return "";
	}

	public String description() {
		return "An ordinary wooden door with swinging hinges and a latch.";
	}

	public boolean hasADoor() {
		return true;
	}

	public boolean hasALock() {
		return false;
	}

	public boolean defaultsLocked() {
		return false;
	}

	public boolean defaultsClosed() {
		return true;
	}

	public String closedText() {
		return "a closed door";
	}
}
