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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.NeuronSentry;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class NeuronSentrySprite extends MobSprite {

	private int cellToAttack;

	public NeuronSentrySprite(){

		super();

		renderShadow = false;
		shadowWidth = 0;

		texture( Assets.Sprites.NEURON_SENTRY );

		TextureFilm frames = new TextureFilm( texture, 18, 28 );

		idle = new Animation( 1, true );
		idle.frames( frames, 0, 1, 2, 3);

		run = idle.clone();

		attack = new Animation( 5, false );
		attack.frames( frames, 0,4, 5, 4, 5 );

		zap = attack.clone();

		die = new Animation( 12, false );
		die.frames( frames, 0 );

		play( idle );

	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent( cell, ch.pos )) {

			cellToAttack = cell;
			zap(cell);

		} else {

			super.attack( cell );

		}
	}

	public void zap( int pos ) {
		pos = ((NeuronSentry) ch).wherestrike;
		PointF origin = DungeonTilemap.raisedTileCenterToWorld(pos);
		PointF fin = DungeonTilemap.raisedTileCenterToWorld(pos);

		//shoot lightning from eye, not sprite center.
		origin.y -= 100;
		parent.add(new Lightning(origin, fin, (NeuronSentry) ch));

		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

		super.zap( ch.pos );
		flash();
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public void turnTo(int from, int to) {
		//do nothing
	}

	@Override
	public int blood() {
		return 0xFF88CC44;
	}

}
