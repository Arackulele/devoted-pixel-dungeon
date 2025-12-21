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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Thymor;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CourtParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class ThymorSprite extends MobSprite {

	public Animation cast;
	
	public ThymorSprite() {
		super();
		
		texture( Assets.Sprites.COURT );
		
		TextureFilm frames = new TextureFilm( texture, 20, 20 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );
		
		run = new Animation( 8, true );
		run.frames( frames, 2, 3 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 4, 5, 6, 6, 4);
		
		die = new Animation( 6, false );
		die.frames( frames, 7, 8, 9, 10, 11, 12, 13, 14, 15 );

		cast = attack.clone();
		
		play( idle );
	}


	private Emitter ChargeParticles;

	@Override
	public void link(Char ch) {
		super.link(ch);

		ChargeParticles = emitter();
		ChargeParticles.autoKill = false;
		ChargeParticles.pour(CourtParticle.THYMOR, 0.1f);
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

		if (ch instanceof Thymor && (  ((Thymor) ch).TYCombo || ((Thymor) ch).TWCombo || ((Thymor) ch).TripleCombo  )  ){
			ChargeParticles.on = true;
		}
		else ChargeParticles.on = false;

	}


}
