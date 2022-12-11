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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForgeBossLevel;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.FurnaceGolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlastFurnaceSprite;
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

public class FurnaceGolem extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  400 : 300;
		EXP = 10;
		defenseSkill = 20;
		spriteClass = FurnaceGolemSprite.class;
		baseSpeed = 1f;

		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);

	}



	private int leapPos = -1;
	private float leapCooldown = 3;

	private int lastEnemyPos = -1;

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
		return Random.NormalIntRange(0, 6);
	}


	private boolean phasetransition1 = false;

	private boolean phasetransition2 = false;

	ForgeBossLevel level;

	public void damage(int dmg, Object src) {

		if (HP<(HT/3)*2 && phasetransition1 == false)
		{
			if (Dungeon.level instanceof ForgeBossLevel) level = (ForgeBossLevel)Dungeon.level;
				level.furnaceactive = true;
				phasetransition1 = true;
				BlastFurnaceSprite.active = true;
			yell(Messages.get(this, "scream"));
		}

		if (HP<(HT/3) && phasetransition2 == false)
		{
			phasetransition2 = true;
			yell(Messages.get(this, "scream2"));
		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*2);

		 super.damage(dmg, src);
	}



 private int fireblastcooldown = 12;

	@Override
	public float speed() {
		float newspeed = 1;
		if (fireblastcooldown < 2) newspeed = 0.5f;
		else if(phasetransition2) newspeed = 1.25f;
		return super.speed() * newspeed;
	}

	@Override
	public boolean act() {

		((ForgeBossLevel)Dungeon.level).boss = this;


		ArrayList<Integer> positions = new ArrayList<>();
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = this.pos + PathFinder.NEIGHBOURS8[i];
			positions.add(p);
		}

		for (int i : positions) {


			Char ch = Actor.findChar(i);

			if (ch != null && ch.isAlive() && ch instanceof Elemental.CoalElemental) {
				this.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "absorb"));
				this.HP += 30;
				this.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 10);
				this.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(30));
				ch.die(this);
				Statistics.qualifiedForBossChallengeBadge = false;
			}
		}


		if (phasetransition2==true){
			if (Dungeon.hero != null) {
				if (Random.Int(100) > 70) Tengu.throwBomb(this, Dungeon.hero);
			}
		}

		ForgeBossLevel.boss = this;

		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			Dungeon.level.seal();
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()) {
				if (ch instanceof DriedRose.GhostHero) {
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}

		if (fireblastcooldown == 1) GLog.n(Messages.get(this, "preparing"));

		if (fireblastcooldown > 0) fireblastcooldown--;
		else if (Dungeon.level.distance(Dungeon.hero.pos, pos) > 3) {

			if (Random.Int(100)>49)
			{
				Ballistica ballistica = new Ballistica(pos, Dungeon.hero.pos, Ballistica.IGNORE_SOFT_SOLID);
				ConeAOE cone = new ConeAOE(ballistica, 12, 30, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
				sprite.zap(ballistica.collisionPos);
				Camera.main.shake(2, 0.5f);
				for (int cell : cone.cells) {
					GameScene.add(Blob.seed(cell, 2, Fire.class));
				}
			}
			else {
				Ballistica trajectory = new Ballistica(pos, Dungeon.hero.pos, Ballistica.STOP_SOLID);
				((FurnaceGolemSprite)sprite).shroud(trajectory.collisionPos);
				for (int i : trajectory.subPath(0, trajectory.dist)){
					GameScene.add(Blob.seed(i, 300, Smog.class));
				}
				GameScene.add(Blob.seed(trajectory.collisionPos, 300, Smog.class));
			}
			fireblastcooldown = 12;
		}

		if ((HP * 3 <= HT)) BossHealthBar.bleed(true);

		if (state != SLEEPING) {

			Dungeon.level.seal();
		}

		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy);
	}



	public void onZapComplete(){
		next();
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

		Dungeon.level.unseal();

		GameScene.bossSlain();

		Camera.main.shake( 3, 1f );

		//60% chance of 2 shards, 30% chance of 3, 10% chance for 4. Average of 2.5
		int shards = Random.chances(new float[]{0, 0, 6, 3, 1});
		for (int i = 0; i < shards; i++){
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop( new EmberEssence(), pos + ofs ).sprite.drop( pos );
		}

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

	private static String PHASETRANSITION1 = "PHASETRANSITION1";

	private static String PHASETRANSITION2 = "PHASETRANSITION2";

	private static String FIREBLASTCOOLDOWN = "FIREBLASTCOOLDOWN";


	@Override
	public void storeInBundle(Bundle bundle) {

		super.storeInBundle(bundle);

		bundle.put(PHASETRANSITION1, phasetransition1);
		bundle.put(PHASETRANSITION2, phasetransition2);
		bundle.put(FIREBLASTCOOLDOWN, fireblastcooldown);

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		phasetransition1 = bundle.getBoolean( PHASETRANSITION1 );
		phasetransition2 = bundle.getBoolean( PHASETRANSITION2 );
		fireblastcooldown = bundle.getInt( FIREBLASTCOOLDOWN );


		BossHealthBar.assignBoss(this);
		if ((HP * 3 <= HT)) BossHealthBar.bleed(true);


	}



}
