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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BrawlerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Random;

public abstract class TrollBrawler extends Mob {
	
	{
		spriteClass = BrawlerSprite.class;

		HP = HT = 40;
		defenseSkill = 22;

		EXP = 10;
		maxLvl = 18;

		loot = new SmallRation();
		lootChance = 0.125f;

		immunities.add(Burning.class);

	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 6, 12 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 26;
	}


	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	public int cooldown = 0;

	public String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COOLDOWN, cooldown);
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		cooldown = bundle.getInt(COOLDOWN);
		super.restoreFromBundle(bundle);
	}

	@Override
	protected boolean act() {

		if (cooldown > 0) {
			cooldown--;

			if (cooldown < 1) sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollBrawler.class, "charged"));
		}

		return super.act();
	}

	@Override
	public String description() {
		return super.description() + "\n\n" + Messages.get(this, "specialtype");
	}

	public static class YellowBrawler extends TrollBrawler {
		{
			spriteClass = BrawlerSprite.Yellow.class;
		}

		public void onZapComplete(){
			Dungeon.hero.damage(newdmg, this);
			next();
		}
		public int newdmg;

		@Override
		public int defenseProc( Char enemy, int damage ) {
			newdmg = damage;
			if (enemy == Dungeon.hero && cooldown < 1)
			{
				Hero hero = (Hero)enemy;
				if (hero.belongings.thrownWeapon != null && hero.belongings.thrownWeapon instanceof MissileWeapon) {
					newdmg = damage/2;
					sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollBrawler.class, "deflect"));
					cooldown += 3;
					sprite.zap( enemy.pos );
					return 0;
				}


			}
			return super.defenseProc( enemy, newdmg );
		}


	}

	public static class RedBrawler extends TrollBrawler {
		{
			spriteClass = BrawlerSprite.Red.class;
		}
		public int newdmg;
		@Override
		public int defenseProc( Char enemy, int damage ) {
			newdmg = damage;
			if (cooldown < 1)
			{
				if (Dungeon.hero.belongings.thrownWeapon == null) {
					newdmg = damage/2;
					sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollBrawler.class, "deflect"));
					cooldown += 11;
					enemy.damage(newdmg - enemy.drRoll(), this);
					return 0;
				}
			}
			return super.defenseProc( enemy, newdmg );
		}


	}

	public static class PurpleBrawler extends TrollBrawler {
		{
			spriteClass = BrawlerSprite.Purple.class;
		}

		public int newdmg = 0;

		protected void zap() {
			Invisibility.dispel(this);
			if (Char.hit( this, enemy, true )) {

				int dmg = newdmg;
				dmg = Math.round(dmg * AscensionChallenge.statModifier(this));

				if (enemy == Dungeon.hero && !enemy.isAlive()) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "bolt_kill") );
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
		}

		public void onZapComplete() {
			enemy.damage( newdmg, this );
			zap();
			next();
		}

		@Override
		public void damage( int dmg, Object src ){

			if ((src instanceof Wand || src instanceof ClericSpell) && cooldown < 1) {
				sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollBrawler.class, "deflect"));
				newdmg = dmg/2;
				cooldown += 5;
				enemy = Dungeon.hero;
				if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
					sprite.zap( enemy.pos );
				} else {
					zap();
				}
				//com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			}
			else super.damage( dmg, src );
			}
		}

	public static Class<? extends TrollBrawler> random(){
		float roll = Random.Float();
		if (roll < 0.4f){
			return TrollBrawler.YellowBrawler.class;
		} else if (roll < 0.8f){
			return TrollBrawler.PurpleBrawler.class;
		} else {
			return TrollBrawler.RedBrawler.class;
		}
	}


}
