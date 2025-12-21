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
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlueImpSprite;
import com.watabou.utils.Random;

public class BlueImp extends NPC {
	
	{
		alignment = Char.Alignment.ALLY;
		state = HUNTING;
		intelligentAlly = true;
		baseSpeed = 1.5f;

		spriteClass = BlueImpSprite.class;
		

		if (Dungeon.hero != null) HP = HT = 30 + (int)(Dungeon.hero.lvl * 2);
		else HP = HT = 5;

		defenseSkill = 8;

		//WANDERING = new Wandering();

		//before other mobs
		actPriority = Actor.MOB_PRIO + 1;

		properties.add(Char.Property.DEMONIC);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + (Wisp.GetHeroLevel() / 5 ), 4 + (Wisp.GetHeroLevel() / 2 ));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 999;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 5);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		Bomb exp = new Bomb();

		die(null);

		exp.explode(pos);

		return 0;
	}

	
}
