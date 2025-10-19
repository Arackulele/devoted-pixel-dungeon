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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RoyalGrubSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoyalGrub extends Grub {
	
	{
		spriteClass = RoyalGrubSprite.class;
		
		HP = HT = 4;
		defenseSkill = 7;
		
		EXP = 2;
		maxLvl = 7;

		//loot = new MysteryMeat();
		//lootChance = 0.167f;

	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	private boolean addpartner = true;
	@Override
	protected boolean act() {
		//create a child
		if (addpartner == true) {

			ArrayList<Integer> candidates = new ArrayList<>();

			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (Dungeon.level.passable[n]
						&& Actor.findChar(n) == null
						&& (!Char.hasProp(this, Property.LARGE) || Dungeon.level.openSpace[n])) {
					candidates.add(n);
				}
			}

			if (!candidates.isEmpty()) {
				RoyalGrub child = new RoyalGrub();
				addpartner = false;
				if (state != SLEEPING) {
					child.state = child.WANDERING;
				}

				child.pos = Random.element(candidates);

				GameScene.add(child);
				child.addpartner = false;

				Dungeon.level.occupyCell(child);

				if (sprite.visible) {
					Actor.addDelayed(new Pushing(child, pos, child.pos), -1);
				}
			}
		}
		return super.act();
	}

	@Override
	public void die( Object cause ) {

		super.die(cause);

		if (cause == Chasm.class) return;

		List<String> types = Arrays.asList("bomb", "gas", "dew", "fire", "water", "plant");
		Random.shuffle(types);

		switch (types.get(0))
		{

			default: case "bomb":
				Bomb b = new Bomb();
				b.explode(pos);
				break;
			case "gas":
				GameScene.add(Blob.seed(this.pos , 20, ConfusionGas.class));
				break;
			case "dew":
				Dungeon.level.drop(new Dewdrop(), pos).sprite.drop(pos);
				break;
			case "fire":
				GameScene.add(Blob.seed(pos, 2, Fire.class));
				break;
			case "water":
				Dungeon.level.setCellToWater(true, pos);
				break;
			case "plant":
				Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), pos);
				break;

		}

	}

	private static final String ADDPARTNER = "addpartner";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ADDPARTNER, addpartner );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		addpartner = bundle.getBoolean( ADDPARTNER );
	}


}
