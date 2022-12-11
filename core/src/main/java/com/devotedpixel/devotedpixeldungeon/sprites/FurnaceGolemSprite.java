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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FurnaceGolem;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class FurnaceGolemSprite extends MobSprite {

	public FurnaceGolemSprite () {
		super();

		texture( Assets.Sprites.FURNACEGOLEM );

		TextureFilm frames = new TextureFilm( texture, 25, 22 );

		idle = new Animation( 8, true );
		idle.frames( frames, 0, 1, 2, 1 );

		run = new Animation( 11, true );
		run.frames( frames, 3, 4, 3 );

		attack = new Animation( 8, false );
		attack.frames( frames, 5, 6, 7 );

		zap = attack.clone();

		die = new Animation( 3, false );
		die.frames( frames, 8, 9, 10, 11, 12, 13 );

		play( idle );
	}


	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( zap );

		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
	}

	public void shroud( int cell ) {


		turnTo( ch.pos , cell );
		play( zap );

		MagicMissile.boltFromChar( parent,
				MagicMissile.SMOG,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((FurnaceGolem)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.GAS );
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}

}
