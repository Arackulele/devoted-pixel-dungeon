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

public class ElderThornSprite extends MobSprite {

	public ElderThornSprite() {
		super();
		
		texture( Assets.Sprites.THORNLASHER );
		
		TextureFilm frames = new TextureFilm( texture, 13, 16 );

		int C = 17;
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0+C, 0+C, 1+C, 2+C );
		
		run = new Animation( 10, true );
		run.frames( frames, 6+C, 7+C, 8+C, 9+C, 10+C );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 3+C, 4+C, 5+C );
		
		die = new Animation( 9, false );
		die.frames( frames, 11+C, 12+C, 13+C, 14+C, 15+C, 16+C );
		
		play( idle );
	}
}
