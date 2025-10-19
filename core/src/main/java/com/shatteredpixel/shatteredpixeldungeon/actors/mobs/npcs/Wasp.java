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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WaspSprite;
import com.watabou.utils.Random;

public class Wasp extends NPC {
	
	{
		viewDistance = 4;
		alignment = Char.Alignment.ALLY;
		state = WANDERING;
		intelligentAlly = true;
		spriteClass = WaspSprite.class;

		if (Dungeon.hero != null) HP = HT = 15 + (int)(Dungeon.hero.lvl * 3f);
		else HP = HT = 5;

		defenseSkill = 8;

		//before other mobs
		actPriority = Actor.MOB_PRIO + 1;
		flying = true;
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
		return Random.NormalIntRange( 6 + (Dungeon.hero.lvl / 3 ), 8 + (int)(Dungeon.hero.lvl / 1.2f ));
	}

	@Override
	public int attackSkill( Char target ) {
		return 15 + Dungeon.hero.lvl;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 5);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int(2) == 0) {
			int duration = Random.IntRange(3 + (Dungeon.hero.lvl / 1 ), 3 + (Dungeon.hero.lvl / 3 ));
			Buff.affect(enemy, Poison.class).set(duration);
		}

		return damage;
	}


	
}
