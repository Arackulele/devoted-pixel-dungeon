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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.levels.CitadelBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.EmberEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WendarSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlastFurnaceSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Wendar extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  800 : 600;
		EXP = 10;
		defenseSkill = 20;
		spriteClass = WendarSprite.class;
		baseSpeed = 1f;

		flying = true;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);



	}

	private int charge = 0;
	private int cooldown = 10;

	private int beamcooldown = 4;
	@Override
	protected boolean act() {


		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		if (!level.boss.isAlive() || !level.yuria.isAlive()) die(null);

		cooldown--;
		beamcooldown--;
		deathbeam();

		if (charge == 2) {
			charge = 0;
			summonMinions(Random.NormalIntRange( 1, 3 ));
			cooldown = 15;
		}
		else if ( charge == 1) {

			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "summon2"));
			charge++;
		}


		if (cooldown<1 && charge == 0)
		{
			cooldown= 15;
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "summon1"));
			charge++;
		}


		if (Actor.findChar(target) == null) target = Dungeon.hero.pos;

		return super.act();

	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (charge != 0)
		{
			charge = 0;
			cooldown = 15;
			sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "cancel"));
		}


		return super.defenseProc( enemy, damage);
	}



	@Override
	protected boolean getCloser( int target ) {
		if (charge != 0) {
			sprite.idle();
			return false;
		}
		else if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	public void summonMinions(int amount) {


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
				int type = Random.Int(100);
				Mob mob;

				if (type <= 50) {
					mob = new Cultist();
				} else if (type <= 70) {
					mob = new Leader();
				} else if (type <= 90) {
					mob = new Trapper();
				} else mob = new Alchemist();


				mob.pos = Random.element(respawnPoints);
				GameScene.add(mob, 1);
				mob.state = mob.HUNTING;
				Dungeon.level.occupyCell(mob);
			}


			amount--;
		}


	}

	private ArrayList<Integer> targetedCells = new ArrayList<>();

	public void deathbeam() {

		boolean terrainAffected = false;
		HashSet<Char> affected = new HashSet<>();
		//delay fire on a rooted hero
		if (!Dungeon.hero.rooted) {
			for (int i : targetedCells) {
				Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP);
				//shoot beams
				sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
				for (int p : b.path) {
					Char ch = Actor.findChar(p);
					if (ch != null && (ch.alignment != alignment || ch instanceof Bee)) {
						affected.add(ch);
					}
					if (Dungeon.level.flamable[p]) {
						Dungeon.level.destroy(p);
						GameScene.updateMap(p);
						terrainAffected = true;
					}
				}
			}
			if (terrainAffected) {
				Dungeon.observe();
			}
			Invisibility.dispel(this);
			for (Char ch : affected) {

				if (hit(this, ch, true)) {
					if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
						ch.damage(Random.NormalIntRange(10, 30), new Eye.DeathGaze());
					} else {
						ch.damage(Random.NormalIntRange(10, 20), new Eye.DeathGaze());
					}
					if (Dungeon.level.heroFOV[pos]) {
						ch.sprite.flash();
						CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
					}
					if (!ch.isAlive() && ch == Dungeon.hero) {
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail(getClass());
						GLog.n(Messages.get(Char.class, "kill", name()));
					}
				} else {
					ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
				}
			}
			targetedCells.clear();
		}

		if (charge == 0 && beamcooldown < 1) {

			int beams = 1;
			HashSet<Integer> affectedCells = new HashSet<>();
			for (int i = 0; i < beams; i++) {

				int targetPos = Dungeon.hero.pos;
				if (i != 0) {
					do {
						targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
					} while (Dungeon.level.trueDistance(pos, Dungeon.hero.pos)
							> Dungeon.level.trueDistance(pos, targetPos));
				}
				targetedCells.add(targetPos);
				Ballistica b = new Ballistica(pos, targetPos, Ballistica.WONT_STOP);
				affectedCells.addAll(b.path);
			}

			for (int i : targetedCells) {
				Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP);
				for (int p : b.path) {
					sprite.parent.add(new TargetedCell(p, 0xFF0000));
					affectedCells.add(p);
				}
				beamcooldown = 5;
			}
		}
	}


	CitadelBossLevel level;

	@Override
	public void damage(int dmg, Object src) {

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		super.damage(dmg, src);
		level.yuria.HP=this.HP;
		level.boss.HP=this.HP;
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 17, 23 );
	}


	@Override
	public int attackSkill(Char target) {
		int attack = 16;
		if (HP * 2 <= HT) attack = 22;
		return attack;
	}



	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 8);
	}


	{
		immunities.add(Sleep.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
		immunities.add(Fire.class);
		immunities.add(Smog.class);
	}


	private static final String CHARGE = "CHARGE";

	private static final String COOLDOWN = "COOLDOWN";

	private static final String BEAMCOOLDOWN = "BEAMCOOLDOWN";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COOLDOWN, cooldown);
		bundle.put(BEAMCOOLDOWN, beamcooldown);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cooldown = bundle.getInt( COOLDOWN );
		beamcooldown = bundle.getInt( BEAMCOOLDOWN );
		charge = bundle.getInt( CHARGE );
	}


}
