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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EquipmentDisabled;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.BedrockGolem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Puppet;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class BedrockGolemSprite extends MobSprite {

	public Animation dash;

	public BedrockGolemSprite() {
		super();

		texture( Assets.Sprites.BEDROCKGOLEM );

		TextureFilm frames = new TextureFilm( texture, 22, 21 );

		idle = new Animation( 2, true );
		idle.frames( frames, 0, 1, 2, 14, 14, 14 );

		run = new Animation( 6, true );
		run.frames( frames,  8, 9 );

		dash = new Animation( 7, false );
		dash.frames( frames, 7, 8, 9, 7 );

		attack = new Animation( 7, false );
		attack.frames( frames, 3, 4, 5, 6 );

		die = new Animation( 3, false );
		die.frames( frames, 10, 11, 12, 13);

		play( idle );
	}


	public void DashATile( int cell ) {

		PixelScene.shake(0.8f, 0.175f);

        //If doing the final step, make sure the golem doesnt overlap with any other actor
        if (((BedrockGolem)ch).counter == ((BedrockGolem)ch).route.dist) ((BedrockGolem)ch).PushAway(cell);


        jump(ch.pos, cell, 0f, 0.175f, new com.watabou.utils.Callback() {
			@Override
			public void call() {
				if (Dungeon.level.map[ch.pos] == Terrain.OPEN_DOOR) {
					Door.leave(ch.pos);
				}


					CellEmitter.get(ch.pos).burst(ShadowParticle.UP, 5);
					if (Actor.findChar(cell) != null && !(Actor.findChar(cell) instanceof BedrockGolem)) {
						Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
						Buff.affect(Actor.findChar(cell), EquipmentDisabled.class, 10f);
						int dmg = Random.NormalIntRange(25, 40);
						dmg -= Actor.findChar(cell).drRoll();
						Actor.findChar(cell).damage(dmg, this);
					}


				ch.pos = cell;
				Dungeon.level.occupyCell(ch);

				jump(ch.pos, ch.pos, 0f, 0.1f, new com.watabou.utils.Callback() {
					@Override
					public void call() {

						((BedrockGolem)ch).DashTile();

					}
				});
			}
		});

		play( dash );

	}





	@Override
	public int blood() {
		return 0xFFFFFF88;
	}

}
