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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ToadSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Toad extends Mob {

	{
		spriteClass = ToadSprite.class;
		
		HP = HT = 10;
		defenseSkill = 1;
		baseSpeed = 2f;

		maxLvl = 5;
	}

	private int moving;

	@Override
	protected boolean getCloser( int target ) {
		if (moving > 1) {
			moving-=2;
			return super.getCloser( target );
		} else if (moving==1) {
			moving+=3;
			return true;
		}
		else {
			moving++;
			return true;
		}

	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 6;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}


}
