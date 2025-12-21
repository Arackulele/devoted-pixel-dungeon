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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PyronautSprite;
import com.watabou.utils.Random;

public class Pyronaut extends Piranha {

	{
		spriteClass = PyronautSprite.class;

		baseSpeed = 0.5f;

		EXP = 7;
		maxLvl = -2;

		HP = HT = 30;
		defenseSkill = 16;

		state = SLEEPING;

		properties.add(Char.Property.INORGANIC);

		immunities.add(Burning.class);
		immunities.add(Smog.class);
	}

	public int Pyrocooldown = 1;

	@Override
	protected boolean act() {
		if (state == HUNTING || state == WANDERING){


			if (Pyrocooldown < 1 && enemy != null && enemy.distance(this) < 6)
			{
				GameScene.add( Blob.seed(pos, 500, Smog.class));
				sprite.zap(pos);
				spend(3f);
				Pyrocooldown = 30;
				return true;
			}
			else {
				Pyrocooldown--;
				return super.act();
			}

		}
		else return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (Blob.volumeAt(pos, Smog.class) > 0) dmg /= 3;
		super.damage(dmg, src);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 8, 22 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 22;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(2, 12);
	}



}
