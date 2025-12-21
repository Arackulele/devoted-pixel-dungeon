/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RevenantSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Revenant extends Banshee implements Callback {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = RevenantSprite.class;

		HP = HT = 38;
		defenseSkill = 20;

		EXP = 6;
		maxLvl = 13;

		loot = PhantomMeat.class;
		lootChance = 1f;

		flying = true;

		properties.add(Char.Property.UNDEAD);
		properties.add(Char.Property.INORGANIC);
	}

	protected void zap() {
		spend( TIME_TO_ZAP );

		Invisibility.dispel(this);
		Char enemy = this.enemy;
		if (Char.hit( this, enemy, true )) {
			if (enemy == Dungeon.hero) {
				Buff.append(enemy, TalismanOfForesight.CharAwareness.class, 5).charID = this.id();
			}
			Buff.affect(enemy, Blindness.class, 5f);

			enemy.sprite.showStatus( CharSprite.NEUTRAL, Messages.get(this, "petrify") );
			
			int dmg = Random.NormalIntRange( 5, 14 );
			dmg = Math.round(dmg * AscensionChallenge.statModifier(this));

			enemy.damage( dmg, new BansheeStare() );
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( this );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}

	@Override
	public void damage(int dmg, Object src) {



		int newHP = HP - dmg;
		//Teleport away once at half HP
		//This enemy would be much too easy otherwise
		if ( HP * 2 > HT && newHP * 2 <= HT )ScrollOfTeleportation.teleportChar( this );

		super.damage(dmg, src);
	}
	
	@Override
	public void call() {
		next();
	}

}
