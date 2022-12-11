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
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpinnerQueenSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Excalibur;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Gungnir;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SpinnerQueen extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
	{
		spriteClass = SpinnerQueenSprite.class;
		
		HP = HT = Dungeon.hero.lvl*25;
		EXP = 0;

		flying = true;

		state = WANDERING;

		baseSpeed = 2f;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}

	private static final String LEVEL = "level";
	private int timer;
	private Boolean hasadjacent;



	private int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}

	private int right(int direction){
		return direction == 7 ? 0 : direction+1;
	}


	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		adjustStats();
		webCoolDown = bundle.getInt( WEB_COOLDOWN );
		lastEnemyPos = bundle.getInt( LAST_ENEMY_POS );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.hero.lvl*2, Dungeon.hero.lvl*4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10 + Dungeon.hero.lvl;
	}

	public void adjustStats() {
		this.level = level;
		this.HP = Dungeon.hero.lvl*25;
		this.HT = this.HP;
		defenseSkill = Dungeon.hero.lvl*2;
		enemySeen = true;
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}

	private int webCoolDown = 0;
	private int lastEnemyPos = -1;

	private static final String WEB_COOLDOWN = "web_cooldown";
	private static final String LAST_ENEMY_POS = "last_enemy_pos";



	@Override
	protected boolean act() {
		AiState lastState = state;
		boolean result = super.act();

		//if state changed from wandering to hunting, we haven't acted yet, don't update.
		if (!(lastState == WANDERING && state == HUNTING)) {
			webCoolDown--;
			if (shotWebVisually){
				result = shotWebVisually = false;
			} else {
				if (enemy != null && enemySeen) {
					lastEnemyPos = enemy.pos;
				} else {
					lastEnemyPos = Dungeon.hero.pos;
				}
			}
		}

		if (state == FLEEING && buff( Terror.class ) == null && buff( Dread.class ) == null &&
				enemy != null && enemySeen && enemy.buff( Poison.class ) == null) {
			state = HUNTING;
		}



			ArrayList<Integer> spawnPoints = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = this.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add(p);
				}
			}

			if (state == FLEEING && Random.Int(3) == 1) {

				ArrayList<Integer> respawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = this.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
						respawnPoints.add(p);
					}
				}
				int index = Random.index(respawnPoints);

				if (respawnPoints.size() > 0) {
					Spinner mob = new Spinner();
					GameScene.add(mob);
					mob.pos = Random.element(spawnPoints);
					ScrollOfTeleportation.appear(mob, respawnPoints.get(index));
				}
			}

		return result;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int(2) == 0) {
			int duration = Random.IntRange(7, 8);
			//we only use half the ascension modifier here as total poison dmg doesn't scale linearly
			duration = Math.round(duration * (AscensionChallenge.statModifier(this)/2f + 0.5f));
			Buff.affect(enemy, Poison.class).set(duration);
			webCoolDown = 0;
			state = FLEEING;
		}

		return damage;
	}

	private boolean shotWebVisually = false;

	@Override
	public void move(int step, boolean travelling) {
		if (travelling && enemySeen && webCoolDown <= 0 && lastEnemyPos != -1){
			if (webPos() != -1){
				if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
					sprite.zap( webPos() );
					shotWebVisually = true;
				} else {
					shootWeb();
				}
			}
		}
		super.move(step, travelling);
	}

	public int webPos(){

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

		int webPos = b.path.get( collisionIndex+1 );

		//ensure we aren't shooting the web through walls
		int projectilePos = new Ballistica( pos, webPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID).collisionPos;

		if (webPos != enemy.pos && projectilePos == webPos && Dungeon.level.passable[webPos]){
			return webPos;
		} else {
			return -1;
		}

	}

	public void shootWeb(){
		int webPos = webPos();
		if (webPos != -1){
			int i;
			for ( i = 0; i < PathFinder.CIRCLE8.length; i++){
				if ((enemy.pos + PathFinder.CIRCLE8[i]) == webPos){
					break;
				}
			}

			//spread to the tile hero was moving towards and the two adjacent ones
			int leftPos = enemy.pos + PathFinder.CIRCLE8[left(i)];
			int rightPos = enemy.pos + PathFinder.CIRCLE8[right(i)];

			if (Dungeon.level.passable[leftPos]) GameScene.add(Blob.seed(leftPos, 20, Web.class));
			if (Dungeon.level.passable[webPos])  GameScene.add(Blob.seed(webPos, 20, Web.class));
			if (Dungeon.level.passable[rightPos])GameScene.add(Blob.seed(rightPos, 20, Web.class));

			webCoolDown = 10;

			if (Dungeon.level.heroFOV[enemy.pos]){
				Dungeon.hero.interrupt();
			}
		}
		next();
	}


	{
		resistances.add(Poison.class);
	}

	{
		immunities.add(Web.class);
	}

	{
		immunities.add(Doom.class);
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null && buff(Dread.class) == null) {
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		TrollChild.Quest.complete();
		TrollChild.reward = new Gungnir();
	}

}
