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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;



public class FinalPrayer extends ArmorAbility {

    {
        baseChargeUse = 50f;
    }


    @Override
    public int icon() {
        return HeroIcon.FINAL_PRAYER;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.AUGMENTED_HEALING, Talent.DOWNTIME, Talent.STAY_AWAY, Talent.HEROIC_ENERGY};

    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(DoubleJumpTracker.class) != null){
            //reduced charge use by 20%/36%/50%/60%
            chargeUse *= Math.pow(0.795, hero.pointsInTalent(Talent.DOUBLE_JUMP));
        }
        return chargeUse;
    }

    @Override
    public void activate( ClassArmor armor, Hero hero, Integer target ) {
        if (target != null) {

            Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
            int cell = route.collisionPos;

            //can't occupy the same cell as another char, so move back one.
            int backTrace = route.dist-1;
            while (Actor.findChar( cell ) != null && cell != hero.pos) {
                cell = route.path.get(backTrace);
                backTrace--;
            }

            armor.charge -= chargeUse( hero );
            armor.updateQuickslot();

            final int dest = cell;
            hero.busy();
            hero.sprite.jump(hero.pos, cell, new Callback() {
                @Override
                public void call() {
                    hero.move(dest);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();
                    hero.damage(hero.HP - 1, this);
                    Buff.affect(hero, Healing.class).setHeal((int) (0.8f * hero.HT + 14), 0.02f*(1+hero.pointsInTalent(Talent.AUGMENTED_HEALING)), 0);
                    Buff.affect(hero, AnkhInvulnerability.class, 1);
                    if (hero.hasTalent(Talent.DOWNTIME))Buff.affect(hero, Haste.class, 2*hero.pointsInTalent(Talent.STAY_AWAY));

                    for (int i : PathFinder.NEIGHBOURS8) {
                        Char mob = Actor.findChar(hero.pos + i);
                        if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                            if (mob.pos == hero.pos + i && hero.hasTalent(Talent.STAY_AWAY)){
                                Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, Ballistica.MAGIC_BOLT);
                                int strength = 2+hero.pointsInTalent(Talent.STAY_AWAY);
                                WandOfBlastWave.throwChar(mob, trajectory, strength, true, true, FinalPrayer.this.getClass());
                            }
                        }
                    }

                    WandOfBlastWave.BlastWave.blast(dest);


                    Invisibility.dispel();
                    hero.spendAndNext(Actor.TICK);


                }
            });
        }
    }

    public static class DoubleJumpTracker extends FlavourBuff{};

}






