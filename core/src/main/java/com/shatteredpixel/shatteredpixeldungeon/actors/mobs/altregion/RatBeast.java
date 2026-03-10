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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Barf;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BarfParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Meal;
import com.shatteredpixel.shatteredpixeldungeon.items.Wyvernfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.ColdhouseBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatBeastSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.utils.Bundle;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;
import com.watabou.utils.PathFinder;

public class RatBeast extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 250 : 200;
		EXP = 10;
		defenseSkill = 8;
		spriteClass = RatBeastSprite.class;
		baseSpeed = 1.25f;

		properties.add(Char.Property.BOSS);
		properties.add(Char.Property.DEMONIC);
		properties.add(Char.Property.ACIDIC);

	}

	@Override
	public int damageRoll() {
		int min = 3;
		int max = (HP * 2 <= HT) ? 15 : 10;

		return Random.NormalIntRange(min, max);
	}

	@Override
	public int attackSkill(Char target) {
		int attack = 17;
		if (HP * 2 <= HT) attack = 26;
		if (HP * 2 <= HT && combochain > 3) attack = Integer.MAX_VALUE;
		return attack;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int) (super.defenseSkill(enemy) * ((HP * 2 <= HT) ? 3 : 2));
	}

	@Override
	public int drRoll() {
		if (HP * 2 <= HT) return Random.NormalIntRange(3, 11);
		return Random.NormalIntRange(2, 8);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );


		if (combochain == 4) {
				enemy.sprite.burst( 0x05e0a0a, 8 );
				Buff.affect(enemy, Broken.class).amount+=1;
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) Buff.affect(enemy, Broken.class).amount+=1;

				spend(1f);

				combochain = 0;

			if (HP*2 <= HT && shoulddoTransition)
			{
				yell(Messages.get(this, "scream"));
				PathFinder.buildDistanceMap(pos, com.watabou.utils.BArray.not(Dungeon.level.solid, null), 8);
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						if (Dungeon.level.traps.get(i) != null && !Dungeon.level.traps.get(i).active) {
							DashToPos(i);

							PathFinder.buildDistanceMap(i, com.watabou.utils.BArray.not(Dungeon.level.solid, null), 2);
							for (int h = 0; h < PathFinder.distance.length; h++) {
								if (PathFinder.distance[h] < Integer.MAX_VALUE) {
									if (Dungeon.level.traps.get(h) != null) {
										CellEmitter.get(h).burst(Speck.factory(Speck.LIGHT), 5);
										Dungeon.level.traps.get(h).active = true;
										Dungeon.level.set(h, Terrain.TRAP);
										Dungeon.level.discover(h);
									}


								}
								}

							break;
						}


					}
				}


			}

		}
		else combochain++;


		return damage;
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

		if (dmg > 14) super.damage((int) (10 + (dmg * 0.1)), src);
		else super.damage(dmg, src);
	}

	public boolean chargingBarf;

	public boolean shoulddoTransition = true;

	public int combochain;

	public com.watabou.noosa.particles.Emitter StarEmitter;

	//used so resistances can differentiate between melee and magical attacks
	public static class BarfAcid{}


	@SuppressWarnings("SuspiciousIndentation")
    @Override
	public boolean act() {

		if (HP <= HT*0.15f && shoulddoTransition)
		{
			GameScene.flash(0x80FFFFFF);

			shoulddoTransition = false;
			HP = (int)(HT*0.15f);

			Buff.affect(this, Barrier.class).setShield(40);

			ScrollOfTeleportation.teleportToLocation(Dungeon.hero, Dungeon.level.randomRespawnCell(Dungeon.hero));
			ScrollOfTeleportation.teleportToLocation(this, Dungeon.level.randomRespawnCell(Dungeon.hero));

			ColdhouseBossLevel l = null;
			if (Dungeon.level instanceof ColdhouseBossLevel) l = (ColdhouseBossLevel) Dungeon.level;
			if (l != null) l.createFinalArena();

		}

		if (StarEmitter == null) StarEmitter = sprite.emitter();

		if (combochain == 3) StarEmitter.pour(Speck.factory(Speck.FORGE), 0.2f);
		else {
			//this.sprite.emitter().clear();
			StarEmitter.on = false;
			StarEmitter.revive();
		}

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
			BossHealthBar.bleed(false);
			HP = Math.min(HP, HT);
		}


		if (state != SLEEPING) {
			Dungeon.level.seal();
		}

		if (chargingBarf) {
			if (enemy != null) {
				spend(Actor.TICK * 2);
				Ballistica bolt = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

				int dist = Random.Int(2, 3);
				if (HP <= HT*0.5f) dist += 1;
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) dist += 1;

				ConeAOE cone = new ConeAOE(bolt,
						dist,
						80,
						Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

				PixelScene.shake( 3, 0.2f );

				int max = 2;

				for (int cell : cone.cells) {



					if (Dungeon.level.map[cell] == Terrain.WATER) {
						Level.set(cell, Terrain.EMPTY);
						GameScene.updateMap(cell);
					}
                    else {
                        Char ch = Actor.findChar( cell );
                        if (ch != null && ch.alignment != alignment) {
                            if (enemy == Dungeon.hero) {
                                Statistics.bossScores[0] -= 50;
                            }

                            ch.damage(Random.NormalIntRange(2, 9), new BarfAcid());
                        }

                        if (Random.Int(10) == 1 && max > 0) {
                            Dungeon.level.drop(new Wyvernfruit(), cell).sprite.drop();
                            max--;
                        }

                        GameScene.add(Blob.seed(cell, 8, Barf.class));
                    }
				}
			}

			chargingBarf = false;

			return true;
		}


		if (enemy != null &&
				this.distance(enemy) < 3 &&
				Random.Int(6) == 1 &&
				!chargingBarf) {
            spend(1f);
            chargingBarf = true;
            sprite.emitter().start(PoisonParticle.SPLASH, 0.1f, 10);
            return true;
        }

		for (int offset : PathFinder.NEIGHBOURS9) {
			Trap T = Dungeon.level.traps.get(pos + offset);
			if (T != null &&  Random.Int(3) == 1 && T.active) {
				if (T instanceof GeyserTrap) {
					T.reveal();
					CellEmitter.get(pos + offset).burst(Speck.factory(Speck.LIGHT), 2);
				}
				else {
					T.activate();
					T.disarm();
				}
			}
		}


		return super.act();
	}


	@Override
	public void die(Object cause) {

		super.die(cause);

        //Punish not removing broken debuff before the fight ends
        if (Dungeon.hero.buff(Broken.class) != null) Statistics.bossScores[1] -= Dungeon.hero.buff(Broken.class).amount * 10;

        Buff.detach(Dungeon.hero, Broken.class);
        Buff.detach(Dungeon.hero, Ooze.class);

		ColdhouseBossLevel l = null;
		if (Dungeon.level instanceof ColdhouseBossLevel) l = (ColdhouseBossLevel) Dungeon.level;
		l.postDeath();

		Dungeon.level.unseal();

		GameScene.bossSlain();

		Camera.main.shake( 3, 1f );

		Dungeon.level.drop( new Meal(), pos ).sprite.drop();

        if (TimesStolen < 3)Statistics.qualifiedForBossChallengeBadge = false;

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge) {
			Badges.validateBossChallengeCompleted();
		}
        Statistics.bossScores[1] += 2000;


		yell(Messages.get(this, "defeated"));
	}

	@Override
	public void notice() {
		super.notice();

	}

	{
		immunities.add(StenchGas.class);
		immunities.add(Chill.class);
	}

	public void DashToPos(int Pos){

		Ballistica route = new Ballistica(this.pos, Pos, Ballistica.STOP_TARGET | Ballistica.STOP_CHARS);
		int cell = route.path.get(route.dist );
		com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.MISS);
		if (cell != pos && Dungeon.level.passable[pos] && Actor.findChar(cell) == null) {
			sprite.emitter().start(Speck.factory(Speck.DUST), 0.01f, Math.round(4 + 2 * Dungeon.level.trueDistance(pos, cell)));
			sprite.jump(pos, cell, 0.5f, 0.25f, new com.watabou.utils.Callback() {
				@Override
				public void call() {
					if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
						Door.leave(pos);
					}
					pos = cell;
					Dungeon.level.occupyCell(RatBeast.this);
				}
			});
		}
		spend(1f);

	}

    public int TimesStolen = 0;

	private static final String CHARGINGBARF     = "chargingbarf";
	private static final String SHOULDDOTRANSITION   = "shoulddoTransition";
	private static final String COMBOCHAIN = "combochain";

    private static final String TIMESSTOLEN     = "TimesStolen";


    @Override
	public void storeInBundle(Bundle bundle) {

		bundle.put( CHARGINGBARF, chargingBarf );
		bundle.put( SHOULDDOTRANSITION, shoulddoTransition );
		bundle.put( COMBOCHAIN, combochain );
        bundle.put( TIMESSTOLEN, TimesStolen );

        super.storeInBundle(bundle);

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		chargingBarf = bundle.getBoolean( CHARGINGBARF );
		shoulddoTransition = bundle.getBoolean( SHOULDDOTRANSITION );
		combochain = bundle.getInt( COMBOCHAIN );
        TimesStolen = bundle.getInt( TIMESSTOLEN );

		BossHealthBar.assignBoss(this);
		if ((HP * 2 <= HT)) BossHealthBar.bleed(true);


	}

}
