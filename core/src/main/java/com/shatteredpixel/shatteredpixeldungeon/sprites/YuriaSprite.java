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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Thymor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Yuria;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CourtParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.CitadelBossLevel;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class YuriaSprite extends MobSprite {
	private static int n = 16;

	public Animation cast;

	public YuriaSprite() {
		super();
		
		texture( Assets.Sprites.COURT );
		
		TextureFilm frames = new TextureFilm( texture, 20, 20 );

		idle = new Animation( 2, true );
		idle.frames( frames, 0+n, 0+n, 0+n, 1+n );

		run = new Animation( 5, true );
		run.frames( frames, 2+n, 3+n );

		attack = new Animation( 10, false );
		attack.frames( frames, 4+n, 5+n, 5+n, 6+n);

		die = new Animation( 6, false );
		die.frames( frames, 7+n, 8+n, 9+n, 10+n, 11+n, 12+n, 13+n, 14+n, 15+n );

		cast = attack.clone();

		zap = attack.clone();
		
		play( idle );
	}


	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( zap );

		MagicMissile.boltFromChar( parent,
				MagicMissile.SHAMAN_PURPLE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((Yuria)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	private Emitter ChargeParticles;

	@Override
	public void link(Char ch) {
		super.link(ch);

		ChargeParticles = emitter();
		ChargeParticles.autoKill = false;
		ChargeParticles.pour(CourtParticle.YURIA, 0.1f);

		ChargeParticles.on = false;

		CheckParticles();
	}

	@Override
	public void update() {
		super.update();

		if (ChargeParticles != null){
			CheckParticles();
			ChargeParticles.visible = visible;
		}



	}

	private void CheckParticles()
	{
		CitadelBossLevel l = (CitadelBossLevel) Dungeon.level;
		Thymor thym = (Thymor) l.boss;

		if ((  thym.TYCombo || thym.YWCombo || thym.TripleCombo  )  ){
			ChargeParticles.on = true;
		}
		else ChargeParticles.on = false;

	}

}
