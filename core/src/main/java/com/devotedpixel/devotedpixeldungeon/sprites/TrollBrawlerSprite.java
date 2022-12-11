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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Callback;

public class TrollBrawlerSprite extends MobSprite {

	private Animation cast;

	public TrollBrawlerSprite() {
		super();
		
		texture( Assets.Sprites.TROLLBRAWLER );
		
		TextureFilm frames = new TextureFilm( texture, 12, 16 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );

		run = new Animation( 9, true );
		run.frames( frames, 2, 3 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 4, 5 );

		cast = attack.clone();

		die = new Animation( 8, false );
		die.frames( frames, 6, 7, 8 );
		
		play( idle );
	}

	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( cast );

		if (Dungeon.hero.belongings.thrownWeapon != null && Dungeon.hero.belongings.thrownWeapon instanceof MissileWeapon) {

			MissileWeapon wep = (MissileWeapon)Dungeon.hero.belongings.thrownWeapon;


			((MissileSprite) parent.recycle(MissileSprite.class)).
					reset(this, cell, wep, new Callback() {
						@Override
						public void call() {
							((com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrollBrawler)ch).onZapComplete();
						}
					});
		}
		play( cast );
		turnTo( ch.pos , cell );
	}

}
