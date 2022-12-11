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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Alchemist;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class AlchemistSprite extends MobSprite {

	private Emitter teleParticles;
	
	public AlchemistSprite() {
		super();
		
		texture( Assets.Sprites.ALCHEMIST );
		
		TextureFilm frames = new TextureFilm( texture, 16, 19 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 1 );
		
		run = new Animation( 8, true );
		run.frames( frames, 2, 3 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 4, 5, 6 );

		zap = attack.clone();
		
		die = new Animation( 15, false );
		die.frames( frames, 7, 8, 9, 10 );
		
		play( idle );
	}


	@Override
	public int blood() {
		return 0xFF80706c;
	}

	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( zap );

		((MissileSprite)parent.recycle( MissileSprite.class )).
		reset( this, cell, new ScorpioShot(), new Callback() {
			@Override
			public void call() {
				((Alchemist)ch).onZapComplete();
			}
		} );
	}

public class ScorpioShot extends Item {
	{
		image = ItemSpriteSheet.POTION_CRIMSON;
	}
}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
}
