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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.RockBlock;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollSpearmanSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class TrollSpearman extends Mob {

	{
		spriteClass = TrollSpearmanSprite.class;

		HP = HT = 42;
		defenseSkill = 10;

		EXP = 7;
		maxLvl = -2;

		WANDERING = new TrollSpearman.Wandering();

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
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return attack.collisionPos == enemy.pos;
	}

	public boolean spearcharge;
	public int spearPos = -1;

	public String SPEARCHARGE = "spearcharge";

	public String SPEARPOS = "spearpos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPEARCHARGE, spearcharge);
		bundle.put(SPEARPOS, spearPos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spearcharge = bundle.getBoolean(SPEARCHARGE);
		spearPos = bundle.getInt(SPEARPOS);
	}

	@Override
	protected boolean  doAttack( Char enemy ) {

		if (enemy != null && distance(enemy) > 1)
		{
			if (!spearcharge && enemy != null) {
				spearcharge = true;
				spearPos = enemy.pos;

				lavaspear(spearPos, false);

				spend(1f);

				return true;
			}
			else {
				spend(1f);

				return true;

			}
		}

		else return super.doAttack(enemy);

	}

	@Override
	public void onAttackComplete() {
		if (enemy != null && distance(enemy) > 1) {

			Invisibility.dispel(this);
			spend( attackDelay() );

		}
		else super.onAttackComplete();
	}

	public void lavaspear(int cell, boolean destroy) {

		for (int i : PathFinder.NEIGHBOURS5){
			if (Dungeon.level.dry[cell + i] && Dungeon.level.map[cell + i] != Terrain.EMPTY_SP) {
				if (destroy) {
					Splash.at(cell + i, 0xed980e, 15);
					Level.set(cell + i, Terrain.WATER);
					GameScene.updateMap(cell + i);
					if (Actor.findChar(cell + i) != null) Dungeon.level.occupyCell(Actor.findChar(cell + i));
				}
				else sprite.parent.add(new TargetedCell(cell + i, 0xFF0000));
			}
		}


		Dungeon.level.buildFlagMaps();
	}

	@Override
	public boolean act() {

		if (spearcharge)
		{
			sprite.zap(spearPos);

			spend(1f);

			spearcharge = false;
			return true;
		}

		else return super.act();

	}



	@Override
	protected boolean getCloser( int target ) {

		if (rooted || spearcharge) {
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

		if (rooted || spearcharge) {
			return false;
		}

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

    @Override
    public void rollToDropLoot() {
        super.rollToDropLoot();

        int ofs;
        do {
            ofs = PathFinder.NEIGHBOURS9[Random.Int(9)];
        } while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs] && !Dungeon.level.dry[pos + ofs]);
        Dungeon.level.drop( new RockBlock(), pos + ofs ).sprite.drop( pos );
    }

}
