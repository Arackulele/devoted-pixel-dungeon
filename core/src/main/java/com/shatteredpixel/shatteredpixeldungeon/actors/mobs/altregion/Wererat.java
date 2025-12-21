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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WereratSprite;
import com.watabou.utils.Random;

public class Wererat extends Mob {
	
	{
		spriteClass = WereratSprite.class;
		
		HP = HT = 40;
		defenseSkill = 14;
		
		EXP = 7;
		maxLvl = 14;
		
		loot = MysteryMeat.class;
		lootChance = 0.3f; //see lootChance()

		HUNTING = new Hunting();

	}


	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 9 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 5);
	}
	
	@Override
	public float lootChance() {
		return super.lootChance() * ((6f - Dungeon.LimitedDrops.NECRO_HP.count) / 6f);
	}
	
	@Override
	public Item createLoot(){
		Dungeon.LimitedDrops.NECRO_HP.count++;
		return super.createLoot();
	}

	public void DashToEnemy(){

		Ballistica route = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
		int cell = route.path.get(route.dist-1 );
		if (cell != pos && Dungeon.level.passable[cell] && Actor.findChar(cell) == null && Dungeon.hero.pos != cell) {
			com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.MISS);
			sprite.emitter().start(Speck.factory(Speck.DUST), 0.01f, Math.round(4 + 2 * Dungeon.level.trueDistance(pos, cell)));
			sprite.jump(pos, cell, 0.5f, 0.25f, new com.watabou.utils.Callback() {
				@Override
				public void call() {
					if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
						Door.leave(pos);
					}
					pos = cell;
					Dungeon.level.occupyCell(Wererat.this);
				}
			});
		}
		else sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "prepare"));
		spend(1f);

	}

	public boolean dashed;

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;

			if (	(HP > HT/2)
					&& enemy != null
					&& Dungeon.level.distance( pos, enemy.pos ) > 2
					&& enemySeen
					&& !isCharmedBy( enemy )
					&& !canAttack( enemy )
			)
			{
				DashToEnemy();
				return true;
			}

			return super.act( enemyInFOV, justAlerted );

		}
	}
}