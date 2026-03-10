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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.gardensboss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Vault;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.GardenBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EmperorCrystalSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardenSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Emperor;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CrystalRoom extends StandardRoom {

    @Override
    public void paint(Level level) {
        Painter.fill( level, this, Terrain.WALL );

        Painter.fillEllipse( level, this, 1, Terrain.EMPTY);

        for (Door door : connected.values()) {
            door.set( Door.Type.REGULAR );
            Point dir;
            if (door.x == left){
                dir = new Point(1, 0);
            } else if (door.y == top){
                dir = new Point(0, 1);
            } else if (door.x == right){
                dir = new Point(-1, 0);
            } else {
                dir = new Point(0, -1);
            }

            Point curr = new Point(door);
            do {
                Painter.set(level, curr, Terrain.EMPTY_SP);
                curr.x += dir.x;
                curr.y += dir.y;
            } while (level.map[level.pointToCell(curr)] == Terrain.WALL);
        }


        MagicCrystal crystal = new MagicCrystal();
        crystal.pos = level.pointToCell(center());
        level.mobs.add( crystal );



    }

    public static class MagicCrystal extends Mob {

        {
            spriteClass = EmperorCrystalSprite.class;

            properties.add(Property.IMMOVABLE);

            HP = HT = 20;
            defenseSkill = 0;

            maxLvl = -1;

            state = HUNTING;

        }

        private boolean emperorstate;

        @Override
        protected boolean act() {

            Mob Emperor = null;
            for (Mob m : Dungeon.level.mobs)
            {
                if (m instanceof Emperor) {
                Emperor = m;
                break;
                }
            }

            if (sprite != null ) sprite.idle();

            if (Emperor != null && Emperor.HP*2 <= Emperor.HT) emperorstate = true;

            if (emperorstate && Emperor != null)
            {

                if (sprite.visible || Emperor.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), Emperor.sprite.center()));
                }

                int healing = 1;
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) healing += Random.Int(1, 3);
                Emperor.HP = Math.min(Emperor.HP + healing, Emperor.HT);
                Emperor.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                Emperor.sprite.showStatus(CharSprite.POSITIVE, "+" + healing);



            }

            if (Emperor == null) die(this);

            spend(Actor.TICK * Random.Int(1, 4));

            return true;
        }

        @Override
        public void die( Object cause ) {

            if (cause instanceof MagicCrystal || cause instanceof Emperor) {}
            else Statistics.qualifiedForBossChallengeBadge = false;
                //Statistics.bossScores[0] -= 100;
            super.die(cause);
        }



        @Override
        public boolean isInvulnerable(Class effect) {
            //immune to damage when inactive
            return  !emperorstate || super.isInvulnerable(effect);
        }

        private static final String EMPERORSTATE = "emperor_state";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(EMPERORSTATE, emperorstate);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            emperorstate = bundle.getBoolean(EMPERORSTATE);
        }

    }



}
