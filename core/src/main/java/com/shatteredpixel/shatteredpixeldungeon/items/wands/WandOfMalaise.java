/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SageCorpse;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.watabou.utils.Random;

public class WandOfMalaise extends Wand {

	{
		image = ItemSpriteSheet.WAND_MALAISE;
		unique = true;
		bones = false;

		usesTargeting = false; //player usually targets wards or spaces, not enemies

	}

	@Override
	public void gainCharge( float amt ){

	}
	@Override
	public void gainCharge( float amt, boolean overcharge ){
		partialCharge = 0;

	}

	@Override
	public int collisionProperties(int target) {
		if (cursed)                                 return super.collisionProperties(target);
		else if (!Dungeon.level.heroFOV[target])    return Ballistica.PROJECTILE;
		else                                        return Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	public int initialCharges() {
		return 10;
	}

	@Override
	public boolean tryToZap(Hero owner, int target) {
		Char ch = Actor.findChar(target);
		if (ch != null && ch.getClass() == Wraith.class) return false;
		if (ch != null && ch.getClass() == SageCorpse.class) return false;

		if (ch != null &&
				(ch.buff(Invulnerability.class) != null ||
						ch.buff(Barrier.class) != null))
		{
			return false;
		}

		else return super.tryToZap(owner, target);

	}

	@Override
	public void onZap(Ballistica bolt) {

		if (curCharges < 2) QuestDone = true;

		int target = bolt.collisionPos;
		Char ch = Actor.findChar(target);
		if (ch != null){

			if (ch.alignment != Char.Alignment.ALLY) {
				Buff.affect(ch, Barrier.class).setShield((10 + buffedLvl()));
				Buff.affect(ch, Invulnerability.class, 3f);
			}
			else {
				ch.damage(Random.NormalIntRange(9, 25), this);
			}

			return;

		}

		if (!Dungeon.level.passable[target]) {
			GLog.w(Messages.get(this, "bad_location"));
			Dungeon.level.pressCell(target);
		}

			Wraith ghost = Wraith.spawnAt(target);
			ghost.HP = ghost.HT = 20;


	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		SpellSprite.show(attacker, SpellSprite.CHARGE);
		for (Wand.Charger c : attacker.buffs(Wand.Charger.class)){
			if (c.wand() != this){
				c.gainCharge(0.5f * procChanceMultiplier(attacker));
			}
		}

	}

	public boolean QuestDone = false;

	public String QUESTDONE = "QuestDone";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(QUESTDONE, QuestDone);
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		QuestDone = bundle.getBoolean(QUESTDONE);
		super.restoreFromBundle(bundle);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (QuestDone){
			return new ItemSprite.Glowing( 0xFFFF00 );
		}
		return null;
	}

}
