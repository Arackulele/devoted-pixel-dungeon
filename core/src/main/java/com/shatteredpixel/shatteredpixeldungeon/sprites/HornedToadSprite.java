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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class HornedToadSprite extends MobSprite {

	public HornedToadSprite() {
		super();

		texture( Assets.Sprites.TOAD );

		TextureFilm frames = new TextureFilm( texture, 17, 19 );

		idle = new Animation( 3, true );
		idle.frames( frames, 16, 16, 16, 16, 17, 17 );

		run = new Animation( 6, true );
		run.frames( frames, 18, 19, 20, 20 );

		attack = new Animation( 7, false );
		attack.frames( frames, 20, 21 );

		die = new Animation( 6, false );
		die.frames( frames, 21, 22, 23, 23, 23 );

		play( idle );
	}
}
