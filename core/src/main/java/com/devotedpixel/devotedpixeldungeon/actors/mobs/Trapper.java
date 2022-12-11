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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpinnerQueen;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrapperSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Trapper extends Mob {
	
	{
		spriteClass = TrapperSprite.class;
		
		HP = HT = 65;
		defenseSkill = 20;
		
		EXP = 7;
		maxLvl = 20;

		loot = new Food();
		lootChance = 0.083f;
		
		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 16, 22 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 24;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );

		super.rollToDropLoot();
	}

	private boolean hasbolas = true;

	@Override
	protected boolean canAttack( Char enemy ) {

		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);

		if (Dungeon.level.adjacent( pos, enemy.pos )) return true;
		else if( attack.collisionPos == enemy.pos && this.hasbolas == true ) return true;
		else return false;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		if (!Dungeon.level.adjacent( pos, enemy.pos )) {
			hasbolas = false;
			Buff.prolong( enemy, Cripple.class, Cripple.DURATION );
		}

		return damage;
	}

	private int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}

	private int right(int direction){
		return direction == 7 ? 0 : direction+1;
	}

	private int trapcooldown = 0;
	private int lastEnemyPos = -1;




	public void setTraps(){


		 Trap[] trapClasses = new Trap[]{
				new ChillingTrap(),new ShockingTrap(),new ToxicTrap(),new WornDartTrap(),
		new AlarmTrap(),new OozeTrap(),
		new ConfusionTrap() ,new FlockTrap() ,new SummoningTrap() ,new TeleportationTrap() ,new GatewayTrap() };


		int trapPos = trapPos();
		if (trapPos != -1) {
			int i;
			for (i = 0; i < PathFinder.CIRCLE8.length; i++) {
				if ((enemy.pos + PathFinder.CIRCLE8[i]) == trapPos) {
					break;
				}
			}

			//spread to the tile hero was moving towards and the two adjacent ones
			int leftPos = enemy.pos + PathFinder.CIRCLE8[left(i)];
			int rightPos = enemy.pos + PathFinder.CIRCLE8[right(i)];


			if (Dungeon.level.passable[leftPos] && Dungeon.level.map[leftPos] != Terrain.EXIT&& Dungeon.level.map[leftPos] != Terrain.ENTRANCE) {
				Level.set(leftPos, Terrain.SECRET_TRAP);


				Trap t = trapClasses[Random.Int(trapClasses.length)];

				Dungeon.level.setTrap(t, leftPos);

				Dungeon.level.discover(leftPos);
				CellEmitter.get(leftPos).burst(Speck.factory(Speck.LIGHT), 2);

			}
			if (Dungeon.level.passable[trapPos] && Dungeon.level.map[trapPos] != Terrain.EXIT&& Dungeon.level.map[trapPos] != Terrain.ENTRANCE) {
				Level.set(trapPos, Terrain.SECRET_TRAP);


				Trap t1 = trapClasses[Random.Int(trapClasses.length)];

				Dungeon.level.setTrap(t1, trapPos);

				Dungeon.level.discover(trapPos);
				CellEmitter.get(trapPos).burst(Speck.factory(Speck.LIGHT), 2);
			}

			if (Dungeon.level.passable[rightPos] && Dungeon.level.map[rightPos] != Terrain.EXIT&& Dungeon.level.map[rightPos] != Terrain.ENTRANCE) {
				Level.set(rightPos, Terrain.SECRET_TRAP);


				Trap t3 = trapClasses[Random.Int(trapClasses.length)];

				Dungeon.level.setTrap(t3, rightPos);

				Dungeon.level.discover(rightPos);
				CellEmitter.get(rightPos).burst(Speck.factory(Speck.LIGHT), 2);
			}

			trapcooldown = 20;

			if (Dungeon.level.heroFOV[enemy.pos]) {
				Dungeon.hero.interrupt();
			}
		}
		next();
	}


	@Override
	protected boolean act() {
		trapcooldown--;

		AiState lastState = state;
		boolean result = super.act();


				if (enemy != null && enemySeen) {
					lastEnemyPos = enemy.pos;
				} else {
					lastEnemyPos = Dungeon.hero.pos;
				}


		return result;
	}



	public int trapPos(){

		Char enemy = this.enemy;
		if (enemy == null) return -1;

		Ballistica b;
		//aims web in direction enemy is moving, or between self and enemy if they aren't moving
		if (lastEnemyPos == enemy.pos){
			b = new Ballistica( enemy.pos, pos, Ballistica.WONT_STOP );
		} else {
			b = new Ballistica( lastEnemyPos, enemy.pos, Ballistica.WONT_STOP );
		}

		int collisionIndex = 0;
		for (int i = 0; i < b.path.size(); i++){
			if (b.path.get(i) == enemy.pos){
				collisionIndex = i;
				break;
			}
		}

		//in case target is at the edge of the map and there are no more cells in the path
		if (b.path.size() <= collisionIndex+1){
			return -1;
		}

		int trapPos = b.path.get( collisionIndex+1 );

		//ensure we aren't shooting the web through walls
		int projectilePos = new Ballistica( pos, trapPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID).collisionPos;

		if (trapPos != enemy.pos && projectilePos == trapPos && Dungeon.level.passable[trapPos]){
			return trapPos;
		} else {
			return -1;
		}

	}

	@Override
	public void move(int step, boolean travelling) {
		if (travelling && enemySeen && trapcooldown <= 0 && lastEnemyPos != -1){
			if (trapPos() != -1){

				setTraps();

			}
		}
		super.move(step, travelling);
	}


	private static final String TRAPCOOLDOWN = "TRAPCOOLDOWN";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TRAPCOOLDOWN, trapcooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		trapcooldown = bundle.getInt( TRAPCOOLDOWN );
	}


}
