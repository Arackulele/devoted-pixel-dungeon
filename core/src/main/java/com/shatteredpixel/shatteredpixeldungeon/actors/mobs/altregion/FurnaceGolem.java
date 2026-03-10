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

import com.badlogic.gdx.utils.IntMap;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.EmberEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForgeBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.*;
//import com.shatteredpixel.shatteredpixeldungeon.items.quest.EmberEssence;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FurnaceGolem extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  400 : 300;
		EXP = 10;
		defenseSkill = 20;
		spriteClass = FurnaceGolemSprite.class;
		baseSpeed = 1f;
        flying = true;

		properties.add(Char.Property.BOSS);
		properties.add(Char.Property.INORGANIC);
		properties.add(Char.Property.LARGE);

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
		return Random.NormalIntRange(0, 6);
	}


	private boolean phasetransition1 = false;
	private boolean phasetransition2 = false;
	private int fireblastcooldown = 6;
	private boolean infireblast = false;
	private boolean chargedattack = false;
	private int bombattackcooldown = 3;
	private int fireblasttimer = 0;
	private int fireblasttile = -1;

	private String FIREBLASTCOOLDOWN = "fireblastcooldown";
	private String INFIREBLAST = "infireblast";
	private String CHARGEDATTACK = "chargedattack";
	private String BOMBATTACKCOOLDOWN = "bombattackcooldown";
	private String FIREBLASTTIMER = "fireblasttimer";
	private String FIREBLASTTILE = "fireblasttile";


	ForgeBossLevel level;

	public void FireblastLogic()
	{
		if (fireblasttile == -1) {
			ArrayList<Integer> validtiles = new ArrayList<Integer>();

			PathFinder.buildDistanceMap(level.randomRespawnCell(Dungeon.hero), com.watabou.utils.BArray.or(Dungeon.level.passable, Dungeon.level.solid, null), 5);
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {
					//Make sure the tile isnt too close to the hero
					if (Dungeon.level.passable[i]
					&& Dungeon.level.distance(i, Dungeon.hero.pos) > 5
						)validtiles.add(i);
				}
			}
			Random.shuffle(validtiles);
			fireblasttile = validtiles.get(1);
			fireblasttimer = 13;
		}

		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.distance(i, fireblasttile) > fireblasttimer) {
				if (fireblasttimer < 1)
				{
					if (Dungeon.level.blobs.get(i) != null) Dungeon.level.blobs.get(i).clear(i);
					Char ch = Actor.findChar(i);
					CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
					if (ch != null && ch.isAlive() && ch.alignment != this.alignment) {
						int damage = Math.round(Random.NormalIntRange(20, 45));
						damage = Math.round(damage);
						ch.damage(damage, new Fire());
					}

				}
				else GameScene.add( Blob.seed(i, 1, Smog.class));
			}
		}

		if (fireblasttimer < 1)
		{
			infireblast = false;
			fireblastcooldown = 25;
			fireblasttimer = 0;
			fireblasttile = -1;
			com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.EXPLOSION, 1.5f );
			com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.BURNING );
			return;
		}

		fireblasttimer--;
		com.watabou.utils.PointF p = DungeonTilemap.raisedTileCenterToWorld(fireblasttile);
		FloatingText.show(p.x, p.y, fireblasttile, (fireblasttimer+1) + "...", CharSprite.WARNING);
        sprite.parent.add(new TargetedCell(fireblasttile, 0x03fc1c));
		com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.ALERT );
	}

	@Override
	public void damage(int dmg, Object src) {
		if (infireblast){
			dmg = (int)(dmg *0.34f);
		}
		else if (phasetransition2){
			//ok this is kind of cheating but we want the final phase to last a bit longer
			dmg = (int)(dmg *0.67f);
		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*2);
		super.damage(dmg, src);

		if (HP<(HT/3)*2 && phasetransition1 == false)
		{
			fireblastcooldown = 10;
			phasetransition1 = true;
			BlastFurnaceSprite.active = true;
			HP = (HT/3)*2;
			Buff.affect( this, Terror.class, 4f ).object = Dungeon.hero.id();
			Buff.affect(this, Stamina.class, 8f);
			yell(Messages.get(this, "scream"));
		}

		if (HP<(HT/3) && phasetransition2 == false)
		{
			fireblastcooldown = 4;
			phasetransition2 = true;
			HP = (HT/3);
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob instanceof BlastFurnace) mob.die(null);
			}

			Buff.affect( this, Terror.class, 4f ).object = Dungeon.hero.id();
			Buff.affect(this, Stamina.class, 8f);
			yell(Messages.get(this, "scream2"));
		}

	}

	@Override
	public float speed() {
		float newspeed = 1;
		if(phasetransition2) newspeed = 1.25f;
		return super.speed() * newspeed;
	}

	private int moving = 0;
	@Override
	protected boolean getCloser( int target ) {
		//this is used so that the crab remains slower, but still detects the player at the expected rate.
		if (infireblast) {
			moving++;
			if (moving < 3) {
				return super.getCloser(target);
			} else {
				moving = 0;
				return true;
			}
		}
		else return super.getCloser(target);
	}

	public void SetupParams()
	{
		if (Dungeon.level instanceof ForgeBossLevel) level = (ForgeBossLevel)Dungeon.level;
		level.boss = this;
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
				this.sprite.emitter().burst(Speck.factory(Speck.HEALING), 10);
				this.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(30));
				ch.die(this);
				Statistics.qualifiedForBossChallengeBadge = false;
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
		if ((HP * 3 <= HT)) BossHealthBar.bleed(true);
		if (state != SLEEPING) {

			Dungeon.level.seal();
		}

		if (state != HUNTING){
			if (Dungeon.hero.invisible <= 0){
				beckon(Dungeon.hero.pos);
			}
		}


	}

	private static Char throwingChar;

	public void BombattackLogic()
	{
		bombattackcooldown = 14;
        if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) bombattackcooldown -= 5;
		//this should always target the hero to prevent ally cheese
		ArrayList<Integer> validtiles = new ArrayList<Integer>();
		PathFinder.buildDistanceMap( Dungeon.hero.pos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				validtiles.add(i);
			}
		}
		int amount = Random.Int(2) + 2;
		Random.shuffle(validtiles);
		for (int validtile : validtiles) {
			if (amount > 0)
			{

				final Char thrower = this;
				final int finalTargetCell = validtile;
				throwingChar = thrower;
				final FurnaceGolem.BombAbility.BombItem item = new FurnaceGolem.BombAbility.BombItem();
				thrower.sprite.zap(finalTargetCell);
				((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
						reset(thrower.sprite,
								finalTargetCell,
								item,
								new com.watabou.utils.Callback() {
									@Override
									public void call() {
										item.onThrow(finalTargetCell);
										thrower.next();
									}
								});

				amount--;
			}
			else break;
		}



	}


	@Override
	public boolean act() {
		SetupParams();
		if (infireblast) FireblastLogic();
		if (bombattackcooldown < 1 && !infireblast) BombattackLogic();
		else if ((Random.Int(7) == 3) && !chargedattack)
		{
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "charge"));
			this.sprite.emitter().burst(FlameParticle.FACTORY, 15);

			chargedattack = true;
		}


		if (fireblastcooldown < 1 && !infireblast)
		{
			if (phasetransition1 && !phasetransition2) {
				sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "summon"));
				this.sprite.emitter().burst(FlameParticle.FACTORY, 15);

				ArrayList<BlastFurnace> Furnaces = new ArrayList<BlastFurnace>();
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (mob instanceof BlastFurnace && !((BlastFurnace) mob).animated) Furnaces.add((BlastFurnace)mob);
				}
				if (Furnaces.size() > 0) {
					Random.shuffle(Furnaces);
					Furnaces.get(0).animated = true;
				}
				fireblastcooldown = 35;
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) fireblastcooldown -= 6;

			}
			else {
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "explosion"));
			this.sprite.emitter().burst(FlameParticle.FACTORY, 15);
			infireblast = true;
			chargedattack = false;
			}
		}
		else if (Random.Int(6) != 3) fireblastcooldown--;

		if (Random.Int(6) != 3 && phasetransition2) bombattackcooldown--;
		if (bombattackcooldown < 1 && !infireblast)
		{
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "bombs"));
			this.sprite.emitter().burst(FlameParticle.FACTORY, 15);
		}


		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy);
	}

	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		if (!infireblast && chargedattack){
			chargedattack = false;
			accMulti = 50;
			Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(enemy, trajectory, 2, true, false, this);

			for (int i = 0; i < com.watabou.utils.PathFinder.NEIGHBOURS9.length; i++) {
				int b = com.watabou.utils.PathFinder.NEIGHBOURS9[i]+pos;
				GameScene.add(Blob.seed(b, 5, Fire.class));
				if (Random.Int(4) == 1 && (Dungeon.level.map[b] != Terrain.CHASM && Dungeon.level.map[b] != Terrain.WATER && Dungeon.level.map[b] != Terrain.EMPTY_SP && Dungeon.level.map[b] != Terrain.EMPTY_SP && Dungeon.level.passable[b])) {
					Level.set(b, Terrain.MAGMA_TILE);
					GameScene.updateMap(b);
				}
			}
		}
		return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
	}



	public void onZapComplete(){
		next();
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

		GameScene.bossSlain();
		Dungeon.level.unseal();

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

        if (cause instanceof MagesStaff) cause = ((MagesStaff) cause).wand;

        if (Dungeon.hero.HP < (Dungeon.hero.HT * 0.3f) && cause instanceof Wand && ((Wand) cause).curCharges < 2 && infireblast) {
        } else Statistics.qualifiedForBossChallengeBadge = false;


        Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge) {
			Badges.validateBossChallengeCompleted();
		}
        Statistics.bossScores[2] += 3000;


        for (Heap heap : Dungeon.level.heaps.valueList()){
                for (Item item : heap.items){
                    if ((item instanceof BombAbility.BombItem)) {
                        heap.destroy();
                    }
                }

            }

		yell(Messages.get(this, "defeated"));
	}

	@Override
	public void notice() {
		super.notice();

	}

	{
		immunities.add(Sleep.class);

		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
        resistances.add(Slow.class);
        resistances.add(Roots.class);
		immunities.add(Fire.class);
        immunities.add(Burning.class);
        immunities.add(Smog.class);
	}

	private static String PHASETRANSITION1 = "PHASETRANSITION1";

	private static String PHASETRANSITION2 = "PHASETRANSITION2";

	@Override
	public void storeInBundle(Bundle bundle) {

		super.storeInBundle(bundle);

		bundle.put(FIREBLASTCOOLDOWN, fireblastcooldown);
		bundle.put(PHASETRANSITION1, phasetransition1);
		bundle.put(PHASETRANSITION2, phasetransition2);
		bundle.put(INFIREBLAST, infireblast);
		bundle.put(CHARGEDATTACK, chargedattack);
		bundle.put(BOMBATTACKCOOLDOWN, bombattackcooldown);
		bundle.put(FIREBLASTTIMER, fireblasttimer);
		bundle.put(FIREBLASTTILE, fireblasttile);

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		fireblastcooldown = bundle.getInt( FIREBLASTCOOLDOWN );
		phasetransition1 = bundle.getBoolean( PHASETRANSITION1 );
		phasetransition2 = bundle.getBoolean( PHASETRANSITION2 );
		infireblast = bundle.getBoolean( INFIREBLAST );
		chargedattack = bundle.getBoolean( CHARGEDATTACK );
		bombattackcooldown = bundle.getInt( BOMBATTACKCOOLDOWN );
		fireblasttimer = bundle.getInt( FIREBLASTTIMER );
		fireblasttile = bundle.getInt( FIREBLASTTILE );


		BossHealthBar.assignBoss(this);
		if ((HP * 3 <= HT)) BossHealthBar.bleed(true);


	}

	public static class BombAbility extends Buff {

		public int bombPos = -1;
		private int timer = Random.Int(3) + 1;

		private int range = 1;

		private ArrayList<com.watabou.noosa.particles.Emitter> smokeEmitters = new ArrayList<>();

		@Override
		public boolean act() {

			if (smokeEmitters.isEmpty()){
				fx(true);
			}


			com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
			for (int cell = 0; cell < com.watabou.utils.PathFinder.distance.length; cell++) {

				if (com.watabou.utils.PathFinder.distance[cell] < Integer.MAX_VALUE) {
					Char ch = Actor.findChar(cell);
					if (ch != null && ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.CAUTION_TO_THE_WIND)) {

						Buff.prolong(Dungeon.hero, Recharging.class, Dungeon.hero.pointsInTalent(Talent.CAUTION_TO_THE_WIND));

					}
				}
			}


			com.watabou.utils.PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
			if (timer == 4){
				FloatingText.show(p.x, p.y, bombPos, "4...", CharSprite.WARNING);
			} else if (timer == 3) {
				FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.WARNING);
			} else if (timer == 2){
				FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
			} else if (timer == 1){
				FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.WARNING);
			} else {
				com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
				for (int cell = 0; cell < com.watabou.utils.PathFinder.distance.length; cell++) {

					if (com.watabou.utils.PathFinder.distance[cell] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(cell);
						if (ch != null && !(ch instanceof FurnaceGolem)) {
							int dmg = com.watabou.utils.Random.NormalIntRange(10, 30);
							dmg -= ch.drRoll();

							if (dmg > 0) {
								ch.damage(dmg, this);
							}


						}
					}

				}

				Heap h = Dungeon.level.heaps.get(bombPos);
				if (h != null) {
					for (Item i : h.items.toArray(new Item[0])) {
						if (i instanceof FurnaceGolem.BombAbility.BombItem) {
							h.remove(i);
						}
					}
				}
				com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.BLAST);
				detach();
				return true;
			}

			timer--;
			spend(Actor.TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && bombPos != -1){
				com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
				for (int i = 0; i < com.watabou.utils.PathFinder.distance.length; i++) {
					if (com.watabou.utils.PathFinder.distance[i] < Integer.MAX_VALUE) {
						com.watabou.noosa.particles.Emitter e = CellEmitter.get(i);
						e.pour( SmokeParticle.FACTORY, 0.25f );
						smokeEmitters.add(e);
					}
				}
			} else if (!on) {
				for (com.watabou.noosa.particles.Emitter e : smokeEmitters){
					e.burst(BlastParticle.FACTORY, 4);
				}
			}
		}

		private static final String BOMB_POS = "bomb_pos";
		private static final String TIMER = "timer";

		@Override
		public void storeInBundle(com.watabou.utils.Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BOMB_POS, bombPos );
			bundle.put( TIMER, timer );
		}

		@Override
		public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
			super.restoreFromBundle(bundle);
			bombPos = bundle.getInt( BOMB_POS );
			timer = bundle.getInt( TIMER );
		}

		public static class BombItem extends Item {

			{
				dropsDownHeap = true;
				unique = true;

				image = ItemSpriteSheet.BOMB;
			}

			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}

			@Override
			public void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, FurnaceGolem.BombAbility.class).bombPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, FurnaceGolem.BombAbility.class).bombPos = cell;
				}
			}


		}
	}



}
