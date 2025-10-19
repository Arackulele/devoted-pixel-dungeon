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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BarfParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((BarfParticle)emitter.recycle( BarfParticle.class )).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return false;
		}
	};

	public BarfParticle() {
		super();

		lifespan = 3f;

		ArrayList<Integer> cols = new ArrayList<Integer>();
		cols.add(0x176321);
		cols.add(0x358f14);
		cols.add(0x587a07);
		Random.shuffle(cols);

		color( cols.get(0) );
	}

	public void reset( float x, float y){
		revive();

		this.x = x;
		this.y = y;

		left = lifespan;
		size = Random.Float( 8, 11 );

		speed.set( 0, Random.Float( 0.1f, 1.2f ) );
	}

	@Override
	public void update() {
		super.update();

		am = 1 - left / lifespan;
	}

}
