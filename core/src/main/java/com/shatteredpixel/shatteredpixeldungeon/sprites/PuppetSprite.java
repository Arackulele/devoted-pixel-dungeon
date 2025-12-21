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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Banshee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class PuppetSprite extends MobSprite {

	private Animation cast;

    public int c = 0;

	public PuppetSprite() {
		super();
		
		texture( Assets.Sprites.PUPPET );
		
		TextureFilm frames = new TextureFilm( texture, 16, 24 );
		
		idle = new Animation( 3, true );
		idle.frames( frames, 0+c, 0+c, 1+c, 1+c, 2+c, 2+c, 1+c, 1+c, 0+c, 0+c );
		
		run = new Animation( 5, true );
		run.frames( frames, 4+c, 5+c );
		
		attack = new Animation( 9, false );
		attack.frames( frames, 3+c, 4+c, 5+c, 4+c, 3+c );

        cast = new Animation( 15, false );
        cast.frames( frames, 3+c, 4+c, 5+c, 4+c );
		
		die = new Animation( 6, false );
		die.frames( frames, 6+c, 7+c, 8+c, 9+c, 10+c, 11+c, 12+c, 13+c );
		
		play( idle );
	}

	public void zap( int cell ) {

		((MissileSprite)parent.recycle( MissileSprite.class )).
				reset( this, cell, new PuppetShot(), new Callback() {
					@Override
					public void call() {
                        ((Puppet)ch).onZapComplete(cell);
					}
				} );

		play( cast );
		turnTo( ch.pos , cell );
	}



	public class PuppetShot extends Item {
		{
			image = ItemSpriteSheet.PEARL;
		}
	}

}
