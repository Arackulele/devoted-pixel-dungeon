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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DemonicGolemSprite extends MobSprite {

	public DemonicGolemSprite () {
		super();

		texture( Assets.Sprites.ROCKGOLEM );

		TextureFilm frames = new TextureFilm( texture, 21, 19 );

		idle = new Animation( 1, true );
		idle.frames( frames, 10, 10, 18, 18 );

		run = new Animation( 3, true );
		run.frames( frames, 11, 12 );

		attack = new Animation( 7, false );
		attack.frames( frames, 13, 14, 14 );


		die = new Animation( 3, false );
		die.frames( frames, 15, 16, 17 );

		play( idle );
	}



	@Override
	public int blood() {
		return 0xFFFFFF88;
	}

}
