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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CM01;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;
import com.watabou.utils.Callback;

public class CM01Sprite extends MobSprite {
	
	public CM01Sprite () {
		super();
		
		texture( Assets.Sprites.CM01 );
		
		TextureFilm frames = new TextureFilm( texture, 16, 14 );
		
		idle = new Animation( 1, true );
		idle.frames( frames, 0, 0, 0, 7, 1, 1, 0, 0 );

		run = new Animation( 12, true );
		run.frames( frames, 5, 6, 5, 6 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, 2, 3, 4, 0 );

		zap = new Animation( 8, false );
		zap.frames( frames, 1, 4, 5 );

		die = new Animation( 12, false );
		die.frames( frames, 7, 8, 9, 10, 11, 12, 13 );
		
		play( idle );
	}

	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( zap );

		MagicMissile.boltFromChar( parent,
				MagicMissile.BLIZZARD_BEAM,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((CM01)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.GAS );
	}

	@Override
	public void die() {
		emitter().burst( Speck.factory( Speck.WOOL ), 5 );
		super.die();
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}
