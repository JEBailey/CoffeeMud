package com.planet_ink.coffee_mud.Areas;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Areas.interfaces.AutoGenArea;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.ScriptingEngine;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Libraries.interfaces.XMLLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.coffee_mud.core.collections.STreeMap;
import com.planet_ink.coffee_mud.core.collections.SVector;
import com.planet_ink.coffee_mud.core.exceptions.CMException;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class StdAutoGenInstance extends StdArea implements AutoGenArea {
	public String ID() {
		return "StdAutoGenInstance";
	}

	private long flags = Area.FLAG_INSTANCE_PARENT;

	public long flags() {
		return flags;
	}

	protected SVector<AreaInstanceChild> instanceChildren = new SVector<AreaInstanceChild>();
	protected volatile int instanceCounter = 0;
	protected long childCheckDown = CMProps.getMillisPerMudHour()
			/ CMProps.getTickMillis();
	protected WeakReference<Area> parentArea = null;
	protected String filePath = "randareas/example.xml";
	protected Map<String, String> varMap = new Hashtable<String, String>(1);

	protected String getStrippedRoomID(String roomID) {
		int x = roomID.indexOf('#');
		if (x < 0)
			return null;
		return roomID.substring(x);
	}

	protected String convertToMyArea(String roomID) {
		String strippedID = getStrippedRoomID(roomID);
		if (strippedID == null)
			return null;
		return Name() + strippedID;
	}

	protected Area getParentArea() {
		if ((parentArea != null) && (parentArea.get() != null))
			return parentArea.get();
		int x = Name().indexOf('_');
		if (x < 0)
			return null;
		if (!CMath.isNumber(Name().substring(0, x)))
			return null;
		Area parentA = CMLib.map().getArea(Name().substring(x + 1));
		if ((parentA == null)
				|| (!CMath.bset(parentA.flags(), Area.FLAG_INSTANCE_PARENT))
				|| (CMath.bset(parentA.flags(), Area.FLAG_INSTANCE_CHILD)))
			return null;
		parentArea = new WeakReference<Area>(parentA);
		return parentA;
	}

	@Override
	public int getPercentRoomsCached() {
		return (getParentArea() == null) ? 0 : 100;
	}

	@Override
	public int[] getAreaIStats() {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return emptyStats;
		Area parentArea = getParentArea();
		final String areaName = (parentArea == null) ? Name() : parentArea
				.Name();
		int[] statData = (int[]) Resources.getResource("STATS_"
				+ areaName.toUpperCase());
		if (statData != null)
			return statData;
		synchronized (("STATS_" + areaName).intern()) {
			if (parentArea == null) {
				Enumeration<AreaInstanceChild> childE = instanceChildren
						.elements();
				int ct = 0;
				if (childE.hasMoreElements()) {
					statData = new int[Area.Stats.values().length];
					for (; childE.hasMoreElements();) {
						int[] theseStats = childE.nextElement().A
								.getAreaIStats();
						if (theseStats != emptyStats) {
							ct++;
							for (int i = 0; i < theseStats.length; i++)
								statData[i] += theseStats[i];
						}
					}
				}
				if ((ct == 0) || (statData == null))
					return emptyStats;
				for (int i = 0; i < statData.length; i++)
					statData[i] = statData[i] / ct;
				Resources.removeResource("HELP_" + areaName.toUpperCase());
				Resources.submitResource("STATS_" + areaName.toUpperCase(),
						statData);
			} else {
				statData = buildAreaIStats();
			}
		}
		return statData;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (CMath.bset(flags(), Area.FLAG_INSTANCE_CHILD))
			return true;
		if ((--childCheckDown) <= 0) {
			childCheckDown = CMProps.getMillisPerMudHour()
					/ CMProps.getTickMillis();
			synchronized (instanceChildren) {
				for (int i = instanceChildren.size() - 1; i >= 0; i--) {
					Area childA = instanceChildren.elementAt(i).A;
					if (childA.getAreaState() != Area.State.ACTIVE) {
						List<WeakReference<MOB>> V = instanceChildren
								.elementAt(i).mobs;
						boolean anyInside = false;
						for (WeakReference<MOB> wmob : V) {
							MOB M = wmob.get();
							if ((M != null)
									&& CMLib.flags().isInTheGame(M, true)
									&& (M.location() != null)
									&& (M.location().getArea() == childA)) {
								anyInside = true;
								break;
							}
						}
						if (!anyInside) {
							instanceChildren.remove(i);
							for (WeakReference<MOB> wmob : V) {
								MOB M = wmob.get();
								if ((M != null) && (M.location() != null)
										&& (M.location().getArea() == this))
									M.setLocation(M.getStartRoom());
							}
							MOB mob = CMClass.sampleMOB();
							for (Enumeration<Room> e = childA.getProperMap(); e
									.hasMoreElements();) {
								Room R = e.nextElement();
								R.executeMsg(mob, CMClass.getMsg(mob, R, null,
										CMMsg.MSG_EXPIRE, null));
							}
							CMLib.map().delArea(childA);
							childA.destroy();
						}
					}
				}
			}
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (CMath.bset(flags(), Area.FLAG_INSTANCE_CHILD)
				&& (msg.sourceMinor() == CMMsg.TYP_SPEAK)
				&& (msg.sourceMessage() != null)
				&& ((msg.sourceMajor() & CMMsg.MASK_MAGIC) == 0)) {
			String said = CMStrings.getSayFromMessage(msg.sourceMessage());
			if ("RESET INSTANCE".equalsIgnoreCase(said)) {
				Room returnToRoom = null;
				Room thisRoom = msg.source().location();
				if (thisRoom.getArea() == this) {
					for (int d = 0; d < Directions.NUM_DIRECTIONS(); d++) {
						Room R = thisRoom.getRoomInDir(d);
						if ((R != null) && (R.getArea() != null)
								&& (R.getArea() != this))
							returnToRoom = R;
					}
				}
				if (returnToRoom == null) {
					msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, null,
							CMMsg.MSG_OK_ACTION, CMMsg.NO_EFFECT,
							CMMsg.NO_EFFECT,
							"You must be at an entrance to reset the area."));
					return;
				}
				Area A = this.getParentArea();
				if (A instanceof StdAutoGenInstance) {
					StdAutoGenInstance parentA = (StdAutoGenInstance) A;
					synchronized (parentA.instanceChildren) {
						for (int i = parentA.instanceChildren.size() - 1; i >= 0; i--) {
							List<WeakReference<MOB>> V = parentA.instanceChildren
									.elementAt(i).mobs;
							if (parentA.instanceChildren.elementAt(i).A == this) {
								for (WeakReference<MOB> wM : V) {
									MOB M = wM.get();
									if ((M != null)
											&& CMLib.flags().isInTheGame(M,
													true)
											&& (M.location() != null)
											&& (M.location() != returnToRoom)
											&& (M.location().getArea() == this)) {
										returnToRoom.bringMobHere(M, true);
										CMLib.commands().postLook(M, true);
									}
								}
								parentA.instanceChildren.remove(i);
								MOB mob = CMClass.sampleMOB();
								for (Enumeration<Room> e = getProperMap(); e
										.hasMoreElements();) {
									Room R = e.nextElement();
									R.executeMsg(mob, CMClass.getMsg(mob, R,
											null, CMMsg.MSG_EXPIRE, null));
								}
								msg.addTrailerMsg(CMClass.getMsg(msg.source(),
										CMMsg.MSG_OK_ACTION,
										"The instance has been reset."));
								CMLib.map().delArea(this);
								destroy();
								return;
							}
						}
					}
				}
				msg.addTrailerMsg(CMClass.getMsg(msg.source(),
						CMMsg.MSG_OK_ACTION, "The instance failed to reset."));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (CMath.bset(flags(), Area.FLAG_INSTANCE_CHILD))
			return true;
		setAreaState(Area.State.PASSIVE);
		if ((msg.sourceMinor() == CMMsg.TYP_ENTER)
				&& (msg.target() instanceof Room)
				&& (CMath.bset(flags(), Area.FLAG_INSTANCE_PARENT))
				&& (isRoom((Room) msg.target()))
				&& (((msg.source().getStartRoom() == null) || (msg.source()
						.getStartRoom().getArea() != this)))) {
			if (msg.source().isMonster()) {
				Set<MOB> friends = msg.source().getGroupMembers(
						new HashSet<MOB>());
				boolean playerInvolved = false;
				for (MOB M : friends)
					playerInvolved = playerInvolved || (!M.isMonster());
				if (!playerInvolved) {
					msg.source()
							.tell("You'll need to be accompanied by an adult to enter there.");
					return false;
				}
			}
			synchronized (instanceChildren) {
				int myDex = -1;
				for (int i = 0; i < instanceChildren.size(); i++) {
					List<WeakReference<MOB>> V = instanceChildren.elementAt(i).mobs;
					for (Iterator<WeakReference<MOB>> vi = V.iterator(); vi
							.hasNext();)
						if (msg.source() == vi.next().get()) {
							myDex = i;
							break;
						}
				}
				Set<MOB> grp = msg.source().getGroupMembers(new HashSet<MOB>());
				for (int i = 0; i < instanceChildren.size(); i++) {
					if (i != myDex) {
						List<WeakReference<MOB>> V = instanceChildren
								.elementAt(i).mobs;
						for (int v = V.size() - 1; v >= 0; v--) {
							final WeakReference<MOB> wmob = V.get(v);
							if (wmob == null)
								continue;
							final MOB M = wmob.get();
							if (grp.contains(M)) {
								if (myDex < 0) {
									myDex = i;
									break;
								} else if ((CMLib.flags().isInTheGame(M, true))
										&& (M.location().getArea() != instanceChildren
												.elementAt(i).A)) {
									V.remove(M);
									instanceChildren.get(myDex).mobs
											.add(new WeakReference<MOB>(M));
								}
							}
						}
					}
				}
				Area redirectA = null;
				int direction = CMLib.map().getRoomDir(msg.source().location(),
						(Room) msg.target());
				if ((direction < 0) && (msg.tool() instanceof Exit))
					direction = CMLib.map().getExitDir(msg.source().location(),
							(Exit) msg.target());
				if (direction < 0) {
					msg.source().tell(
							"Can't figure out where you're coming from?!");
					return false;
				}
				if (myDex < 0) {
					final StdAutoGenInstance newA = (StdAutoGenInstance) this
							.copyOf();
					newA.properRooms = new STreeMap<String, Room>(
							new Area.RoomIDComparator());
					newA.properRoomIDSet = null;
					newA.metroRoomIDSet = null;
					newA.blurbFlags = new STreeMap<String, String>();
					newA.setName((++instanceCounter) + "_" + Name());
					newA.flags |= Area.FLAG_INSTANCE_CHILD;
					Set<MOB> myGroup = msg.source().getGroupMembers(
							new HashSet<MOB>());
					StringBuffer xml = Resources.getFileResource(
							getGeneratorXmlPath(), true);
					if ((xml == null) || (xml.length() == 0)) {
						msg.source()
								.tell("Unable to load this area.  Please try again later.");
						return false;
					}
					List<XMLLibrary.XMLpiece> xmlRoot = CMLib.xml()
							.parseAllXML(xml);
					Hashtable definedIDs = new Hashtable();
					CMLib.percolator().buildDefinedIDSet(xmlRoot, definedIDs);
					String idName = "";
					List<String> idChoices = new Vector<String>();
					for (String key : getAutoGenVariables().keySet())
						if (key.equalsIgnoreCase("AREA_ID")
								|| key.equalsIgnoreCase("AREA_IDS")
								|| key.equalsIgnoreCase("AREAID")
								|| key.equalsIgnoreCase("AREAIDS"))
							idChoices.addAll(CMParms.parseCommas(
									getAutoGenVariables().get(key), true));
					// TODO: fix this
					// FIXME: this can select an area tag with inserts that
					// don't resolve to isolated (real) areas.
					if (idChoices.size() == 0) {
						for (Object key : definedIDs.keySet()) {
							Object val = definedIDs.get(key);
							if ((key instanceof String)
									&& (val instanceof XMLLibrary.XMLpiece)
									&& (((XMLLibrary.XMLpiece) val).tag
											.equalsIgnoreCase("area")))
								idChoices.add((String) key);
						}
					}
					if (idChoices.size() > 0)
						idName = idChoices
								.get(CMLib.dice().roll(1, idChoices.size(), -1))
								.toUpperCase().trim();

					if ((!(definedIDs.get(idName) instanceof XMLLibrary.XMLpiece))
							|| (!((XMLLibrary.XMLpiece) definedIDs.get(idName)).tag
									.equalsIgnoreCase("area"))) {
						msg.source()
								.tell("The area id '"
										+ idName
										+ "' has not been defined in the data file.");
						return false;
					}
					final ScriptingEngine scrptEng = (ScriptingEngine) CMClass
							.getCommon("DefaultScriptingEngine");
					final Object[] scriptObjs = new Object[ScriptingEngine.SPECIAL_NUM_OBJECTS];
					final List<Double> levels = new ArrayList<Double>();
					final Set<MOB> followers = msg.source().getGroupMembers(
							new HashSet<MOB>());
					if (!followers.contains(msg.source()))
						followers.add(msg.source());
					double totalLevels = 0.0;
					for (final MOB M : followers) {
						final Double D = Double.valueOf(M.basePhyStats()
								.level());
						levels.add(D);
						totalLevels += D.doubleValue();
					}
					final Double[] sortedLevels = levels.toArray(new Double[0]);
					final double lowestLevel = sortedLevels[0].doubleValue();
					final double medianLevel = sortedLevels[(int) Math
							.round(Math.floor(sortedLevels.length / 2))]
							.doubleValue();
					final double averageLevel = Math.round(10.0 * totalLevels
							/ (sortedLevels.length)) / 10.0;
					final double highestLevel = sortedLevels[sortedLevels.length - 1]
							.doubleValue();
					final double groupSize = Double.valueOf(followers.size())
							.doubleValue();
					final double values[] = {
							msg.source().basePhyStats().level(), lowestLevel,
							medianLevel, averageLevel, highestLevel,
							totalLevels, groupSize };
					for (final String key : getAutoGenVariables().keySet())
						if (!(key.equalsIgnoreCase("AREA_ID")
								|| key.equalsIgnoreCase("AREA_IDS")
								|| key.equalsIgnoreCase("AREAID") || key
									.equalsIgnoreCase("AREAIDS"))) {
							final String rawValue = CMath.replaceVariables(
									getAutoGenVariables().get(key), values);
							final String val = scrptEng.varify(msg.source(),
									newA, msg.source(), msg.source(), null,
									null, msg.sourceMessage(), scriptObjs,
									rawValue);
							definedIDs.put(key.toUpperCase(), val);
						}
					definedIDs.put("AREANAME", Name());
					if (!definedIDs.containsKey("AREASIZE"))
						definedIDs.put("AREASIZE", "50");
					if (!definedIDs.containsKey("LEVEL_RANGE"))
						definedIDs.put("LEVEL_RANGE", (msg.source()
								.basePhyStats().level() - 3)
								+ "?"
								+ (msg.source().basePhyStats().level() + 3));
					if (!definedIDs.containsKey("AGGROCHANCE"))
						definedIDs.put("AGGROCHANCE", ""
								+ msg.source().basePhyStats().level());
					XMLLibrary.XMLpiece piece = (XMLLibrary.XMLpiece) definedIDs
							.get(idName);
					// TODO: fix this
					// FIXME: this can maybe pick town.
					if (!definedIDs.containsKey("THEME")) {
						Map<String, String> unfilled = CMLib.percolator()
								.getUnfilledRequirements(definedIDs, piece);
						List<String> themes = CMParms.parseCommas(
								unfilled.get("THEME"), true);
						if (themes.size() > 0)
							definedIDs.put(
									"THEME",
									themes.get(
											CMLib.dice().roll(1, themes.size(),
													-1)).toUpperCase().trim());
					}
					try {
						CMLib.percolator().checkRequirements(piece, definedIDs);
					} catch (CMException cme) {
						msg.source().tell(
								"Required ids for " + idName
										+ " were missing: " + cme.getMessage());
						return false;
					}
					for (MOB M : myGroup)
						M.tell("^x------------------------------------------------------\n\r"
								+ "Preparing to enter "
								+ Name()
								+ ", please stand by...\n\r"
								+ "------------------------------------------------------^N^.");
					try {
						piece = CMLib.percolator().processLikeParm("AREA",
								piece, definedIDs);
						if (!CMLib.percolator().fillInArea(piece, definedIDs,
								newA, direction)) {
							msg.source()
									.tell("Failed to enter the new area.  Try again later.");
							return false;
						}
					} catch (CMException cme) {
						Log.errOut("StdAutoGenInstance", cme);
						msg.source()
								.tell("Failed to finish entering the new area.  Try again later.");
						return false;
					}
					redirectA = newA;
					CMLib.map().addArea(newA);
					newA.setAreaState(Area.State.ACTIVE); // starts ticking
					final List<WeakReference<MOB>> newMobList = new SVector<WeakReference<MOB>>(
							5);
					newMobList.add(new WeakReference<MOB>(msg.source()));
					final AreaInstanceChild child = new AreaInstanceChild(
							redirectA, newMobList);
					instanceChildren.add(child);

					getAreaIStats(); // if this is the first child ever, this
										// will force stat making

					Room R = redirectA.getRoom(redirectA.Name() + "#0");
					if (R != null) {
						Exit E = R.getExitInDir(Directions
								.getOpDirectionCode(direction));
						if (E == null)
							E = CMClass.getExit("Open");
						int opDir = Directions.getOpDirectionCode(direction);
						if (R.getRoomInDir(opDir) != null)
							msg.source()
									.tell("An error has caused the following exit to be one-way.");
						else {
							R.setRawExit(opDir, E);
							R.rawDoors()[opDir] = msg.source().location();
						}
					}
				} else
					redirectA = instanceChildren.get(myDex).A;
				if (redirectA instanceof StdAutoGenInstance) {
					Room R = redirectA.getRoom(redirectA.Name() + "#0");
					if (R != null) {
						msg.setTarget(R);
					}
				}
			}
		}
		return true;
	}

	private final static String[] MYCODES = { "GENERATIONFILEPATH", "OTHERVARS" };

	public String getStat(String code) {
		if (CMParms.indexOfIgnoreCase(STDAREACODES, code) >= 0)
			return super.getStat(code);
		else
			switch (getCodeNum(code)) {
			case 0:
				return this.getGeneratorXmlPath();
			case 1:
				return CMParms.toStringEqList(this.getAutoGenVariables());
			default:
				break;
			}
		return "";
	}

	public void setStat(String code, String val) {
		if (CMParms.indexOfIgnoreCase(STDAREACODES, code) >= 0)
			super.setStat(code, val);
		else
			switch (getCodeNum(code)) {
			case 0:
				setGeneratorXmlPath(val);
				break;
			case 1:
				setAutoGenVariables(val);
				break;
			default:
				break;
			}
	}

	protected int getCodeNum(String code) {
		for (int i = 0; i < MYCODES.length; i++)
			if (code.equalsIgnoreCase(MYCODES[i]))
				return i;
		return -1;
	}

	private static String[] codes = null;

	public String[] getStatCodes() {
		if (codes != null)
			return codes;
		String[] MYCODES = CMProps.getStatCodesList(StdAutoGenInstance.MYCODES,
				this);
		String[] superCodes = STDAREACODES;
		codes = new String[superCodes.length + MYCODES.length];
		int i = 0;
		for (; i < superCodes.length; i++)
			codes[i] = superCodes[i];
		for (int x = 0; x < MYCODES.length; i++, x++)
			codes[i] = MYCODES[x];
		return codes;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof StdAutoGenInstance))
			return false;
		String[] codes = getStatCodes();
		for (int i = 0; i < codes.length; i++)
			if (!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}

	public String getGeneratorXmlPath() {
		return filePath;
	}

	public Map<String, String> getAutoGenVariables() {
		return varMap;
	}

	public void setGeneratorXmlPath(String path) {
		filePath = path;
	}

	public void setAutoGenVariables(Map<String, String> vars) {
		varMap = vars;
	}

	public void setAutoGenVariables(String vars) {
		setAutoGenVariables(CMParms.parseEQParms(vars));
	}
}
