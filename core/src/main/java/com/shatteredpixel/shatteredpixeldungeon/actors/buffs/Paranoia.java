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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Paranoia extends Buff implements Hero.Doom {

	public static final float PARANOID	= 100f;
	public static final float INSANE	= 200f;

	private float level;
	private static final String LEVEL			= "level";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
    }

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
    }
    

	public void reduceInsanity( float energy ) {
		affectInsanity( -energy, false );
	}

	public void affectInsanity(float energy ){
        affectInsanity( energy, false );
	}

	public void affectInsanity(float energy, boolean overrideLimits ) {

		float oldLevel = level;

		level += energy;
		if (level < 0 && !overrideLimits) {
			level = 0;
		} else if (level > INSANE) {

		}

        if (energy > 0) {
            if (oldLevel < PARANOID && level >= PARANOID){
                GLog.w( Messages.get(this, "onparanoid") );
            } else if (oldLevel < INSANE && level >= INSANE){
                GLog.n( Messages.get(this, "oninsane") );
                target.damage( 1, this );
            } else if (oldLevel >= INSANE) {
                GLog.n(Messages.get(this, "triggerinsane"));
                Buff.prolong(target, Weakness.class, Vertigo.DURATION);
                Buff.prolong(target, Blindness.class, Blindness.DURATION);
                target.damage((int) (target.HP * 0.2f), this);
                reduceInsanity(PARANOID);
                com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.FALLING, 0.8f, 1.2f);
            }
        }

		BuffIndicator.refreshHero();
	}

	public boolean isInsane() {
		return level >= INSANE;
	}

	public boolean isParanoid() {
		return level >= PARANOID;
	}

	public int insanity() {
		return (int)Math.ceil(level);
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

            if (level < 1) {

                //ToDO: Strength minus

            } else {

                float delay = 0.3f;

                reduceInsanity(delay);

            }

            spend( Actor.TICK );

        } else {

            diactivate();

        }

        return true;
    }


    @Override
	public int icon() {
		if (level < PARANOID) {
			return BuffIndicator.NONE;
		} else if (level < INSANE) {
			return BuffIndicator.PARANOID;
		} else {
			return BuffIndicator.INSANE;
		}
	}

	@Override
	public String name() {
		if (level < INSANE) {
			return Messages.get(this, "paranoid");
		} else {
			return Messages.get(this, "insane");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < INSANE) {
			result = Messages.get(this, "desc_intro_paranoid");
		} else {
			result = Messages.get(this, "desc_intro_insane");
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
