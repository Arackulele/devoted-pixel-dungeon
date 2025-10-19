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
import com.shatteredpixel.shatteredpixeldungeon.sprites.EmperorButterflySprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.watabou.utils.Random;

public class EmperorButterfly extends Butterfly {

	{
		spriteClass = EmperorButterflySprite.class;

		HP = HT = 30;
		defenseSkill = 6;

		properties.add(Char.Property.MINIBOSS);
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
	}


	protected class Wandering extends Mob.Wandering {
		@Override
		protected int randomDestination() {
			//of two potential wander positions, picks the one closest to the hero
			int pos1 = super.randomDestination();
			int pos2 = super.randomDestination();
			com.watabou.utils.PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
			if (com.watabou.utils.PathFinder.distance[pos2] < com.watabou.utils.PathFinder.distance[pos1]){
				return pos2;
			} else {
				return pos1;
			}
		}
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 3);
	}


}
