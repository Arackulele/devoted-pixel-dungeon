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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HellEyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DemonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class HellEye extends Mob implements Callback {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = HellEyeSprite.class;
		
		HP = HT = 20;
		defenseSkill = 8;
		
		EXP = 6;
		maxLvl = 13;
		

		alignment = Alignment.ALLY;
		
		properties.add(Property.ELECTRIC);
		properties.add(Property.INORGANIC);
	}
	private static final String HEROID	= "hero_id";
	private int heroID;
	private Hero hero;

	private int Hpcurrent;

	private int max;

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		heroID = bundle.getInt( HEROID );
	}


	public void summonEye( Hero hero) {
		this.hero = hero;
		Hpcurrent = this.hero.HP;
		max = this.hero.HT;
		heroID = this.hero.id();
		this.HP = hero.lvl*(8)+hero.pointsInTalent(Talent.DIVINE_CONNECTION)*12;
		HT = hero.lvl*(8)+hero.pointsInTalent(Talent.DIVINE_CONNECTION)*12;
	}

	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 5+(max/Hpcurrent)/8, 8+(max/Hpcurrent));
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (hero.hasTalent(Talent.CHAOS_WITHIN)) {
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				Char ch = findChar(pos + PathFinder.NEIGHBOURS8[i]);
				if (ch != null && ch.isAlive()) {
					int damage = Math.round(Random.NormalIntRange(6, 12));
					damage = Math.round(damage * AscensionChallenge.statModifier(this));
					damage = Math.max(0, 10*hero.pointsInTalent(Talent.CHAOS_WITHIN));
					ch.damage(damage, this);

				}
			}
		}

		Sample.INSTANCE.play( Assets.Sounds.BONES );


	}

	@Override
	public int attackSkill( Char target ) {
		return 11;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange( (8+(max/Hpcurrent)/4)*(int)(1+hero.pointsInTalent(Talent.ALL_SEEING)*0.2), (15+(max/Hpcurrent)/2)*(int)(1+hero.pointsInTalent(Talent.ALL_SEEING)*0.2) );
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt{}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.distance( pos, enemy.pos ) <= 1) {
			
			return super.doAttack( enemy );
			
		} else {
			
			spend( TIME_TO_ZAP );

			Invisibility.dispel(this);
			if (hit( this, enemy, true )) {
				int dmg = Random.NormalIntRange( (8+(max/Hpcurrent)/4)*(int)(1+hero.pointsInTalent(Talent.ALL_SEEING)*0.2), (15+(max/Hpcurrent)/2)*(int)(1+hero.pointsInTalent(Talent.ALL_SEEING)*0.2));
				dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
				enemy.damage( dmg, new LightningBolt() );

				if (enemy.sprite.visible) {
					enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
					enemy.sprite.flash();
				}
				
				if (enemy == Dungeon.hero) {
					
					Camera.main.shake( 2, 0.3f );
					
					if (!enemy.isAlive()) {
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail( getClass() );
						GLog.n( Messages.get(this, "zap_kill") );
					}
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public void call() {
		next();
	}
	
}
