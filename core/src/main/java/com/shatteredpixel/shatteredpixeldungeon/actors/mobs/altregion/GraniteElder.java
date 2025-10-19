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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GraniteElderSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GraniteElder extends GraniteTroll {
	
	{
		spriteClass = GraniteElderSprite.class;

		HP = HT = 35;
		defenseSkill = 20;

		EXP = 8;
		maxLvl = 16;

		loot = Gold.class;
		lootChance = 0.5f;

		immunities.add(Burning.class);
	}
	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {

		if (rockfall){
			rockfall = false;

			Ballistica bolt = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

			int dist = 4;

			ConeAOE cone = new ConeAOE(bolt,
					dist,
					60,
					Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

			PixelScene.shake( 2, 0.2f );


			for (int cell : cone.cells) {
				CellEmitter.get(cell).start(Speck.factory(Speck.LIGHT), 0.07f, 2);
				Char ch = Actor.findChar( cell );
				if (ch != null && ch.alignment != alignment)
				{
					ch.damage(Random.NormalIntRange(5, 18) - ch.drRoll(), this);
				}

				if ((Dungeon.level.map[cell] != Terrain.CHASM)) {
					GameScene.add(Blob.seed(cell, Random.Int(8, 18), Electricity.class));
					//GameScene.updateMap(cell);
				}


			}


			Sample.INSTANCE.play(Assets.Sounds.ROCKS);

		}

		return super.attack(enemy, dmgMulti, dmgBonus, accMulti);


	}
	

}
