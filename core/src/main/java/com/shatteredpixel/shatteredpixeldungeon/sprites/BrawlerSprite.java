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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.TrollBrawler;
import com.watabou.noosa.TextureFilm;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Callback;
public abstract class BrawlerSprite extends MobSprite {

	protected abstract int texOffset();

	public BrawlerSprite() {
		super();
		
		int c = texOffset();
		
		texture( Assets.Sprites.BRAWLER );
		
		TextureFilm frames = new TextureFilm( texture, 13, 19 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+1, c+2, c+2, c+1, c+1, c+0, c+0, c+0 );
		
		run = new Animation( 12, true );
		run.frames( frames, c+3, c+4, c+5 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, c+6, c+7, c+8 );

		zap = attack.clone();
		
		die = new Animation( 2, false );
		die.frames( frames, c+0, c+10, c+11 );
		
		play( idle );
	}

	
	public static class Yellow extends BrawlerSprite {
		
		@Override
		protected int texOffset() {
			return 0;
		}

		public void zap( int cell ) {

			turnTo( ch.pos , cell );
			play( zap );

			if (Dungeon.hero.belongings.thrownWeapon != null && Dungeon.hero.belongings.thrownWeapon instanceof MissileWeapon) {

				MissileWeapon wep = (MissileWeapon)Dungeon.hero.belongings.thrownWeapon;


				((MissileSprite) parent.recycle(MissileSprite.class)).
						reset(this, cell, wep, new Callback() {
							@Override
							public void call() {
								((TrollBrawler.YellowBrawler)ch).onZapComplete();
							}
						});
			}
			play( zap );
			turnTo( ch.pos , cell );
		}
	}
	
	public static class Purple extends BrawlerSprite {
		
		@Override
		protected int texOffset() {
			return 12;
		}

		public void zap( int cell ) {

			super.zap( cell );

			MagicMissile.boltFromChar( parent,
					MagicMissile.SHADOW,
					this,
					cell,
					new Callback() {
						@Override
						public void call() {
							((TrollBrawler.PurpleBrawler)ch).onZapComplete();
						}
					} );
			com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

	}
	
	public static class Red extends BrawlerSprite {
		
		@Override
		protected int texOffset() {
			return 24;
		}
	}
}
