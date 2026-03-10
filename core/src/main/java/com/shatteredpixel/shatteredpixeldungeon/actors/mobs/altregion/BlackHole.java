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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EquipmentDisabled;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackHoleSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BlackHole extends Mob {

    {
        spriteClass = BlackHoleSprite.class;

        HP = HT = 60;
        defenseSkill = 0;

        maxLvl = -1;

        state = PASSIVE;


        properties.add(Property.IMMOVABLE);
        properties.add(Property.STATIC);
        properties.add(Property.LARGE);

        actPriority = MOB_PRIO + 1;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 12);
    }

    @Override
    public void beckon(int cell) {
        //do nothing
    }

    @Override
    public boolean reset() {
        return true;
    }


    @Override
    protected boolean act() {

        for (Mob m : Dungeon.level.mobs) {
            if (m.alignment != this.alignment && !m.sprite.isMoving) {

                PullChar(m);
            }
        }
        if (!Dungeon.hero.sprite.isMoving) PullChar(Dungeon.hero);

        spend(1f);

        sprite.jump(pos, pos, 0f, 0.1f, new com.watabou.utils.Callback() {
            @Override
            public void call() {
                next();
            }
        });
        return false;
    }

    private void PullChar(Char m) {

        if (m instanceof Hero) Dungeon.hero.interrupt();
        Ballistica trajectory = new Ballistica(m.pos, pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        //knock them back along that ballistica
        if (Dungeon.level.distance(trajectory.path.get(1), pos) < Dungeon.level.distance(m.pos, pos) && Dungeon.level.passable[trajectory.path.get(1)] && Actor.findChar(trajectory.path.get(1)) == null) {
            Actor.add(new Pushing(m, m.pos, trajectory.path.get(1), new Callback() {
                @Override
                public void call() {
                    m.pos = trajectory.path.get(1);
                    Dungeon.level.occupyCell(m);
                }
            }));
            m.move(m.pos);
            next();
        } else next();


    }

    @Override
    public void damage(int dmg, Object src) {
        if (dmg >= 20) {
            //takes 20/21/22/23/24/25/26/27/28/29/30 dmg
            // at   20/22/25/29/34/40/47/55/64/74/85 incoming dmg
            dmg = 19 + (int) (Math.sqrt(8 * (dmg - 19) + 1) - 1) / 2;
        }
        super.damage(dmg, src);
    }

    @Override
    public void die(Object cause) {

        super.die(cause);

        if (!Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) return;

        Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

        Sample.INSTANCE.play(Assets.Sounds.EVOKE);

        ConeAOE cone = new ConeAOE(aim,
                2.5f,
                360,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

        sprite.zap(pos);
        spend(1f);

        for (int cell : cone.cells) {
            int b = Random.Int(1, 30);
            if (b < 11) GameScene.add(Blob.seed(cell, Random.Int(3, 7), Freezing.class));
            else if (b < 21) GameScene.add(Blob.seed(cell, Random.Int(3, 7), Fire.class));
            else GameScene.add(Blob.seed(cell, Random.Int(3, 7), Electricity.class));

            if (Actor.findChar(cell) != null && Actor.findChar(cell).alignment != this.alignment)
                Actor.findChar(cell).damage(Random.NormalIntRange(10, 20), this);
        }

    }


}
