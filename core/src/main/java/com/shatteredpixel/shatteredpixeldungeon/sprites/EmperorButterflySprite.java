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

public class EmperorButterflySprite extends MobSprite {

	public EmperorButterflySprite() {
		super();

		texture( Assets.Sprites.BUTTERFLY );
		
		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		int c = 7;
		
		idle = new Animation( 8, true );
		idle.frames( frames, 0+c, 1+c );
		
		run = new Animation( 12, true );
		run.frames( frames, 0+c, 1+c );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 0+c, 3+c, 4+c );
		
		die = new Animation( 4, false );
		die.frames( frames, 4+c, 5+c, 6+c );
		
		play( idle );
	}
	
	@Override
	public int blood() {
		return 0xffaa00;
	}
}
