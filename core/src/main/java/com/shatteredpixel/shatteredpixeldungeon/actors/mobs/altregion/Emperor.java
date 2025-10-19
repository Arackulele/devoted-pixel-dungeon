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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.StaffSplinter;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EmperorSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
//import com.shatteredpixel.shatteredpixeldungeon.items.quest.StaffSplinter;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Emperor extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 120 : 90;
		EXP = 10;
		defenseSkill = 7;
		spriteClass = EmperorSprite.class;

		properties.add(Char.Property.BOSS);
		properties.add(Char.Property.DEMONIC);
		properties.add(Char.Property.ACIDIC);
	}


	public boolean explodeattack = false;

	public int explodeturn = 0;

	public int explodetimer = 10;

	@Override
	public int damageRoll() {
		int min = 2;
		int max = (HP*2 <= HT) ? 11 : 7;

			return Random.NormalIntRange( min, max );

	}

	@Override
	public int attackSkill( Char target ) {
		int attack = 10;
		if (HP*2 <= HT) attack = 15;
		return attack;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return (int)(super.defenseSkill(enemy) * ((HP*2 <= HT)? 1.5 : 1));
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 2);
	}

	public int Tile;

	public ArrayList<Integer> OuterTiles = new ArrayList<Integer>();

	public ArrayList<Emitter> CellEmitters = new ArrayList<Emitter>();

	@Override
	public boolean act() {


		if (state != SLEEPING){
			Dungeon.level.seal();
		}

		if (distance(Dungeon.hero) < 3) {
			if (explodetimer < 1 && explodeattack == false && enemy != null) {
				explodeattack = true;
				explodeturn = 0;
				boolean tped = false;

				Tile = Dungeon.hero.pos;
				PathFinder.buildDistanceMap(Tile, com.watabou.utils.BArray.not(Dungeon.level.solid, null), 3);
				for (int i = 0; i < PathFinder.distance.length; i++) {

					if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))
					{

						if (Dungeon.level.distance(Tile, i) == 1) {
							OuterTiles.add(i);
							CellEmitters.add(CellEmitter.get(i));
						}

					}

					if (Dungeon.level.distance(Tile, i) == 2) {
						OuterTiles.add(i);
						CellEmitters.add(CellEmitter.get(i));
					}
					if (Dungeon.level.distance(Tile, i) == 3 && !tped && Dungeon.level.passable[i]) {
						ScrollOfTeleportation.teleportToLocation( this, i );
						tped = true;
					}


				}

			}
			else if (explodeattack == false) explodetimer--;
		}
		if (explodeattack)
		{

			ArrayList<Integer> mid = new ArrayList<Integer>();
			mid.add(Tile);
			CellEmitters.add(CellEmitter.get(Tile));

			//if (smokeEmitters.isEmpty()){
			//	fx(true);
			//}
			if (explodeturn == 0) {
				ActivateParticleArea(mid, 1);
				InitializeParticles(true);
				InitializeParticles(true);

			} else if (explodeturn == 1){

			ActivateParticleArea(OuterTiles, 1);
			ExplodeMiddleTile();

			} else if (explodeturn == 2){

			ExplodeOuterTiles();
			ActivateParticleArea(OuterTiles, 1);
			ActivateParticleArea(mid, 1);

			} else if (explodeturn == 3){

			ExplodeMiddleTile();
			ExplodeOuterTiles();
				InitializeParticles(false);
				InitializeParticles(false);
			explodetimer = 10;
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) explodetimer -= com.watabou.utils.Random.NormalIntRange(0, 5);
			explodeattack = false;
			OuterTiles = new ArrayList<>();
			}
			explodeturn++;

		}

		return super.act();
	}

	public void ExplodeMiddleTile()
	{

		Char ch = Actor.findChar(Tile);
		if (ch != null && ch.alignment != alignment) {
			//ToDo: Badder Bosses/Enraged Damage
			int dmg = com.watabou.utils.Random.NormalIntRange(8, 18);

			WandOfMagicMissile m = new WandOfMagicMissile();
			ch.damage(dmg, m);
		}

		ArrayList<Integer> mid = new ArrayList<Integer>();
		mid.add(Tile);

		ActivateParticleArea(mid, 2);

	}

	public void ExplodeOuterTiles()
	{

		for(int I : OuterTiles) {
			Char ch = Actor.findChar(I);
			if (ch != null && ch.alignment != alignment) {
				//ToDo: Badder Bosses/Enraged Damage
				int dmg = com.watabou.utils.Random.NormalIntRange(8, 18);

				WandOfMagicMissile m = new WandOfMagicMissile();
				ch.damage(dmg, m);
			}
		}

		ActivateParticleArea(OuterTiles, 2);

	}

	//type: 0=dormant, 1=active, 2=explodeandclear
	public void ActivateParticleArea(ArrayList<Integer> input, int type)
	{

		//ToDo: Fix this
		for(int i : input) {
			com.watabou.noosa.particles.Emitter e = CellEmitter.get(i);;
			if (type == 1) sprite.parent.add(new TargetedCell(i, 0xFF0000));
			if (type == 2) e.burst(MagicMissile.WardParticle.FACTORY, 8);
			if (type == 3) {
				e.on = false;
				e.revive();
			}
		}

	}

	public void InitializeParticles(boolean start)
	{
		for(Emitter e : CellEmitters)
		{
			if (start)e.pour(MagicMissile.MagicParticle.FACTORY, 0.2f);
			else e.on = false;
		}

		if (!start)CellEmitters.clear();

	}



	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int( 3 ) == 0) {
			if (HP*2 <= HT) {

				this.HP += damage;
				this.sprite.emitter().burst(Speck.factory(Speck.HEALING), damage);
				this.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(damage));
				enemy.sprite.burst( 0x00CC6666, 5 );
			}

		}


		return damage;
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();

		if (explodeattack == true){
			sprite.attack(Dungeon.hero.pos);
		}
	}

	@Override
	protected boolean getCloser( int target ) {
		if (explodeattack == true) {
			sprite.idle();
			return true;
		}
		else return super.getCloser( target );
	}

	@Override
	protected boolean getFurther(int target) {
		if (explodeattack) {
			sprite.idle();
		}
		return super.getFurther( target );
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!BossHealthBar.isAssigned()){
			BossHealthBar.assignBoss( this );
			Dungeon.level.seal();
		}
		boolean bleeding = (HP*2 <= HT);
		super.damage(dmg, src);
		if ((HP*2 <= HT) && !bleeding){
			BossHealthBar.bleed(true);
			sprite.showStatus(CharSprite.WARNING, Messages.get(this, "enraged"));
			yell(Messages.get(this, "gluuurp"));
		}
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmg);
			else                                                    lock.addTime(dmg*1.5f);
		}
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		Dungeon.level.unseal();

		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();

		//60% chance of 2 blobs, 30% chance of 3, 10% chance for 4. Average of 2.5
		int blobs = Random.chances(new float[]{0, 0, 6, 3, 1});
		for (int i = 0; i < blobs; i++){
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop( new StaffSplinter(), pos + ofs ).sprite.drop( pos );
		}

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge){
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[0] += 1000;

		yell( Messages.get(this, "defeated") );
	}

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			Dungeon.level.seal();
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}

	private final String EXPLODEATTACK = "explodeattack";

	private final String EXPLODETURN = "explodeturn";

	private final String EXPLODETIMER = "explodetimer";

	private final String TILE = "tile";

	private final String OUTERTILES = "outertiles";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		//thanks stack overflow, java streams are hard
		if (OuterTiles != null && OuterTiles.size() > 0)
		{
		int[] tempconversion = OuterTiles.stream().mapToInt(i->i).toArray();
		bundle.put( OUTERTILES, tempconversion );
		}

		bundle.put( EXPLODEATTACK , explodeattack );
		bundle.put( EXPLODETURN , explodeturn );
		bundle.put( EXPLODETIMER , explodetimer );
		bundle.put( TILE, Tile );

	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		super.restoreFromBundle( bundle );

		explodeattack = bundle.getBoolean( EXPLODEATTACK );
		explodeturn = bundle.getInt( EXPLODEATTACK );
		explodetimer = bundle.getInt( EXPLODETIMER );

		int[] tempconversion = new int[0];

		if (bundle.getIntArray( OUTERTILES ) != null)tempconversion = bundle.getIntArray( OUTERTILES );

		//???? this is messy
		int[] finalTempconversion = tempconversion;
		OuterTiles = new ArrayList<Integer>() {{ for (int i : finalTempconversion) add(i); }};

		if (state != SLEEPING) BossHealthBar.assignBoss(this);
		if ((HP*2 <= HT)) BossHealthBar.bleed(true);
	}
	
}
