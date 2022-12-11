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

public class WendarSprite extends MobSprite {
	private static int n = 18;
	public WendarSprite() {
		super();
		
		texture( Assets.Sprites.COURT );
		
		TextureFilm frames = new TextureFilm( texture, 15, 18 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0+n, 0+n, 0+n, 3+n );
		
		run = new Animation( 10, true );
		run.frames( frames, 0+n, 1+n, 2+n );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 3+n, 4+n, 5+n );
		
		die = new Animation( 10, false );
		die.frames( frames, 6+n, 7+n, 8+n );
		
		play( idle );
	}
}
