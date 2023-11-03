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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CourtCurse;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.YuriaSprite;
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

public class Yuria extends Mob implements Callback {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  800 : 600;
		EXP = 10;
		defenseSkill = 12;
		spriteClass = YuriaSprite.class;
		baseSpeed = 1.5f;

		flying = true;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);

	}

	@Override
	public int attackProc( Char enemy, int damage ) {

		int newdmg = damage;

		if (enemy.buff(CourtCurse.class) !=null) {
			Buff.detach( enemy, CourtCurse.class );
			newdmg = damage*2;
		}

		return super.attackProc( enemy, newdmg );
	}


	@Override
	protected boolean canAttack( Char enemy ) {

		if (closein == false) return (new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos && !Dungeon.level.adjacent( pos, enemy.pos ));
		else return super.canAttack(enemy);

	}

	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {

			if (Random.Int( 2 ) == 0) closein = false;

			return super.doAttack( enemy );

		} else if( closein == false) {

			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
		else return false;
	}

	public static class DarkBolt{}

	protected void zap() {
		spend( 2f );

		Invisibility.dispel(this);
		if (hit( this, enemy, true )) {
			if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
				Buff.prolong( enemy, Hex.class, 4f );
				Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}

			int dmg = Random.NormalIntRange( 9, 13 );
			if (enemy.buff(CourtCurse.class) !=null) {
				Buff.detach( enemy, CourtCurse.class );
				dmg = dmg*2;
			}
			enemy.damage( dmg, new DarkBolt() );

			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void call() {
		next();
	}


	@Override
	protected boolean act() {

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		if (!level.boss.isAlive() || !level.wendar.isAlive()) die(null);


		if (Actor.findChar(target) == null) target = Dungeon.hero.pos;

		if (closein == false && Dungeon.level.distance(target, pos) > 3 && Random.Int(100) > 90)
		{
			closein = true;
		}



		return super.act();

	}

	CitadelBossLevel level;

	boolean closein = false;

	@Override
	public void damage(int dmg, Object src) {

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*2);

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		super.damage(dmg/2, src);
		level.boss.HP=this.HP;
		level.wendar.HP=this.HP;
	}

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING && closein == false && Dungeon.level.distance(target, pos) < 3) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
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
		return Random.NormalIntRange(0, 9);
	}


	{
		immunities.add(Sleep.class);
		immunities.add(Paralysis.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
		immunities.add(Fire.class);
		immunities.add(Smog.class);
	}


	private static final String CLOSEIN = "CLOSEIN";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CLOSEIN, closein);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		closein = bundle.getBoolean( CLOSEIN );
	}


}
