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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Thirst extends Buff implements Hero.Doom {

	public static final float THIRSTY	= 260f;
	public static final float PARCHED	= 390f;

	private float level;
	private float partialDamage;

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}

	@Override
	public boolean act() {

		if (Dungeon.level.locked
				|| target.buff(ScrollOfChallenge.ChallengeArena.class) != null){
			spend(Actor.TICK);
			return true;
		}

		if (target.isAlive() && target instanceof Hero) {

			Hero hero = (Hero)target;

			if (isParched()) {

				//ToDO: Strength minus
				
			} else {

				float thirstDelay = 1f;

				float newLevel = level + (1f/thirstDelay);
				if (newLevel >= PARCHED) {

					GLog.n( Messages.get(this, "onparched") );
					hero.damage( 1, this );

					hero.interrupt();
					newLevel = PARCHED;

				} else if (newLevel >= THIRSTY && level < THIRSTY) {

					GLog.w( Messages.get(this, "onthirsty") );

				}
				level = newLevel;

			}
			
			spend( Actor.TICK );

		} else {

			diactivate();

		}

		return true;
	}

	public void satisfy( float energy ) {
		affectThirst( energy, false );
	}

	public void affectThirst(float energy ){
        affectThirst( energy, false );
	}

	public void affectThirst(float energy, boolean overrideLimits ) {

		float oldLevel = level;

		level -= energy;
		if (level < 0 && !overrideLimits) {
			level = 0;
		} else if (level > PARCHED) {
			//ToDo: Strength Minus
		}

		if (oldLevel < THIRSTY && level >= THIRSTY){
			GLog.w( Messages.get(this, "onthirsty") );
		} else if (oldLevel < PARCHED && level >= PARCHED){
			GLog.n( Messages.get(this, "onparched") );
			target.damage( 1, this );
		}

		BuffIndicator.refreshHero();
	}

	public boolean isParched() {
		return level >= PARCHED;
	}

	public boolean isThirsty() {
		return level >= THIRSTY;
	}

	public int thirst() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (level < THIRSTY) {
			return BuffIndicator.NONE;
		} else if (level < PARCHED) {
			return BuffIndicator.THIRSTY;
		} else {
			return BuffIndicator.PARCHED;
		}
	}

	@Override
	public String name() {
		if (level < PARCHED) {
			return Messages.get(this, "thirsty");
		} else {
			return Messages.get(this, "parched");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < PARCHED) {
			result = Messages.get(this, "desc_intro_thirsty");
		} else {
			result = Messages.get(this, "desc_intro_parched");
		}

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

		Dungeon.fail( this );
		GLog.n( Messages.get(this, "ondeath") );
	}
}
