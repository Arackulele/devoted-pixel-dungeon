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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.CitadelBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WendarSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YuriaSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

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

		level.yuria = this;

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		if (!level.boss.isAlive() || !level.wendar.isAlive()) die(null);


		if (Actor.findChar(target) == null) target = Dungeon.hero.pos;

		if (closein == false && Dungeon.level.distance(target, pos) > 3 && Random.Int(100) > 85)
		{
			closein = true;
		}

		Thymor thym = (Thymor) level.boss;
		if (thym.YWCombo && Dungeon.level.distance(level.wendar.pos, pos) < 2)
		{

			Sample.INSTANCE.play(Assets.Sounds.DEGRADE, 1f, 0.5f);
			sprite.play(((YuriaSprite)sprite).cast );


			int Tile = Dungeon.hero.pos;
			PathFinder.buildDistanceMap(Tile, com.watabou.utils.BArray.not(Dungeon.level.solid, null), 3);
			for (int i = 0; i < PathFinder.distance.length; i++) {

				if (Dungeon.level.distance(Tile, i) == 2) {
					GameScene.add(Blob.seed(i, 10, WallOfLight.LightWall.class));
				}

			}


			thym.YWCombo = false;
			spend(TICK);
			return true;
		}

		return super.act();

	}

	CitadelBossLevel level;

	boolean closein = false;

	@Override
	public void damage(int dmg, Object src) {

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		super.damage(dmg/2, src);
		level.boss.HP=this.HP;
		level.wendar.HP=this.HP;
	}

	@Override
	protected boolean getCloser( int target ) {
		if (Dungeon.level instanceof CitadelBossLevel) {
			level = (CitadelBossLevel) Dungeon.level;
		}
		Thymor thym = (Thymor) level.boss;
		if (thym.TYCombo || thym.TripleCombo) target = thym.pos;
		if (thym.YWCombo) target = level.wendar.pos;
		if (thym.YWCombo || thym.TYCombo || thym.TripleCombo) return super.getCloser(target);


		if (state == HUNTING && closein == false && Dungeon.level.distance(target, pos) < 3 && (!thym.TYCombo && !thym.YWCombo)) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 10, 19 );
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
