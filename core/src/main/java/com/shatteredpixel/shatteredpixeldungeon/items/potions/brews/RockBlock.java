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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RockBlock extends Brew {

	{
		image = ItemSpriteSheet.BLOCK;
	}

	@Override
	public void shatter(int cell) {
		com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.DIG);

        for (int i : com.watabou.utils.PathFinder.NEIGHBOURS5) {
			if (Dungeon.level.map[cell + i] == Terrain.WATER && Actor.findChar(cell + i) == null) {
				Level.set(cell + i, Terrain.EMPTY);
				CellEmitter.get(cell + i).start(Speck.factory(Speck.ROCK), 0.07f, 1);
				GameScene.updateMap(cell + i);
			}
		}

		Dungeon.level.buildFlagMaps();
	}

	@Override
	public int value() {
		return 5;
	}

	@Override
	public int energyVal() {
		return 5;
	}


}
