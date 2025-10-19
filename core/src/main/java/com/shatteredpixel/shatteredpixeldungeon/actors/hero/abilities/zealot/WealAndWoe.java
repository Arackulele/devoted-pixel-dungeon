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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.zealot;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;


public class WealAndWoe extends ArmorAbility {

    {
        baseChargeUse = 25f;
    }


    @Override
    public int icon() {
        return HeroIcon.WEAL_AND_WOE;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.WOE_IS_ME, Talent.AND_WOE_IS_YOU, Talent.WEIGHING_THE_DIE, Talent.HEROIC_ENERGY};

    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);

        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(Actor.TICK);

        if (hero.hasTalent(Talent.WEIGHING_THE_DIE)) {
            if (Random.Int(100) <= 50 - hero.pointsInTalent(Talent.WEIGHING_THE_DIE)*5) {
                weal(hero);
            } else {
                woe(hero);
            }
        }
        else if (Random.Int(2)==1) {
            weal(hero);
        }
        else {
            woe(hero);

        }

        }

        private void weal(Hero hero)
        {
            Buff.affect(hero, Weal.class, 10+hero.pointsInTalent(Talent.WEIGHING_THE_DIE));
            if (hero.hasTalent(Talent.WOE_IS_ME)) Buff.affect(hero, ToxicImbue.class).set(2*hero.pointsInTalent(Talent.WOE_IS_ME));

            Buff.detach(hero, Cripple.class);
            Buff.detach(hero, Weakness.class);
            Buff.detach(hero, Vulnerable.class);
            Buff.detach(hero, Bleeding.class);
            Buff.detach(hero, Blindness.class);
            Buff.detach(hero, Drowsy.class);
            Buff.detach(hero, Slow.class);
            Buff.detach(hero, Vertigo.class);
            Buff.detach(hero, Degrade.class);
        }

        private void woe(Hero hero)
        {

            if (hero.hasTalent(Talent.WOE_IS_ME)) Buff.affect(hero, FireImbue.class).set(2*hero.pointsInTalent(Talent.WOE_IS_ME));
            Buff.affect(hero, Woe.class, 10);

            for (int i : PathFinder.NEIGHBOURS8) {
                Char mob = Actor.findChar(hero.pos + i);
                if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                    if (mob.pos == hero.pos + i && hero.hasTalent(Talent.AND_WOE_IS_YOU)){
                        Buff.affect(mob, Woe.class, 2*hero.pointsInTalent(Talent.WOE_IS_ME));

                    }
                }
            }
        }


    public static class Weal extends FlavourBuff {

        public static final float DURATION = 10f;

        {
            type = buffType.POSITIVE;
            //announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAL;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }

    public static class Woe extends FlavourBuff {

        public static final float DURATION = 10f;

        {
            type = buffType.NEGATIVE;
            //announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WOE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
    }



}













