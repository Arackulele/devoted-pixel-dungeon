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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThornLasherSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ThornLasher extends Mob {

	{
		spriteClass = ThornLasherSprite.class;
		
		HP = HT = 24;
		defenseSkill = 4;
		baseSpeed = 0.5f;

		maxLvl = 7;
	}

	private boolean attackswitch = false;
	private int counter;

	@Override
	public float attackDelay() {
		float amount = 0.5f;

		if (attackswitch==false) {
			if (counter<1) {
				attackswitch = true;
				counter = 0;
				amount = 0.5f;
			}
		}
		else {
			attackswitch = false;
			amount = 1f;
		}
		return super.attackDelay()*amount;
		}



	@Override
	public int damageRoll() {

		if (attackswitch=true) {
			return Random.NormalIntRange(2, 7);
		}
		else return Random.NormalIntRange(0, 4);
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
