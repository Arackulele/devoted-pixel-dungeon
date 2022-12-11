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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollBrawlerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import java.util.ArrayList;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class TrollBrawler extends Mob {
	
	{
		spriteClass = TrollBrawlerSprite.class;

		HP = HT = 40;
		defenseSkill = 22;

		EXP = 10;
		maxLvl = 18;

		loot = new SmallRation();
		lootChance = 0.125f;

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

	public void onZapComplete(){
		Dungeon.hero.damage(newdmg, this);
		next();
	}

	public int newdmg;

	@Override
	public int defenseProc( Char enemy, int damage ) {

		newdmg = damage;

		if (enemy == Dungeon.hero)
		{
			Hero hero = (Hero)enemy;
			if (hero.belongings.thrownWeapon != null && hero.belongings.thrownWeapon instanceof MissileWeapon) {
			newdmg = damage/2;

			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "deflect"));

				sprite.zap( enemy.pos );
			}

		}


		return super.defenseProc( enemy, newdmg );

	}


	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}
}
