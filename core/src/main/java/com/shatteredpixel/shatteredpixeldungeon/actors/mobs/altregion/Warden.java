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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardenSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Warden extends Mob {
	
	{
		spriteClass = WardenSprite.class;
		
		HP = HT = 120;
		defenseSkill = 15;
		
		EXP = 12;
		maxLvl = 22;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.2f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		flying = true;

		HUNTING = new Warden.Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 30 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 12);
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		super.rollToDropLoot();
	}

	public Item createLoot() {
		Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
		//uses probability tables for demon halls
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(5, true);
		} else {
			return Generator.randomArmor(5);
		}
	}

	private float wardcooldown = 0;

    private static final String COOLDOWN = "COOLDOWN";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COOLDOWN, wardcooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        wardcooldown = bundle.getFloat( COOLDOWN );
    }

	@Override
	protected void spend( float time ) {
		wardcooldown -= time;
		super.spend( time );
	}

	public void DashAwayFromEnemy(){

		Ballistica route = new Ballistica(enemy.pos, this.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        int dist = 1;
        if (Dungeon.level.distance(this.pos, enemy.pos) < 2) dist++;

        int cell = route.path.get(Dungeon.level.distance(this.pos, enemy.pos) + dist );
		com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.MISS);
		if (cell != pos && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
			sprite.emitter().start(MagicMissile.WardParticle.FACTORY, 0.01f, Math.round(4 + 2 * Dungeon.level.trueDistance(pos, cell)));
			sprite.jump(pos, cell, 1f, 0.35f, new com.watabou.utils.Callback() {
				@Override
				public void call() {
					if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
						Door.leave(pos);
					}
					pos = cell;
					Dungeon.level.occupyCell(Warden.this);
				}
			});
		}
		spend(1f);

	}

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			if (enemyInFOV && wardcooldown < 0.1)
			{

				WandOfWarding.Ward ward = new EnemyWard();
				ward.pos = pos;
				GameScene.add(ward, 2f);
				Dungeon.level.occupyCell(ward);
				Dungeon.level.pressCell(pos);
                //This uses normal int range instead of unweighted random, since its kind of like dealing damage
				wardcooldown = Random.NormalIntRange(2, 6);
				DashAwayFromEnemy();
				return true;
			}
				else return super.act( enemyInFOV, justAlerted );

		}
	}


	public static class EnemyWard extends WandOfWarding.Ward {

		{
			spriteClass = WardSprite.class;

			alignment = Alignment.ENEMY;

			properties.add(Char.Property.IMMOVABLE);
			properties.add(Char.Property.INORGANIC);

			viewDistance = 0;
			state = WANDERING;
		}

        @Override
        public CharSprite sprite() {
            WardSprite sprite = (WardSprite) super.sprite();
            sprite.linkVisuals(this);
            sprite.color(255, 105, 242);
            return sprite;
        }

		@Override
		public boolean act() {

			if (viewDistance < 8) viewDistance++;

		return super.act();
		}

		@Override
		public boolean canInteract(Char c) {
			return false;
		}



	}


}
