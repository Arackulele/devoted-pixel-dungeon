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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CragWolfSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CragWolf extends Mob {

	{
		spriteClass = CragWolfSprite.class;

		HP = HT = 42;
		defenseSkill = 10;

		EXP = 7;
		maxLvl = -2;

		loot = new Firebloom.Seed();
		lootChance = 0.1f;

		WANDERING = new CragWolf.Wandering();

	}

	@Override
	public int attackSkill( Char target ) {
		return 26;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(3, 9);
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.75f;
	}

	public int howlmeter = 0;
	@Override
	public int attackProc(Char enemy, int damage) {
		howlmeter++;

		return damage;
	}

	@Override
	public void move( int step, boolean travelling) {

		howlmeter = 0;
		super.move( step, travelling);
	}

	@Override
	protected boolean act() {
		boolean result = super.act();
		if (howlmeter > 3) {
			this.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "howl"));
			if (Dungeon.level.heroFOV[pos])com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.HOWL, 0.8f, 1f);


			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (fieldOfView[mob.pos] && mob instanceof CragWolf) {
					Buff.affect(mob, Adrenaline.class, 4f);
				}
			}
			howlmeter = 0;
			spend(2f);


		}

		return result;
	}

	private static final String HOWLMETER = "howlmeter";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HOWLMETER, howlmeter);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		howlmeter = bundle.getInt(HOWLMETER);
	}

	@Override
	protected boolean getCloser( int target ) {

		if (rooted) {
			return false;
		}

		int step = Dungeon.findStep( this, target, com.watabou.utils.BArray.and(Dungeon.level.dry, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean getFurther( int target ) {
		int step = Dungeon.flee( this, target, com.watabou.utils.BArray.and(Dungeon.level.dry, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}


	public class Wandering extends Mob.Wandering {
		@Override
		protected int randomDestination() {

			Mob vault = null;

			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (Dungeon.level.distance(mob.pos, pos) < 10 && (mob instanceof Vault)) {
					if (vault != null && Dungeon.level.distance(mob.pos, pos) < Dungeon.level.distance(vault.pos, pos)) vault = mob;
					else if (vault == null) vault = mob;
				}
			}


			if (vault != null){
				return vault.pos;
			} else {
				return super.randomDestination();
			}
		}
	}

}
