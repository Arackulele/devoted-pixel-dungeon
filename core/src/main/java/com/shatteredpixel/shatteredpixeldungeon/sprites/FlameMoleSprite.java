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
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Mole;
import com.watabou.noosa.TextureFilm;

public class FlameMoleSprite extends MobSprite {

	private com.watabou.noosa.particles.Emitter particles;

	public Animation submerge;

	public Animation emerge;

	public Animation staysubmerged;

	protected com.watabou.noosa.particles.Emitter createEmitter() {
		com.watabou.noosa.particles.Emitter emitter = emitter();
		emitter.pour( EarthParticle.FACTORY, 0.06f );
		return emitter;
	}

	public FlameMoleSprite() {
		super();

		texture( Assets.Sprites.MOLE );

		TextureFilm frames = new TextureFilm( texture, 14, 18 );

		int c = 16;

		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+0, c+0, c+0, c+1, c+0, c+0, c+1, c+2, c+1, c+2, c+1, c+0 );

		run = new Animation( 6, true );
		run.frames( frames, c+0, c+6, c+1);

		//empty frame at the end, since being in the floor is represented by particles
		submerge = new Animation( 15, false );
		submerge.frames( frames, c+6, c+7, c+8, c+9, c+10, c+11, c+15);

		staysubmerged = new Animation( 1, false );
		staysubmerged.frames( frames, c+15);

		emerge = new Animation( 15, false );
		emerge.frames( frames, c+11, c+10, c+9, c+8, c+7, c+6, c+0 );

		attack = new Animation( 10, false );
		attack.frames( frames, c+3, c+4, c+5, c+6 );

		die = new Animation( 8, false );
		die.frames( frames, c+12, c+13, c+14 );

		play( idle );
	}

	public void setSubmerge(){
		play( submerge );
		particles.on = true;
	}

	public void setEmerge(){
		play( emerge );
		particles.on = false;
		particles.revive();
	}

	@Override
	public void play(Animation anim) {
		if (anim == run && ch.speed() > 1f){

			anim = staysubmerged;

		}
		super.play(anim);
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );

		if (particles == null) {
			particles = createEmitter();
			particles.on = false;
			particles.revive();
		}

		if (((Mole)ch).digging && curAnim == idle) setSubmerge();
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
