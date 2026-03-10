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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BarfParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public class Barf extends Blob {

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


                    affect(cell);

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
		if (ch != null && !ch.isImmune(Barf.class)) {

				Buff.affect(ch, Ooze.class).set( Ooze.DURATION * 0.75f );

		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( BarfParticle.FACTORY, 0.2f );
	}



	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
