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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PrimeRibSprite;
import com.watabou.utils.Random;

public class PrimeRib extends MeatRack {

	public Item item;
	
	{
		spriteClass = PrimeRibSprite.class;
		
		HP = HT = 20;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 11;

		loot = Random.oneOf(Blandfruit.class, FrozenCarpaccio.class, MeatPie.class);
		lootChance = 1f; //initially, see lootChance()


		properties.add(Char.Property.UNDEAD);
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 6 );
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.333f;
	}


	@Override
	public float lootChance() {
		return super.lootChance();
	}

	@Override
	public Item createLoot() {
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
		return 14;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 3);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (Random.Int(2) == 1 && enemy == Dungeon.hero) steal((Hero)enemy);

		return damage;
	}



}
