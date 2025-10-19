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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class TrollRangerSprite extends MobSprite {

	private Animation cast;

	public TrollRangerSprite() {
		super();
		
		texture( Assets.Sprites.TROLLRANGER );
		
		TextureFilm frames = new TextureFilm( texture, 15, 18 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 1, 0, 0, 0 );
		
		run = new Animation( 9, true );
		run.frames( frames, 1, 3, 4, 5 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, 6, 7, 7, 8, 8 );

		cast = attack.clone();
		
		die = new Animation( 2, false );
		die.frames( frames, 9, 10, 11 );
		
		play( idle );
	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( this, cell, new RangerShot(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					} );

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}

	public class RangerShot extends Item {
		{
			image = ItemSpriteSheet.ARROW;
		}
	}

}
