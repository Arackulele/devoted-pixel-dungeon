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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GustBeingSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GustBeing extends Mob {

	private Emitter particles;

	{
		spriteClass = GustBeingSprite.class;

		HP = HT = 60;
		defenseSkill = 22;
		viewDistance = Light.DISTANCE;

		EXP = 9; //for corrupting
		maxLvl = -2;

		HUNTING = new Hunting();

		baseSpeed = 1f;

		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 25 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 30;
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}


	@Override
	protected boolean getCloser( int target ) {


		if (burst == true) {
			burst = false;
			sprite.idle();
		}


		return super.getCloser( target );
	}

	private int cooldown = 1;

	private boolean burst = false;

	@Override
	public boolean act() {

		if (enemy != null && cooldown <= 0) {
			if ( Dungeon.level.distance(enemy.pos, pos) <= 3
					&& new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos
					&& new Ballistica(enemy.pos, pos, Ballistica.PROJECTILE).collisionPos == pos);
			{

				for (int i = 0; i < Dungeon.level.length(); i++){
					if (this.fieldOfView != null && this.fieldOfView[i]
							&& Dungeon.level.distance(i, this.pos) <= 3
							&& new Ballistica( pos, i, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == i
							&& new Ballistica( i, pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == pos){
						Emitter e = CellEmitter.get(i);
						e.burst( Speck.factory( Speck.JET ), 6 );
					}
				}

				 Ballistica trajectory = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_SOLID);
				 Paralysis.prolong(enemy, Paralysis.class, 1);
				 throwChar(enemy, trajectory, 3, false, false, this.getClass());
				 cooldown = 7;
				 burst = true;
			}
		}
		else cooldown--;


		return super.act();
	}



	public static void throwChar(final Char ch, final Ballistica trajectory, int power,
								 boolean closeDoors, boolean collideDmg, Class cause){
		if (ch.properties().contains(Char.Property.BOSS)) {
			power /= 2;
		}

		int dist = Math.min(trajectory.dist, power);

		boolean collided = dist == trajectory.dist;

		if (dist <= 0
				|| ch.rooted
				|| ch.properties().contains(Char.Property.IMMOVABLE)) return;

		//large characters cannot be moved into non-open space
		if (Char.hasProp(ch, Char.Property.LARGE)) {
			for (int i = 1; i <= dist; i++) {
				if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
					dist = i-1;
					collided = true;
					break;
				}
			}
		}

		if (Actor.findChar(trajectory.path.get(dist)) != null){
			dist--;
			collided = true;
		}

		if (dist < 0) return;

		final int newPos = trajectory.path.get(dist);

		if (newPos == ch.pos) return;

		final int finalDist = dist;
		final boolean finalCollided = collided && collideDmg;
		final int initialpos = ch.pos;



		Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
			public void call() {
				if (initialpos != ch.pos || Actor.findChar(newPos) != null) {
					//something caused movement or added chars before pushing resolved, cancel to be safe.
					ch.sprite.place(ch.pos);
					return;
				}
				int oldPos = ch.pos;
				ch.pos = newPos;
				if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
					Door.leave(oldPos);
				}
				Dungeon.level.occupyCell(ch);
				if (ch == Dungeon.hero){
					Dungeon.observe();
					GameScene.updateFog();
				}
			}
		}), -1);
	}

}
