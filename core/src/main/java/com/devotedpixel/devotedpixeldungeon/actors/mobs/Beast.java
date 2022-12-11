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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.ColdhouseBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.Meal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BeastSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;
import java.util.ArrayList;

public class Beast extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 250 : 200;
		EXP = 10;
		defenseSkill = 8;
		spriteClass = BeastSprite.class;
		baseSpeed = 1.25f;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		properties.add(Property.ACIDIC);

		FLEEING = new Fleeing();
		HUNTING = new Hunting();
	}

	private int healInc = 1;

	private int leapPos = -1;
	private float leapCooldown = 3;

	private int lastEnemyPos = -1;

	@Override
	public int damageRoll() {
		int min = 4;
		int max = (HP * 2 <= HT) ? 21 : 15;

		return Random.NormalIntRange(min, max);
	}

	@Override
	public int attackSkill(Char target) {
		int attack = 20;
		if (HP * 2 <= HT) attack = 30;
		return attack;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int) (super.defenseSkill(enemy) * ((HP * 2 <= HT) ? 3 : 2));
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(1, 9);
	}

	public void damage(int dmg, Object src) {

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*2);

		boolean bleeding = (HP*2 <= HT);
		if ((HP*2 <= HT) && !bleeding){
			BossHealthBar.bleed(true);
			sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
			yell(Messages.get(this, "scream"));
		}

		if (dmg > 14) super.damage((int) (10 + (dmg * 0.2)), src);
		else super.damage(dmg, src);
	}


	@Override
	public boolean act() {

		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()) {
				if (ch instanceof DriedRose.GhostHero) {
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}

		if (HP * 2 > HT) {

			AiState lastState = state;
			boolean result = super.act();
			if (leapCooldown > 0) leapCooldown--;

			//if state changed from wandering to hunting, we haven't acted yet, don't update.
			if (!(lastState == WANDERING && state == HUNTING)) {
				if (enemy != null) {
					lastEnemyPos = enemy.pos;
				} else {
					lastEnemyPos = Dungeon.hero.pos;
				}
			}

		}


		if (HP * 2 > HT) {
			BossHealthBar.bleed(false);
			HP = Math.min(HP, HT);
		}


		if (state != SLEEPING) {
			Dungeon.level.seal();
		}

		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy);
	}
	private Boolean hasadjacent;
	@Override
	public int attackProc(Char enemy, int damage) {
		hasadjacent=false;
		damage = super.attackProc(enemy, damage);

			if (damage > 0 && Random.Int(2) == 0) {
				Buff.affect(enemy, Bleeding.class).set(damage/0.75f);
				if (HP * 2 > HT) {
				state = FLEEING;
				summonRats(Random.Int(2, 5));
			}
				else {
					Ballistica route = new Ballistica(this.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
					int cell = route.collisionPos;

					for (int i : PathFinder.NEIGHBOURS8) {
						hasadjacent = true;
						Char mob = Actor.findChar(this.pos + i);
						if (mob != null) {
								if (mob.pos == this.pos + i) {
									Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, Ballistica.MAGIC_BOLT);
									int strength = 4;
									WandOfBlastWave.throwChar(mob, trajectory, strength, true, false, this.getClass());
								}
						}
					}
				}
		}

		return damage;
	}

	public void summonRats(int amount) {


		while (amount > 0) {

			ArrayList<Integer> respawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = this.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
					respawnPoints.add(p);
				}
				int index = Random.index(respawnPoints);
			}

			if (respawnPoints.size() > 0) {
				int type = Random.Int(40);
				Mob mob;

				if (type <= 30) {
					mob = new Rat();
				} else if (type <= 38) {
					mob = new Albino();
				} else {
					mob = new FetidRat();
				}


				mob.pos = Random.element(respawnPoints);
				GameScene.add(mob, 1);
				mob.state = mob.HUNTING;
				Dungeon.level.occupyCell(mob);
			}


			amount--;
		}


	}


	@Override
	protected boolean getCloser(int target) {

		return super.getCloser(target);
	}

	@Override
	protected boolean getFurther(int target) {


		return super.getFurther(target);
	}


	@Override
	public void die(Object cause) {

		super.die(cause);

		Dungeon.level.unseal();

		GameScene.bossSlain();

		Camera.main.shake( 3, 1f );

		Dungeon.level.drop( new Meal(), pos ).sprite.drop();

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge) {
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[0] += 1050;
		Statistics.bossScores[0] = Math.min(1000, Statistics.bossScores[0]);

		yell(Messages.get(this, "defeated"));
	}

	@Override
	public void notice() {
		super.notice();

	}

	{
		immunities.add(StenchGas.class);
	}


	@Override
	public void storeInBundle(Bundle bundle) {

		super.storeInBundle(bundle);

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		BossHealthBar.assignBoss(this);
		if ((HP * 2 <= HT)) BossHealthBar.bleed(true);


	}


	private class Fleeing extends Mob.Fleeing {


		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {

			if (Random.Int(2) == 0) {

				int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
				if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos])
						|| (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[newPos])
						|| Actor.findChar(newPos) != null) return super.act(enemyInFOV, justAlerted);
				else {
					sprite.move(pos, newPos);
					pos = newPos;
					GameScene.add(Blob.seed(pos, 60, StenchGas.class));
				}
			} else if (Random.Int(5) == 2) state = HUNTING;
			return super.act(enemyInFOV, justAlerted);
		}


	}


	public class Hunting extends Mob.Hunting {

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {

			if (HP * 2 <= HT) {
				if (leapPos != -1) {

					leapCooldown = 4;
					Ballistica b = new Ballistica(pos, leapPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);

					//check if leap pos is not obstructed by terrain
					if (rooted || b.collisionPos != leapPos) {
						leapPos = -1;
						return true;
					}

					final Char leapVictim = Actor.findChar(leapPos);
					final int endPos;

					//ensure there is somewhere to land after leaping
					if (leapVictim != null) {
						int bouncepos = -1;
						for (int i : PathFinder.NEIGHBOURS8) {
							if ((bouncepos == -1 || Dungeon.level.trueDistance(pos, leapPos + i) < Dungeon.level.trueDistance(pos, bouncepos))
									&& Actor.findChar(leapPos + i) == null && Dungeon.level.passable[leapPos + i]) {
								bouncepos = leapPos + i;
							}
						}
						if (bouncepos == -1) {
							leapPos = -1;
							return true;
						} else {
							endPos = bouncepos;
						}
					} else {
						endPos = leapPos;
					}

					//do leap
					sprite.visible = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[leapPos] || Dungeon.level.heroFOV[endPos];
					sprite.jump(pos, leapPos, new Callback() {
						@Override
						public void call() {

							if (leapVictim != null && alignment != leapVictim.alignment) {
								Statistics.qualifiedForBossChallengeBadge = false;
								Buff.affect(leapVictim, Bleeding.class).set(0.4f * damageRoll());
								leapVictim.sprite.flash();
								Sample.INSTANCE.play(Assets.Sounds.HIT);
							}

							if (endPos != leapPos) {
								Actor.addDelayed(new Pushing(Beast.this, leapPos, endPos), -1);
							}

							pos = endPos;
							leapPos = -1;
							sprite.idle();
							Dungeon.level.occupyCell(Beast.this);
							next();
						}
					});
					return false;
				}

				enemySeen = enemyInFOV;
				if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

					return doAttack(enemy);

				} else {

					if (enemyInFOV) {
						target = enemy.pos;
					} else if (enemy == null) {
						state = WANDERING;
						target = Dungeon.level.randomDestination(Beast.this);
						return true;
					}

					if (enemyInFOV && !rooted
							&& Dungeon.level.distance(pos, enemy.pos) >= 3) {

						int targetPos = enemy.pos;
						if (lastEnemyPos != enemy.pos) {
							int closestIdx = 0;
							for (int i = 1; i < PathFinder.CIRCLE8.length; i++) {
								if (Dungeon.level.trueDistance(lastEnemyPos, enemy.pos + PathFinder.CIRCLE8[i])
										< Dungeon.level.trueDistance(lastEnemyPos, enemy.pos + PathFinder.CIRCLE8[closestIdx])) {
									closestIdx = i;
								}
							}
							targetPos = enemy.pos + PathFinder.CIRCLE8[(closestIdx + 4) % 8];
						}

						Ballistica b = new Ballistica(pos, targetPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
						//try aiming directly at hero if aiming near them doesn't work
						if (b.collisionPos != targetPos && targetPos != enemy.pos) {
							targetPos = enemy.pos;
							b = new Ballistica(pos, targetPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
						}
						if (b.collisionPos == targetPos) {
							//get ready to leap
							leapPos = targetPos;
							//don't want to overly punish players with slow move or attack speed
							spend(GameMath.gate(TICK, enemy.cooldown(), 3 * TICK));
							if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[leapPos]) {
								GLog.w(Messages.get(Beast.this, "leap"));
								sprite.parent.addToBack(new TargetedCell(leapPos, 0xFF0000));
								Dungeon.hero.interrupt();
							}
							return true;
						}
					}

					int oldPos = pos;
					if (target != -1 && getCloser(target)) {

						spend(1 / speed());
						return moveSprite(oldPos, pos);

					} else {
						spend(TICK);
						if (!enemyInFOV) {
							sprite.showLost();
							state = WANDERING;
							target = Dungeon.level.randomDestination(Beast.this);
						}
						return true;
					}
				}
			} else return super.act(enemyInFOV, justAlerted);

		}


	}
}
