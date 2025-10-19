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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FlameMoleSprite;

public class FlamingMole extends Mole {
	
	{
		spriteClass = FlameMoleSprite.class;

		loot = ChargrilledMeat.class;
		lootChance = 1f;

		HUNTING = new FlamingMole.Hunting();

		immunities.add(Burning.class);
	}

	@Override
	public void move( int step, boolean travelling) {

		if (digging) GameScene.add(Blob.seed(pos, 5, Fire.class));
		super.move( step, travelling);
	}

	private class Hunting extends Mob.Hunting {

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {

			if (!digging && enemyInFOV && Dungeon.level.distance(enemy.pos, pos) > 1 && Actor.findChar(pos) == null) {

				digging = true;
				((FlameMoleSprite)sprite).setSubmerge();
				com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.DIG);
				spend(1f);

				return true;
			}
			else if (digging && enemyInFOV && Dungeon.level.distance(enemy.pos, pos) < 2)
			{
				digging = false;
				((FlameMoleSprite)sprite).setEmerge();
				com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.DIG);
				spend(1f);

				for (int i = 0; i < com.watabou.utils.PathFinder.NEIGHBOURS8.length; i++) {
					CellEmitter.get(pos + com.watabou.utils.PathFinder.NEIGHBOURS8[i]).burst(FlameParticle.FACTORY, 5);
						Char ch = Actor.findChar( pos + com.watabou.utils.PathFinder.NEIGHBOURS8[i] );
						if (ch != null && ch.isAlive()) {
							Buff.affect(ch, Burning.class).reignite(ch, 2);
						}
					}


				return true;
			}

			return super.act(enemyInFOV, justAlerted);

		}
	}



}
