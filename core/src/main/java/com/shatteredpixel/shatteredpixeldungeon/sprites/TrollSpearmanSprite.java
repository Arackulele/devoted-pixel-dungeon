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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.TrollSpearman;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class TrollSpearmanSprite extends MobSprite {

	private Animation cast;

	public TrollSpearmanSprite() {
		super();
		
		texture( Assets.Sprites.TROLLSPEARMAN );
		
		TextureFilm frames = new TextureFilm( texture, 18, 17 );

		idle = new Animation( 1, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 1, 1 );

		run = new Animation( 8, true );
		run.frames( frames, 5, 6, 7, 6 );

		attack = new Animation( 13, false );
		attack.frames( frames, 2, 2, 2, 3, 4 );

		cast = attack.clone();

		die = new Animation( 3, false );
		die.frames( frames, 8, 9, 10 );

		play( idle );
	}
	public void zap( int cell ) {

		super.zap( cell );

		((MissileSprite)parent.recycle( MissileSprite.class )).
				reset( this, cell, new RangerShot(), new Callback() {
					@Override
					public void call() {
						ch.onAttackComplete();
						((TrollSpearman)ch).lavaspear(((TrollSpearman)ch).spearPos, true);
					}
				} );

		play( cast );
		turnTo( ch.pos , cell );
	}


	public class RangerShot extends Item {
		{
			image = ItemSpriteSheet.THROWSPEAR;
		}
	}

}
