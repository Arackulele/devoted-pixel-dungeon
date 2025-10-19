/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollRangerSprite;
import com.watabou.utils.Random;

public class TrollRanger extends Mob {
	
	{
		spriteClass = TrollRangerSprite.class;

		HP = HT = 35;
		defenseSkill = 20;

		EXP = 8;
		maxLvl = 16;

		loot = Generator.Category.MISSILE;
		lootChance = 0.06f;
	}


	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 10, 18 );
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		if (HP < HT *0.5f) return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
		else return attack.collisionPos == enemy.pos;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		return damage;
	}

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING && HP < HT *0.5f) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	
	@Override
	public int attackSkill( Char target ) {
		return 25;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}
}
