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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Banshee;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;

public class RevenantSprite extends MobSprite {

	public RevenantSprite() {
		super();
		
		texture( Assets.Sprites.BANSHEE );

		int c = 14;
		
		TextureFilm frames = new TextureFilm( texture, 12, 19 );
		
		idle = new Animation( 5, true );
		idle.frames( frames, 0+c, 0+c, 1+c, 1+c, 2+c, 2+c, 2+c, 1+c, 1+c );
		
		run = new Animation( 5, true );
		run.frames( frames, 8+c, 9+c, 10+c, 9+c, 8+c );
		
		attack = new Animation( 9, false );
		attack.frames( frames, 3+c, 4+c, 5+c, 6+c, 6+c, 7+c );
		
		die = new Animation( 3, false );
		die.frames( frames, 11+c, 12+c, 13+c);

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
		com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}
	
	@Override
	public int blood() {
		return 0xFFcccccc;
	}
}
