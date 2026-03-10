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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.noosa.TextureFilm;

public class EmperorCrystalSprite extends MobSprite {

	private Animation activeIdle;

	private Animation inactiveidle;

	public EmperorCrystalSprite() {
		super();

		perspectiveRaise = 5/16f; //1 pixel less
		renderShadow = false;

		texture( Assets.Sprites.EMPERORCRYSTAL );

		TextureFilm frames = new TextureFilm( texture, 9, 22 );

		idle = new Animation( 1, true );
		idle.frames( frames, 0 );

		activeIdle = new Animation( 1, true );
		activeIdle.frames( frames, 1, 2 );

		inactiveidle = new Animation( 1, true );
		inactiveidle.frames( frames, 0 );

		run = activeIdle.clone();

		attack = idle.clone();

		die = new Animation( 1, false );
		die.frames( frames, 3 );

		play( idle );
	}

    @Override
    public void link(Char ch) {
        super.link(ch);
        renderShadow = false;
    }


	@Override
	public void play(Animation anim) {
		if (anim == idle) {
			if (ch != null && !ch.isInvulnerable(Hero.class)) anim = activeIdle;
			else anim = inactiveidle;
		}
		super.play(anim);
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}
