package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
@SuppressWarnings("rawtypes")
public class Spell_MysticShine extends Spell {
	public String ID() {
		return "Spell_MysticShine";
	}

	public String name() {
		return "Mystic Shine";
	}

	public String displayText() {
		return "(Mystic Shine)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if (!(affected instanceof Room))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_LIGHTSOURCE);
		if (CMLib.flags().isInDark(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					- PhyStats.IS_DARK);
	}

	public void unInvoke() {
		// undo the affects of this spell
		Room room = CMLib.map().roomLocation(affected);
		if ((canBeUninvoked()) && (room != null))
			room.showHappens(CMMsg.MSG_OK_VISUAL, affected,
					"The gleam upon <S-NAME> dims.");
		super.unInvoke();
		if ((canBeUninvoked()) && (room != null))
			room.recoverRoomStats();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null) {
			return false;
		}
		if ((!(target instanceof Item))
				|| (((((Item) target).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL) && ((((Item) target)
						.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL))) {
			mob.tell("This magic only affects metallic items.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		final Room room = mob.location();
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "^S<T-NAME> begin(s) to really shine!"
									: "^S<S-NAME> cause(s) the surface of <T-NAME> to mystically shine!^?");
			if (room.okMessage(mob, msg)) {
				room.send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
				room.recoverRoomStats(); // attempt to handle followers
			}
		} else
			beneficialWordsFizzle(mob, mob.location(),
					"<S-NAME> attempt(s) to cause shininess, but fail(s).");

		return success;
	}
}
