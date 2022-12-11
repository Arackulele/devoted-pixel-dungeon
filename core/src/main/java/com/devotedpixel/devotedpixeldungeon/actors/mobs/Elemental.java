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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.levels.ForgeBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CoalElementalSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Elemental extends Mob {

	{
		HP = HT = 60;
		defenseSkill = 20;
		
		EXP = 10;
		maxLvl = 20;
		
		flying = true;
	}

	private boolean summonedALly;
	
	@Override
	public int damageRoll() {
		if (!summonedALly) {
			return Random.NormalIntRange(20, 25);
		} else {
			int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
			return Random.NormalIntRange(5*regionScale, 5 + 5*regionScale);
		}
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (!summonedALly) {
			return 25;
		} else {
			int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
			return 5 + 5*regionScale;
		}
	}

	public void setSummonedALly(){
		summonedALly = true;
		//sewers are prison are equivalent, otherwise scales as normal (2/2/3/4/5)
		int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
		defenseSkill = 5*regionScale;
		HT = 15*regionScale;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}
	
	protected int rangedCooldown = Random.NormalIntRange( 3, 5 );
	
	@Override
	protected boolean act() {
		if (state == HUNTING){
			rangedCooldown--;
		}
		
		return super.act();
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		if (rangedCooldown <= 0) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
		} else {
			return super.canAttack( enemy );
		}
	}
	
	protected boolean doAttack( Char enemy ) {
		
		if (Dungeon.level.adjacent( pos, enemy.pos ) || rangedCooldown > 0) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		meleeProc( enemy, damage );
		
		return damage;
	}
	
	private void zap() {
		spend( 1f );

		Invisibility.dispel(this);
		if (hit( this, enemy, true )) {
			
			rangedProc( enemy );
			
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}

		rangedCooldown = Random.NormalIntRange( 3, 5 );
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void add( Buff buff ) {
		if (harmfulBuffs.contains( buff.getClass() )) {
			damage( Random.NormalIntRange( HT/2, HT * 3/5 ), buff );
		} else {
			super.add( buff );
		}
	}
	
	protected abstract void meleeProc( Char enemy, int damage );
	protected abstract void rangedProc( Char enemy );
	
	protected ArrayList<Class<? extends Buff>> harmfulBuffs = new ArrayList<>();
	
	private static final String COOLDOWN = "cooldown";
	private static final String SUMMONED_ALLY = "summoned_ally";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, rangedCooldown );
		bundle.put( SUMMONED_ALLY, summonedALly);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (bundle.contains( COOLDOWN )){
			rangedCooldown = bundle.getInt( COOLDOWN );
		}
		summonedALly = bundle.getBoolean( SUMMONED_ALLY );
		if (summonedALly){
			setSummonedALly();
		}
	}
	
	public static class FireElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Fire.class;
			
			loot = new PotionOfLiquidFlame();
			lootChance = 1/8f;
			
			properties.add( Property.FIERY );
			
			harmfulBuffs.add( com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost.class );
			harmfulBuffs.add( Chill.class );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 2 ) == 0 && !Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			if (!Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy, 4f );
			}
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}

	public static class CoalElemental extends FireElemental {


		{
			spriteClass = CoalElementalSprite.class;

			HP = HT = 50;
			defenseSkill = 17;

			if (Dungeon.level instanceof ForgeBossLevel) {

				maxLvl = -2;

			}
			else {
				EXP = 9;
				maxLvl = 17;

				loot = Generator.Category.WAND;
				lootChance = 0.03f; //initially, see lootChance()
			}

			properties.add( Property.FIERY );

			rangedCooldown = Integer.MAX_VALUE;

			harmfulBuffs.add( com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost.class );
			harmfulBuffs.add( Chill.class );
		}

		private int pumpedUp;

		@Override
		protected boolean canAttack( Char enemy ) {
			if (pumpedUp > 0){
				//we check both from and to in this case as projectile logic isn't always symmetrical.
				//this helps trim out BS edge-cases
				return Dungeon.level.distance(enemy.pos, pos) <= 2
						&& new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos
						&& new Ballistica( enemy.pos, pos, Ballistica.PROJECTILE).collisionPos == pos;
			} else {
				return super.canAttack(enemy);
			}
		}

		@Override
		public float lootChance() {
			//each drop makes future drops 1/3 as likely
			// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
			return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.SHAMAN_WAND.count);
		}

		@Override
		public Item createLoot() {
			Dungeon.LimitedDrops.SHAMAN_WAND.count++;
			return super.createLoot();
		}

		@Override
		public void updateSpriteState() {
			super.updateSpriteState();

			if (pumpedUp > 0){
				((CoalElementalSprite)sprite).pumpUp( pumpedUp );
			}
		}
		@Override
		public int damageRoll() {

			if (pumpedUp > 0) {
				pumpedUp = 0;
				return Random.NormalIntRange( 20, 40 );
			}

			else return Random.NormalIntRange( 2, 30 );
		}

		@Override
		protected boolean doAttack( Char enemy ) {
			if (pumpedUp == 1) {
				pumpedUp++;
				((CoalElementalSprite)sprite).pumpUp( pumpedUp );

				spend( attackDelay() );

				return true;
			} else if (pumpedUp >= 2 || Random.Int(2) > 0) {

				boolean visible = Dungeon.level.heroFOV[pos];

				if (visible) {
					if (pumpedUp >= 2) {
						((CoalElementalSprite) sprite).pumpAttack();
					} else {
						sprite.attack(enemy.pos);
					}
				} else {
					if (pumpedUp >= 2){
						((CoalElementalSprite)sprite).triggerEmitters();
					}
					attack( enemy );
					Invisibility.dispel(this);
					spend( attackDelay() );
				}

				return !visible;

			} else {


					pumpedUp++;
					spend( attackDelay() );

				((CoalElementalSprite)sprite).pumpUp( pumpedUp );



				return true;
			}
		}

		@Override
		public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {
			boolean result = super.attack( enemy, dmgMulti, dmgBonus, accMulti );
			if (pumpedUp > 0) {
				pumpedUp = 0;

			}
			return result;
		}

		ForgeBossLevel level;

		@Override
		public float speed() {
			float newspeed = 1;
			if (Dungeon.level instanceof ForgeBossLevel)
			{
				level = (ForgeBossLevel)Dungeon.level;
				newspeed = 2f;
			}
			return super.speed() * newspeed;
		}

		@Override
		protected boolean getCloser( int target ) {

			if (Dungeon.level instanceof ForgeBossLevel)
			{

				level = (ForgeBossLevel)Dungeon.level;

				Mob golem = level.boss;
				target = golem.pos;

			}

			if (pumpedUp != 0) {
				pumpedUp = 0;
				sprite.idle();
			}



			return super.getCloser( target );
		}

		@Override
		protected boolean getFurther(int target) {
			if (pumpedUp != 0) {
				pumpedUp = 0;
				sprite.idle();
			}
			return super.getFurther( target );
		}
		@Override
		public int attackSkill( Char target ) {
			return 20;
		}

		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 5);
		}
	}
	
	//used in wandmaker quest
	public static class NewbornFireElemental extends FireElemental {
		
		{
			spriteClass = ElementalSprite.NewbornFire.class;
			
			HT = 60;
			HP = HT/2; //30
			
			defenseSkill = 12;
			
			EXP = 7;
			
			properties.add(Property.MINIBOSS);

			//newborn elementals do not have ranged attacks
			rangedCooldown = Integer.MAX_VALUE;
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
			if (alignment == Alignment.ENEMY) {
				Dungeon.level.drop( new Embers(), pos ).sprite.drop();
				Statistics.questScores[1] = 2000;
			}
		}

		@Override
		public boolean reset() {
			return true;
		}
		
	}

	//not a miniboss, fully HP, otherwise a newborn elemental
	public static class AllyNewBornElemental extends NewbornFireElemental {

		{
			HP = HT;
			properties.remove(Property.MINIBOSS);
		}

		@Override
		public boolean reset() {
			return false;
		}

	}
	
	public static class FrostElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Frost.class;
			
			loot = new PotionOfFrost();
			lootChance = 1/8f;
			
			properties.add( Property.ICY );
			
			harmfulBuffs.add( Burning.class );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 3 ) == 0 || Dungeon.level.water[enemy.pos]) {
				Freezing.freeze( enemy.pos );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Freezing.freeze( enemy.pos );
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}
	
	public static class ShockElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Shock.class;
			
			loot = new ScrollOfRecharging();
			lootChance = 1/4f;
			
			properties.add( Property.ELECTRIC );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			ArrayList<Char> affected = new ArrayList<>();
			ArrayList<Lightning.Arc> arcs = new ArrayList<>();
			Shocking.arc( this, enemy, 2, affected, arcs );
			
			if (!Dungeon.level.water[enemy.pos]) {
				affected.remove( enemy );
			}
			
			for (Char ch : affected) {
				ch.damage( Math.round( damage * 0.4f ), this );
			}

			boolean visible = sprite.visible || enemy.sprite.visible;
			for (Char ch : affected){
				if (ch.sprite.visible) visible = true;
			}

			if (visible) {
				sprite.parent.addToFront(new Lightning(arcs, null));
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Buff.affect( enemy, Blindness.class, Blindness.DURATION/2f );
			if (enemy == Dungeon.hero) {
				GameScene.flash(0x80FFFFFF);
			}
		}
	}
	
	public static class ChaosElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Chaos.class;
			
			loot = new ScrollOfTransmutation();
			lootChance = 1f;
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			CursedWand.cursedEffect(null, this, enemy);
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			CursedWand.cursedEffect(null, this, enemy);
		}
	}
	
	public static Class<? extends Elemental> random(){
		if (Random.Int( 50 ) == 0){
			return ChaosElemental.class;
		}
		
		float roll = Random.Float();
		if (roll < 0.4f){
			return FireElemental.class;
		} else if (roll < 0.8f){
			return FrostElemental.class;
		} else {
			return ShockElemental.class;
		}
	}
}
