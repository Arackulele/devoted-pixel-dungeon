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

public class ConjurerSprite extends MobSprite {

	public ConjurerSprite() {
		super();
		
		texture( Assets.Sprites.LEADER );

		int c = 13;
		
		TextureFilm frames = new TextureFilm( texture, 12, 18 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0+c, 0+c, 0+c, 1+c, 1+c, 1+c );
		
		run = new Animation( 6, true );
		run.frames( frames, 0+c, 2+c, 3+c, 4+c );
		
		attack = new Animation( 12, false );
		attack.frames( frames, 0+c, 5+c, 6+c, 7+c, 8+c );
		
		die = new Animation( 15, false );
		die.frames( frames, 0+c, 9+c, 10+c, 11+c, 12+c );
		
		play( idle );
	}
	

	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
}
