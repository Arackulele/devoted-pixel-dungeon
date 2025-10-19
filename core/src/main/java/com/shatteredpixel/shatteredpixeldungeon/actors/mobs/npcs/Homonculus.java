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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HomonculusSprite;
import com.watabou.utils.Random;

public class Homonculus extends NPC {
	
	{
		viewDistance = 6;
		alignment = Char.Alignment.ALLY;
		state = WANDERING;
		intelligentAlly = true;
		spriteClass = HomonculusSprite.class;

		if (Dungeon.hero != null) HP = HT = Dungeon.hero.HT;
		else HP = HT = 5;

		defenseSkill = 12;

		//before other mobs
		actPriority = Actor.MOB_PRIO + 1;
	}

	@Override
	protected boolean getCloser(int target) {
		if ( enemy == null && buffs(AllyBuff.class).isEmpty()) {
			target = Dungeon.hero.pos;
		}
		return super.getCloser( target );
	}

	@Override
	public int damageRoll() {
		int damage;
		if (Dungeon.hero.belongings.weapon() != null){
			damage = Dungeon.hero.belongings.weapon().damageRoll(this);
		} else {
			damage = Dungeon.hero.damageRoll(); //handles ring of force
		}
		return damage;
	}

	@Override
	public int attackSkill( Char target ) {
		return 18;
	}

	@Override
	public int drRoll() {
		int dr = super.drRoll();
		if (Dungeon.hero != null && Dungeon.hero.belongings.weapon() != null){
			return dr + Random.NormalIntRange(0, Dungeon.hero.belongings.weapon().defenseFactor(this)/2);
		} else {
			return dr;
		}
	}

	int healcooldown = 10;

	@Override
	protected boolean act() {
		boolean result = super.act();
		Hunger hunger = Buff.affect(Dungeon.hero, Hunger.class);
		if (healcooldown <= 0 && !hunger.isStarving() && HP < HT) {
			HP = Math.min(HT, HP + 1);
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(1), FloatingText.HEALING);
			healcooldown = 10;
		} else healcooldown--;
		return result;
	}


	private static String HEAL_COOLDOWN = "heal_cooldown";
	@Override
	public void storeInBundle( com.watabou.utils.Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( HEAL_COOLDOWN, healcooldown );
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		super.restoreFromBundle( bundle );
		healcooldown = bundle.getInt( HEAL_COOLDOWN );
	}
	
}
