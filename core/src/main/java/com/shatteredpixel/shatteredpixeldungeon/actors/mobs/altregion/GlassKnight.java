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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GlassKnightSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GlassKnight extends Mob {

	//they can only use their chains once
	private boolean chainsUsed = false;

	{
		spriteClass = GlassKnightSprite.class;

		HP = HT = 34;
		defenseSkill = 12;

		EXP = 7;
		maxLvl = 14;

		loot = Generator.Category.ARMOR;
		lootChance = 0.2f; //by default, see lootChance()

		properties.add(Char.Property.ICY);
		properties.add(Char.Property.INORGANIC);
		
		//HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(5, 11);
	}

	public static class IceSlash{}

	private void performSpin(){

		chainsUsed = true;

		Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

		Sample.INSTANCE.play(Assets.Sounds.EVOKE);

		ConeAOE cone = new ConeAOE(aim,
				2.5f,
				360,
				Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

		sprite.zap(pos);
		spend(1f);

		for (int cell : cone.cells) {
			GameScene.add(Blob.seed(cell, 5, Freezing.class));
			if (Actor.findChar(cell) != null) Actor.findChar(cell).damage(Random.NormalIntRange(6, 8), new IceSlash());
		}

	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 7);
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GUARD_ARM.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.GUARD_ARM.count++;
		return super.createLoot();
	}

	private final String CHAINSUSED = "chainsused";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAINSUSED, chainsUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chainsUsed = bundle.getBoolean(CHAINSUSED);
	}

	@Override
	protected boolean act() {
		if (state == HUNTING || state == WANDERING){


			if (!chainsUsed && enemy != null && enemy.distance(this) < 3)
			{
			performSpin();
			return true;
			}
			else return super.act();

		}
		else return super.act();
	}
}
