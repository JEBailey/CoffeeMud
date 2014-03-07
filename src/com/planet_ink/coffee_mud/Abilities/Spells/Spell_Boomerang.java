package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
@SuppressWarnings("rawtypes")
public class Spell_Boomerang extends Spell {
	public String ID() {
		return "Spell_Boomerang";
	}

	public String name() {
		return "Returning";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
	}

	protected MOB owner = null;

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (!super.okMessage(host, msg))
			return false;
		if ((msg.tool() == affected) && (msg.sourceMinor() == CMMsg.TYP_SELL)) {
			unInvoke();
			if (affected != null)
				affected.delEffect(this);
		}
		return true;
	}

	public MOB getOwner(Item I) {
		if (owner == null) {
			if ((I.owner() != null) && (I.owner() instanceof MOB)
					&& (I.owner().Name().equals(text())))
				owner = (MOB) I.owner();
			else
				owner = CMLib.players().getPlayer(text());
		}
		return owner;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (affected instanceof Item) {
			final Item I = (Item) affected;
			MOB owner = getOwner(I);
			if ((owner != null) && (I.owner() != null) && (I.owner() != owner)) {
				if (!owner.isMine(I)) {
					owner.tell(I.name(owner) + " returns to your inventory!");
					I.unWear();
					I.setContainer(null);
					owner.moveItemTo(I);
				} else {
					I.unWear();
					I.setContainer(null);
					owner.moveItemTo(I);
					I.setOwner(owner);
				}
			}
		}
		return (tickID != Tickable.TICKID_ITEM_BOUNCEBACK);
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		super.executeMsg(host, msg);
		if ((msg.targetMinor() == CMMsg.TYP_GET) && (msg.amITarget(affected))
				&& (text().length() == 0)) {
			setMiscText(msg.source().Name());
			msg.source()
					.tell(affected.name() + " will now return back to you.");
			makeNonUninvokable();
		}
		if ((affected instanceof Item) && (text().length() > 0)) {
			Item I = (Item) affected;
			MOB owner = getOwner(I);
			if ((owner != null) && (I.owner() != null) && (I.owner() != owner)) {
				if ((msg.sourceMinor() == CMMsg.TYP_DROP)
						|| (msg.target() == I) || (msg.tool() == I)) {
					msg.addTrailerMsg(CMClass.getMsg(owner, null,
							CMMsg.NO_EFFECT, null));
				}
			} else if (!CMLib.threads().isTicking(this, -1))
				CMLib.threads().startTickDown(this,
						Tickable.TICKID_ITEM_BOUNCEBACK, 1);
		}
	}

	// this fixes a damn PUT bug
	public void affectPhyStats(Physical affectedEnv, PhyStats stats) {
		super.affectPhyStats(affectedEnv, stats);
		if (affectedEnv instanceof Item) {
			final Item item = (Item) affectedEnv;
			if (item.container() != null) {
				final Item container = item.ultimateContainer(null);
				if ((container.amDestroyed())
						|| (container.owner() != item.owner()))
					item.setContainer(null);
			}
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							somanticCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> point(s) at <T-NAMESELF> and cast(s) a spell.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
						"<T-NAME> glows slightly!");
				mob.tell(target.name(mob)
						+ " will now await someone to GET it before acknowleding its new master.");
				setMiscText("");
				beneficialAffect(mob, target, asLevel, 0);
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}

		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> point(s) at <T-NAMESELF>, but fail(s) to cast a spell.");

		// return whether it worked
		return success;
	}
}