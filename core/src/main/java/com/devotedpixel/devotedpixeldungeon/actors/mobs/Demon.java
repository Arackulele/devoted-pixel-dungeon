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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DemonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Demon extends Mob {

	{
		spriteClass = DemonSprite.class;

		HP = HT = 10;
		defenseSkill = 5;
		alignment = Alignment.ALLY;


	}

	private static final String HEROID	= "hero_id";
	private int heroID;
	private Hero hero;

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		heroID = bundle.getInt( HEROID );
	}


	public void summonDemon( Hero hero) {
		this.hero = hero;
		heroID = this.hero.id();
		this.HP = hero.lvl*(5);
		HT = hero.lvl*(5);
	}

	@Override
	public float attackDelay() {
		if (hero != null) {
			if (this.hero.pointsInTalent(Talent.DEMON_HOUND) == 1) return (super.attackDelay()*0.8f);
			if (this.hero.pointsInTalent(Talent.DEMON_HOUND) == 2) return (super.attackDelay()*0.6f);
			if (this.hero.pointsInTalent(Talent.DEMON_HOUND) == 3) return (super.attackDelay()*0.5f);
			else {
				return super.attackDelay();
			}
		} else {
			return super.attackDelay();
		}
	}

	@Override
	protected boolean act() {

			if (state == SLEEPING) state = WANDERING;

		return super.act();
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		Buff.affect(enemy, Vulnerable.class, 3f);
		return super.attackProc(enemy, damage);
	}

	@Override
	public int damageRoll() {
		if (hero != null) {
			if (this.hero.pointsInTalent(Talent.STRONGER_FAITH)!= 0) return Random.NormalIntRange((int)((2 + this.hero.lvl/4) + (2/this.hero.pointsInTalent(Talent.STRONGER_FAITH))),(int)(4 + this.hero.lvl/2 + this.hero.pointsInTalent(Talent.STRONGER_FAITH)));
			else return Random.NormalIntRange((int)(2 + this.hero.lvl/4),(int)(4 + this.hero.lvl/2 ));
		} else {
			return Random.NormalIntRange( 2, 4 );
		}
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}





}
