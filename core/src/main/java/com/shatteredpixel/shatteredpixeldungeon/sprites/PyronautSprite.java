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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.TextureFilm;

public class PyronautSprite extends MobSprite {

	public PyronautSprite() {
		super();

		renderShadow = false;
		perspectiveRaise = 0.2f;
		
		texture( Assets.Sprites.PYRONAUT );
		
		TextureFilm frames = new TextureFilm( texture, 15, 23 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 5, 4, 3 );
		
		run = new Animation( 8, true );
		run.frames( frames, 0, 1, 2, 1 );
		
		attack = new Animation( 8, false );
		attack.frames( frames,6, 7, 8, 9 );

		zap = attack.clone();
		
		die = new Animation( 3, false );
		die.frames( frames, 10, 11, 12 );
		
		play( idle );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		renderShadow = false;
	}

	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim );
		
		if (anim == attack) {
			GameScene.ripple( ch.pos );
		}
	}
}
