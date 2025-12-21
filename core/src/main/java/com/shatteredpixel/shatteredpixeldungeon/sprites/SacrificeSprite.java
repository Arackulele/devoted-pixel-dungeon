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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Sacrifice;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class SacrificeSprite extends MobSprite {


	public SacrificeSprite() {
		super();
		
		texture( Assets.Sprites.SACRIFICE );
		
		TextureFilm frames = new TextureFilm( texture, 17, 23 );
		
		idle = new Animation( 3, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0 );
		
		run = new Animation( 5, true );
		run.frames( frames, 2, 3, 4, 3);
		
		attack = new Animation( 9, false );
		attack.frames( frames, 5, 6, 7, 8 );
		
		die = new Animation( 10, false );
		die.frames( frames, 9, 10, 11, 12 );

		zap = attack.clone();
		
		play( idle );
	}

	public void zap( int cell ) {

		super.zap( cell );

		MagicMissile.boltFromChar( parent,
				MagicMissile.SACRIFICE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((Sacrifice)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, 0.8f );

	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public void die() {
		super.die();
		emitter().burst( Speck.factory( Speck.RATTLE ), 6 );
	}
}
