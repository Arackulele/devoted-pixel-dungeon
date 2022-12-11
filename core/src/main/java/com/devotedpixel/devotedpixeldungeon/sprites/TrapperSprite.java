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

public class TrapperSprite extends MobSprite {
	
	private int cellToAttack;
	
	public TrapperSprite() {
		super();
		
		texture( Assets.Sprites.TRAPPER );
		
		TextureFilm frames = new TextureFilm( texture, 12, 16 );

		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );

		run = new Animation( 12, true );
		run.frames( frames, 2, 3, 4, 5, 6, 7 );

		attack = new Animation( 12, false );
		attack.frames( frames, 0, 8, 9 );

		zap = attack.clone();


		die = new Animation( 15, false );
		die.frames( frames, 0, 10, 11, 12, 13 );

		play( idle );
}

	
	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent( cell, ch.pos )) {
			
			cellToAttack = cell;
			turnTo( ch.pos , cell );
			play( zap );
			
		} else {
			
			super.attack( cell );
			
		}
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
			
			((MissileSprite)parent.recycle( MissileSprite.class )).
			reset( this, cellToAttack, new TrapperShot(), new Callback() {
				@Override
				public void call() {
					ch.onAttackComplete();
				}
			} );
		} else {
			super.onComplete( anim );
		}
	}
	
	public class TrapperShot extends Item {
		{
			image = ItemSpriteSheet.BOLAS;
		}
	}
}
