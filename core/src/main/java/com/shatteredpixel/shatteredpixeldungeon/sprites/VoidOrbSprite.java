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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class VoidOrbSprite extends MobSprite {

	private Emitter particles;

	protected Emitter createEmitter() {
		Emitter emitter = emitter();
		emitter.width = emitter.height = 1;
        Emitter.Factory fact;
        switch(Random.Int(4))
        {
            default:
            case 0:
                fact = CourtParticle.THYMOR;
                break;
            case 1:
                fact = CourtParticle.YURIA;
                break;
            case 2:
                fact = CourtParticle.WENDAR;
                break;

        }

		emitter.pour( fact, 0.06f );
		return emitter;
	}

	public VoidOrbSprite() {
		super();

		texture( Assets.Sprites.ROT_LASH );
		
		TextureFilm frames = new TextureFilm( texture, 12, 14 );
		
		idle = new Animation( 1, true );
		idle.frames( frames, 8 );
		
		run = attack = die = idle.clone();

		play( idle );
	}

	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		
		if (particles == null) {
			particles = createEmitter();
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (particles != null){
			particles.visible = visible;
		}
	}
	
	@Override
	public void die() {
		super.die();
		if (particles != null){
			particles.on = false;
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		if (particles != null){
			particles.killAndErase();
		}
	}
}
