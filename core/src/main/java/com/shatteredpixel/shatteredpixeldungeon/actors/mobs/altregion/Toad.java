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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ToadSprite;
import com.watabou.utils.Random;

public class Toad extends Mob {

	{
		spriteClass = ToadSprite.class;

        HP = HT = 7;
        defenseSkill = 2;
        baseSpeed = 1f;

		maxLvl = 5;
	}

    private int moving = 2;

	@Override
	protected boolean getCloser( int target ) {
        if (moving > 0) {
            baseSpeed = Float.POSITIVE_INFINITY;
            moving--;
            return super.getCloser(target);
        } else {
            baseSpeed = 1f;
            spend(speed());
            moving = 2;
            return true;
        }
    }

    @Override
    public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        //People were complaining about the Toad sometimes moving once instead of twice
        //This happened because it could store a single movement charge when it spent the turn attacking
        //Here we remove any stored charges when it enteres an attacking state, but give it a small
        //damage bonus for them, so they are not wasted, logically idk how this makes sense maybe if it hits you in the middle
        //of the jump its like a strong headbutt
        if (moving > 0) dmgBonus = moving;
        moving = 0;
        return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
    }

    @Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        return 7;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}


}
