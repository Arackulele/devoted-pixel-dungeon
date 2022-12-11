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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

import java.util.ArrayList;
public class CoalElementalSprite extends MobSprite {

	private Emitter particles;

	private Animation pump;
	private Animation pumpAttack;

	private Emitter spray;
	private ArrayList<Emitter> pumpUpEmitters = new ArrayList<>();

	public CoalElementalSprite() {
		super();


		texture( Assets.Sprites.COALELEMENTAL );

		TextureFilm frames = new TextureFilm( texture, 12, 15 );


		idle = new Animation( 10, true );
		idle.frames( frames, 0, 1, 2 );

		run = new Animation( 12, true );
		run.frames( frames, 0, 1, 3 );

		attack = new Animation( 15, false );
		attack.frames( frames, 4, 5, 6 );

		pump = run.clone();

		pumpAttack = attack.clone();

		die = new Animation( 15, false );
		die.frames( frames, 7, 8, 9, 10, 11, 12, 13, 12 );

		play( idle );


	}

	public void pumpUp( int warnDist ) {
		if (warnDist == 0){
			clearEmitters();
		} else {
			Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, warnDist == 1 ? 0.8f : 1f );
			for (int i = 0; i < Dungeon.level.length(); i++){
				if (ch.fieldOfView != null && ch.fieldOfView[i]
						&& Dungeon.level.distance(i, ch.pos) <= warnDist
						&& new Ballistica( ch.pos, i, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == i
						&& new Ballistica( i, ch.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == ch.pos){
					Emitter e = CellEmitter.get(i);
					e.pour(SmokeParticle.FACTORY, 0.1f);
					pumpUpEmitters.add(e);
				}
			}
		}
	}

	public void clearEmitters(){
		for (Emitter e : pumpUpEmitters){
			e.on = false;
		}
		pumpUpEmitters.clear();
	}

	public void triggerEmitters(){
		for (Emitter e : pumpUpEmitters){
			e.burst(FlameParticle.FACTORY, 10);
		}
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
		pumpUpEmitters.clear();
	}

	public void pumpAttack() { play(pumpAttack); }

	@Override
	public void play(Animation anim) {
		if (anim != pump && anim != pumpAttack){
			clearEmitters();
		}
		super.play(anim);
	}


	protected Emitter createEmitter() {
		Emitter emitter = emitter();
		emitter.pour( FlameParticle.FACTORY, 0.04f );
		return emitter;
	}

	@Override
	public int blood() {
		return 0xFFFFBB33;
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


	@Override
	public void onComplete( Animation anim ) {
		super.onComplete(anim);

		if (anim == pumpAttack) {

			triggerEmitters();

			idle();
			ch.onAttackComplete();
		}
	}



}
