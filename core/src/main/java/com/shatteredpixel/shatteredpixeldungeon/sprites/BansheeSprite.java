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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Banshee;
import com.watabou.noosa.TextureFilm;

public class BansheeSprite extends MobSprite {

	public BansheeSprite() {
		super();
		
		texture( Assets.Sprites.BANSHEE );
		
		TextureFilm frames = new TextureFilm( texture, 12, 19 );
		
		idle = new Animation( 5, true );
		idle.frames( frames, 0, 0, 1, 1, 2, 2, 2, 1, 1 );
		
		run = new Animation( 5, true );
		run.frames( frames, 8, 9, 10, 9, 8 );
		
		attack = new Animation( 9, false );
		attack.frames( frames, 3, 4, 5, 6, 6, 7 );
		
		die = new Animation( 3, false );
		die.frames( frames, 11, 12, 13);

		zap = attack.clone();
		
		play( idle );
	}
	
	@Override
	public void die() {
		super.die();
		if (Dungeon.level.heroFOV[ch.pos]) {
			emitter().burst( Speck.factory( Speck.LIGHT ), 6 );
		}
	}

	public void zap( int cell ) {

		super.zap( cell );

		MagicMissile.boltFromChar( parent,
				MagicMissile.BANSHEE,
				this,
				cell,
				new com.watabou.utils.Callback() {
					@Override
					public void call() {
						((Banshee)ch).onZapComplete();
					}
				} );
		com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.WHAIL );
	}
	
	@Override
	public int blood() {
		return 0xFFcccccc;
	}
}
