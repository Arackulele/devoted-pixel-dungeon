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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FrogeatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ToadSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public abstract class Frogeat extends Mob {

    {
        spriteClass = FrogeatSprite.class;

        HP = HT = 100;
        defenseSkill = 20;
        viewDistance = Light.DISTANCE;

        EXP = 10;
        maxLvl = 25;

        loot = Generator.Category.POTION;
        lootChance = 0.5f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public String description() {
        return super.description() + Messages.get(this, "type");
    }


    @Override
    public int damageRoll() {
        return Random.NormalIntRange(26, 35);
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 16);
    }

    @Override
    public Item createLoot() {
        Class<? extends Potion> loot;
        do {
            loot = (Class<? extends Potion>) Random.oneOf(Generator.Category.POTION.classes);
        } while (loot == PotionOfHealing.class || loot == PotionOfStrength.class);

        return Reflection.newInstance(loot);
    }

    int hunger = 140;

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        //Even when dealing no damage, hunger meter should go down
        //Players with good armor who are fine with tanking hits should
        //get rewarded here, as opposed to players that kill enemies quickly
        //without taking risks
        hunger -= 10 + Math.min(damage, 30);

        return damage;
    }

    ;

    @Override
    public void die(Object cause) {

        super.die(cause);

        if (cause == Chasm.class) return;

        Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

        ConeAOE cone = new ConeAOE(aim,
                Math.max(hunger * 0.05f, 1f),
                360,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

        for (int cell : cone.cells) {
            DeathEffect(cell);
        }

        Sample.INSTANCE.play(Assets.Sounds.BLACKHOLE, 2f);

    }

    protected abstract void DeathEffect(int pos);

    public static class PoisonFrogeat extends Frogeat {
        {
            spriteClass = FrogeatSprite.Poison.class;
        }

        @Override
        protected void DeathEffect(int pos) {

            CellEmitter.get(pos).burst(PoisonParticle.SPLASH, 5);
            if (Actor.findChar(pos) != null)
                Buff.affect(Actor.findChar(pos), Poison.class).set(Math.max(5f, hunger * 0.15f));


        }

    }

    public static class BleedingFrogeat extends Frogeat {
        {
            spriteClass = FrogeatSprite.Bleeding.class;
        }

        @Override
        protected void DeathEffect(int pos) {

            CellEmitter.get(pos).burst(BloodParticle.BURST, 5);
            if (Actor.findChar(pos) != null)
                Buff.affect(Actor.findChar(pos), Bleeding.class).set(Math.max(5f, hunger * 0.15f));

        }
    }

    public static class ConfusingFrogeat extends Frogeat {
        {
            spriteClass = FrogeatSprite.Vertigo.class;
        }

        @Override
        protected void DeathEffect(int pos) {

            CellEmitter.get(pos).burst(RainbowParticle.BURST, 5);
            if (Actor.findChar(pos) != null) {
                Buff.affect(Actor.findChar(pos), Vertigo.class, Math.max(10f, hunger * 0.2f));
                int damage = Math.round(Random.NormalIntRange(8, 24));
                Char ch = Actor.findChar(pos);
                damage = Math.max(0, damage - (ch.drRoll() + ch.drRoll()));
                Actor.findChar(pos).damage(damage, this);
            }
        }
    }

    public static Class<? extends Frogeat> random() {
        float roll = Random.Float();
        if (roll < 0.4f) {
            return Frogeat.BleedingFrogeat.class;
        } else if (roll < 0.8f) {
            return Frogeat.PoisonFrogeat.class;
        } else {
            return Frogeat.ConfusingFrogeat.class;
        }
    }

}
