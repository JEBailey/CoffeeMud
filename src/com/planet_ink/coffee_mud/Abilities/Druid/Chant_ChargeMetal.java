package com.planet_ink.coffee_mud.Abilities.Druid;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Chant_ChargeMetal extends Chant {
	public String ID() {
		return "Chant_ChargeMetal";
	}

	public String name() {
		return "Charge Metal";
	}

	public String displayText() {
		return "(Charged)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_ITEMS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS | CAN_MOBS;
	}

	private WeakReference<CMMsg> lastMsg = null;

	protected List<Item> affectedItems = new Vector<Item>();

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		affectedItems = new Vector<Item>();
	}

	public CMObject copyOf() {
		Chant_ChargeMetal obj = (Chant_ChargeMetal) super.copyOf();
		obj.affectedItems = new Vector<Item>();
		obj.affectedItems.addAll(affectedItems);
		return obj;
	}

	public Item wieldingMetal(MOB mob) {
		for (int i = 0; i < mob.numItems(); i++) {
			Item item = mob.getItem(i);
			if ((item != null) && (!item.amWearingAt(Wearable.IN_INVENTORY))
					&& (CMLib.flags().isMetal(item))
					&& (item.container() == null) && (!mob.amDead()))
				return item;
		}
		return null;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (affected == null)
			return true;
		if (!(affected instanceof Item))
			return true;

		Item I = (Item) affected;
		if ((I.owner() == null) || (!(I.owner() instanceof MOB))
				|| (I.amWearingAt(Wearable.IN_INVENTORY)))
			return true;

		MOB mob = (MOB) I.owner();
		if ((!msg.amITarget(mob))
				&& ((msg.targetMinor() == CMMsg.TYP_ELECTRIC) || ((msg
						.sourceMinor() == CMMsg.TYP_ELECTRIC) && (msg
						.targetMinor() == CMMsg.TYP_DAMAGE)))
				&& ((lastMsg == null) || (lastMsg.get() != msg))) {
			lastMsg = new WeakReference<CMMsg>(msg);
			msg.source()
					.location()
					.show(mob, null, I, CMMsg.MSG_OK_VISUAL,
							"<O-NAME> attracts a charge to <S-NAME>!");
			if (mob.okMessage(mob, msg))
				msg.modify(msg.source(), mob, msg.tool(), msg.sourceCode(),
						msg.sourceMessage(), msg.targetCode(),
						msg.targetMessage(), msg.othersCode(),
						msg.othersMessage());
		}
		return true;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (affected == null) {
			super.unInvoke();
			return;
		}

		if (canBeUninvoked())
			if (affected instanceof MOB) {
				for (int i = 0; i < affectedItems.size(); i++) {
					Item I = affectedItems.get(i);
					Ability A = I.fetchEffect(this.ID());
					while (A != null) {
						I.delEffect(A);
						A = I.fetchEffect(this.ID());
					}

				}
			}
		super.unInvoke();
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				Item I = wieldingMetal((MOB) target);
				if (I == null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;
		Item I = null;
		if (target instanceof MOB)
			I = wieldingMetal((MOB) target);

		if ((target instanceof Item) && (CMLib.flags().isMetal(target)))
			I = (Item) target;
		else if (target instanceof Item) {
			mob.tell(target.name(mob) + " is not made of metal!");
			return false;
		}
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if ((success) && (I != null)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) upon <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0)
					success = maliciousAffect(mob, I, asLevel, 0, -1);
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
