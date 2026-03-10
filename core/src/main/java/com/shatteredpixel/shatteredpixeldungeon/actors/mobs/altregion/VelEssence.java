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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Barf;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VoidRoot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.VoidSnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.VoidBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EssenceSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;

public abstract class VelEssence extends Mob {

    {
        HP = HT = 300;
        defenseSkill = 20;

        viewDistance = Light.DISTANCE;

        //for doomed resistance
        EXP = 25;
        maxLvl = -2;

        state = HUNTING;

        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
    }

    public float abilityCooldown = 1;
    public float TimeUntilDeath = 35;


    protected void incrementRangedCooldown() {
        abilityCooldown += Random.NormalFloat(8, 12);
    }

    @Override
    protected boolean act() {
        if (paralysed <= 0 && abilityCooldown > 0) abilityCooldown--;
        TimeUntilDeath--;
        if (TimeUntilDeath < 1) {
            next();
            die(null);
        }

        if (Dungeon.hero.invisible <= 0 && state == WANDERING) {
            beckon(Dungeon.hero.pos);
            state = HUNTING;
            enemy = Dungeon.hero;
        }

        return super.act();
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy);
    }

    private VoidBossLevel level;

    @Override
    public void damage(int dmg, Object src) {


        if (Dungeon.level instanceof VoidBossLevel) level = (VoidBossLevel)Dungeon.level;

        int dst = distance(level.Boss);

        //always do less damage than hitting the boss itself
        dmg *= 0.7f;
        //deal even less damage based on distance from the boss
        dmg -= dst * 3;
        //But dont deal negative damage
        dmg = Math.max(0, dmg);

        //if (sprite.visible || level.Boss.sprite.visible) {
            //sprite.parent.add(new Beam.VoidDots(sprite.center(), level.Boss.sprite.center()));
        //}

        sprite.parent.add(new Chains(sprite.center(),
                level.Boss.sprite.destinationCenter(),
                Effects.Type.VOID_DOTS,
                new Callback() {
                    public void call() {
                    }
                }));


        level.Boss.damage(dmg, src);
        sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg), FloatingText.DEFERRED);

    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(18, 36);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 15);
    }

    {
        immunities.add(Sleep.class);
    }

    @Override
    public String description() {
        return Messages.get(VelEssence.class, "desc") + "\n\n" + Messages.get(this, "desc");
    }

    public static final String RANGED_COOLDOWN = "ranged_cooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(RANGED_COOLDOWN, abilityCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        abilityCooldown = bundle.getFloat(RANGED_COOLDOWN);
    }

    public static class Unearth extends VelEssence {

        {
            spriteClass = EssenceSprite.Unearth.class;

        }

        @Override
        protected boolean act() {
            if (enemy == null) enemy = Dungeon.hero;

            if (abilityCooldown == 1)
            {
                //ToDo: Warn player
                spend(TICK);
                abilityCooldown = 0;
                return true;
            }
            if (abilityCooldown < 1)
            {

                ArrayList<Integer> Ps = new ArrayList();
                for (int p : PathFinder.NEIGHBOURS8)
                {
                    if (Dungeon.level.passable[p+ pos]) Ps.add(p+ pos);
                }
                Random.shuffle(Ps);

                for(int i = 3; i > 0; i--)
                {
                    Mob m;
                    int floor;
                    do {
                        floor = Random.Int(25);
                    } while( Dungeon.bossLevel(floor));
                    m = Reflection.newInstance(MobSpawner.getMobRotation(floor).get(0));

                    m.maxLvl = -1;
                    m.HP = m.HT = 1;

                    m.pos = Ps.get(0);
                    Ps.remove(0);
                    GameScene.add(m, 2f);
                    Dungeon.level.occupyCell(m);
                    CellEmitter.get(m.pos).burst(VoidSnipeParticle.PURPLE, 10);
                    ScrollOfTeleportation.appear(m, m.pos);
                    m.sprite.resetColor();
                    m.state = m.HUNTING;
                    m.sprite.idle();

                }

                sprite.attack(enemy.pos);


                abilityCooldown = Random.Int(20, 25);
                spend(TICK * 4);
                return false;
            }

            return super.act();
        }


    }

    public static class Snipe extends VelEssence {

        {
            immunities.add(Barf.class);
        }

        @Override
        protected boolean act() {

            if (abilityCooldown == 1)
            {
                //ToDo: Warn player
                spend(TICK);
                abilityCooldown = 0;
                return true;
            }
            if (abilityCooldown < 1)
            {
                if (enemy == null) enemy = Dungeon.hero;


                Ballistica f = new  Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT);
                    if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                        sprite.zap(f.collisionPos);
                    } else {
                        zap(f.collisionPos);
                    }

                f = new Ballistica(f.collisionPos, f.path.get(f.path.size() - 1), Ballistica.PROJECTILE);

                for (int i : f.path)
                {
                    //Barf is green, close enough tbh
                    GameScene.add(Blob.seed(i, 5, Barf.class));
                }

                abilityCooldown = Random.Int(9, 13);
                spend(TICK * 2);
                return false;
            }

            return super.act();
        }

        public static class SnipeShot{}

        protected void zap(int pos) {
            spend( TICK * 2 );
            Char enemy = null;
            if (Actor.findChar(pos) != null) enemy = Actor.findChar(pos);
            if (enemy != null && enemy.alignment == this.alignment) enemy = null;

            Invisibility.dispel(this);
            if (enemy != null) {
                int dmg = Random.NormalIntRange(20, 45);
                dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
                enemy.damage(dmg, new SnipeShot());

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "bolt_kill"));
                }
            }
            else {
                if (sprite != null && sprite.visible) {
                    sprite.zap(pos);
                }
                Level.set(pos, Terrain.EMBERS);
                GameScene.updateMap(pos);
            }
        }

        public void onZapComplete() {
            //zap(pos);
            next();
        }


        {
            spriteClass = EssenceSprite.Snipe.class;
        }

    }

    public static class Bombard extends VelEssence {
        {
            spriteClass = EssenceSprite.Bombard.class;
        }

        @Override
        protected boolean act() {

            if (abilityCooldown == 1)
            {
                //ToDo: Warn player
                spend(TICK);
                abilityCooldown = 0;
                return true;
            }
            if (abilityCooldown < 1)
            {
                int amnt = Random.Int(5, 8);

                //this is possibly a bit scuffed
                ArrayList<Integer> temp = new ArrayList<>();
                for (int cell = 0; cell < ( Dungeon.level.length() ); cell++) {
                    if (Dungeon.level.distance(pos, cell) < 4 && !Dungeon.level.solid[cell]) temp.add(cell);
                }
                    while (amnt > 0) {
                    Random.shuffle(temp);
                    amnt--;
                    int wherestrike = temp.get(0);
                    sprite.zap(wherestrike);
                }

                abilityCooldown = Random.Int(4, 6);
                spend(TICK);
                return false;
            }

            return super.act();
        }

        protected void zap(int p)
        {
            for (int i : PathFinder.NEIGHBOURS5) {
                CellEmitter.get(i + p).burst(EarthParticle.FACTORY, 5);
                Char t = Actor.findChar(i + p);
                if (t != null && t.alignment != alignment) {
                    int dmg = Random.NormalIntRange(9, 27);
                    dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
                    t.damage(dmg, this);
                }

            }

        }

        public void onZapComplete(int p) {
            zap(p);
            next();
        }



    }

    public static class Ram extends VelEssence {

        @Override
        protected boolean act() {
            if (enemy == null) enemy = Dungeon.hero;

            if (abilityCooldown == 1)
            {
                //ToDo: Warn player
                spend(TICK);
                abilityCooldown = 0;
                return true;
            }
            if (abilityCooldown < 1)
            {

                Ballistica route = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
                int cell = route.path.get(route.dist );
                if (Actor.findChar(cell) == enemy) DashToEnemy(route);
                else return super.act();


                abilityCooldown = Random.Int(9, 16);
                spend(TICK);
                return false;
            }

            return super.act();
        }


        {
            spriteClass = EssenceSprite.Ram.class;

        }

        public void DashToEnemy(Ballistica route){

            int cell = route.path.get(route.dist );
                sprite.emitter().start(VoidSnipeParticle.RED, 0.01f, 100);
                com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.BLACKHOLE);
                sprite.jump(pos, cell, 0f, 0.5f, new com.watabou.utils.Callback() {
                    @Override
                    public void call() {
                        pos = cell;
                        Dungeon.level.occupyCell(VelEssence.Ram.this);
                        sprite.attack(enemy.pos);
                        Buff.affect(enemy, Vulnerable.class, 10f);
                        PushAway(enemy);


                    }
                });
                //spend(TICK);

        }

        public void PushAway(Char ch)
        {

                int pushPos = pos;
                for (int c : PathFinder.NEIGHBOURS8) {
                    if (findChar(pos + c) == null
                            && Dungeon.level.passable[pos + c]
                            && (Dungeon.level.openSpace[pos + c] || !hasProp(findChar(pos), Property.LARGE))
                            && Dungeon.level.trueDistance(pos, pos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
                        pushPos = pos + c;
                    }
                }


                //push enemy, or wait a turn if there is no valid pushing position
                if (pushPos != pos) {
                    Actor.add(new Pushing(ch, ch.pos, pushPos));

                    ch.pos = pushPos;
                    Dungeon.level.occupyCell(ch);


                }


        }

    }

    public static class Restrain extends VelEssence {

        {
            spriteClass = EssenceSprite.Restrain.class;
        }

        @Override
        protected boolean act() {
            if (enemy == null) enemy = Dungeon.hero;

            if (abilityCooldown == 1 && distance(enemy) < 5)
            {
                //ToDo: Warn player
                spend(TICK);
                abilityCooldown = 0;
                return true;
            }
            if (abilityCooldown < 1)
            {

                GameScene.add(Blob.seed(enemy.pos, 40, VoidRoot.class));

                for (int p : PathFinder.NEIGHBOURS8)
                {

                    if (Random.Int(1, 10) > 5)  GameScene.add(Blob.seed(enemy.pos + p, 40, VoidRoot.class));


                }

                sprite.attack(enemy.pos);


                abilityCooldown = Random.Int(10, 18);
                spend(TICK);
                return false;
            }

            return super.act();
        }

        {
            immunities.add(VoidRoot.class);
        }

    }

    public static VelEssence random() {
        int roll = Random.Int(0, 6);

        switch (roll) {
            default:
            case 0:
                return new VelEssence.Unearth();
            case 1:
                return new VelEssence.Snipe();
            case 2:
                return new VelEssence.Bombard();
            case 3:
                return new VelEssence.Ram();
            case 4:
                return new VelEssence.Restrain();


        }

    }


}
