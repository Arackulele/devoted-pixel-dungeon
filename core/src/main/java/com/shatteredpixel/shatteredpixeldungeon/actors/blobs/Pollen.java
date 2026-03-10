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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PollenParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Random;

public class Pollen extends Blob {

	{
		//acts before the hero, to ensure terrain is adjusted correctly
		actPriority = Actor.HERO_PRIO+1;
	}
	
	@Override
	protected void evolve() {

		int cell;


		for (int i = area.left-1; i <= area.right; i++) {
			for (int j = area.top-1; j <= area.bottom; j++) {
				cell = i + j* Dungeon.level.width();
				if (cur[cell] > 0) {


					Pollen.affect(cell);

					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	public static void affect( int cell ){
		Char ch = Actor.findChar( cell );
		if (ch != null && !ch.isImmune(Pollen.class)) {
			if (ch.buff(Blindness.class) == null){

				int PollenEffect = Random.Int(6);

				if ( PollenEffect >= 5 ) Buff.affect(ch, Blindness.class, 8f);
				else if ( PollenEffect == 1 )Buff.affect(ch, Cripple.class, 6f);

			}
		}
	}


	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( PollenParticle.FACTORY, 0.05f, 0 );
	}



	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}


}
