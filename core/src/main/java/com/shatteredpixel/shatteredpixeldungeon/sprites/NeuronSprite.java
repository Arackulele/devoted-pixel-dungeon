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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Mole;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Neuron;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class NeuronSprite extends MobSprite {

	private Animation cast;

    public int c = 0;

	public NeuronSprite() {
		super();
		
		texture( Assets.Sprites.NEURON );
		
		TextureFilm frames = new TextureFilm( texture, 15, 16 );
		
		idle = new Animation( 3, true );
		idle.frames( frames, 0+c, 0+c, 1+c, 1+c );
		
		run = new Animation( 5, true );
		run.frames( frames, 5+c, 6+c );
		
		attack = new Animation( 9, false );
		attack.frames( frames, 2+c, 3+c, 2+c, 3+c, 4+c, 7+c );

        cast = attack.clone();
		
		die = new Animation( 6, false );
		die.frames( frames, 7+c, 7+c, 8+c, 9+c, 10+c );
		
		play( idle );
	}

    public void UpdateAnimPos()
    {
		texture( Assets.Sprites.NEURON );

		TextureFilm frames = new TextureFilm( texture, 15, 16 );

		idle = new Animation( 3, true );
		idle.frames( frames, 0+c, 0+c, 1+c, 1+c );

		run = new Animation( 5, true );
		run.frames( frames, 5+c, 6+c );

		attack = new Animation( 9, false );
		attack.frames( frames, 2+c, 3+c, 2+c, 3+c, 4+c, 7+c );

		cast = attack.clone();

		die = new Animation( 6, false );
		die.frames( frames, 7+c, 7+c, 8+c, 9+c, 10+c );

		play( idle );
    }

	public void zap( int cell ) {

		super.zap( cell );

		int boltType;

		switch (((Neuron)ch).form)
		{
			default: case 0: boltType = MagicMissile.FIRE;
				break;
			case 1: boltType = MagicMissile.FROST;
				break;
			case 2: boltType = MagicMissile.LIGHT_MISSILE;
				break;
		}

		MagicMissile.boltFromChar( parent,
				boltType,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((Neuron)ch).onZapComplete(cell);
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

}
