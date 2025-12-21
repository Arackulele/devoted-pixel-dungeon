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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class WardenSprite extends MobSprite {

	private Emitter teleParticles;

	public WardenSprite() {
		super();
		
		texture( Assets.Sprites.WARDEN );
		
		TextureFilm frames = new TextureFilm( texture, 19, 20 );
		
		idle = new Animation( 3, true );
		idle.frames( frames, 0, 1, 2, 3 );
		
		run = new Animation( 5, true );
		run.frames( frames, 7, 8, 9, 10 );
		
		attack = new Animation( 7, false );
		attack.frames( frames, 4, 5, 6 );

		zap = attack.clone();
		
		die = new Animation( 5, false );
		die.frames( frames, 11, 12, 13, 14 );
		
		play( idle );
	}

	@Override
	public int blood() {
		return 0xFF80706c;
	}


}
