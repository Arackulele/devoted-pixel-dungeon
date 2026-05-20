/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

import java.util.ArrayList;

public class ArcaneRoot extends Food {

	{
		image = ItemSpriteSheet.ARCANE_ROOT;
		energy = Hunger.STARVING / 2;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		effect(hero);
	}

	public int value() {
		return 15 * quantity;
	}

	public static void effect(Hero hero){
        Buff.affect( hero, WandEmpower.class).set(5, 3);

    }

    //i should really consider making a unified 'arena' type buff, this is way too much duplicate code
    public static class MagicArena extends Buff {

        public ArrayList<Integer> arenaPositions = new ArrayList<>();
        private ArrayList<Emitter> arenaEmitters = new ArrayList<>();

        private static final float DURATION = 35;
        int left = 0;

        {
            type = buffType.POSITIVE;
        }

        public void setup(int pos, int duration){
            left = duration;

            int dist;
            if (Dungeon.depth == 5 || Dungeon.depth == 10 || Dungeon.depth == 20){
                dist = 1; //smaller boss arenas
            } else {

                boolean[] visibleCells = new boolean[Dungeon.level.length()];
                Point c = Dungeon.level.cellToPoint(pos);
                ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), visibleCells, Dungeon.level.losBlocking, 8);

                dist = 2;
            }

            PathFinder.buildDistanceMap( pos, BArray.or( Dungeon.level.passable, Dungeon.level.avoid, null ), dist );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE && !arenaPositions.contains(i)) {
                    arenaPositions.add(i);
                }
            }
            if (target != null) {
                fx(false);
                fx(true);
            }

            left = (int) DURATION;

        }

        @Override
        public boolean act() {
            left--;
            BuffIndicator.refreshHero();
            if (left <= 0){
                detach();
            }

            for (Mob m : Dungeon.level.mobs) {
                //ToDo: Add this class to magical damage dealers
                if (arenaPositions.contains(m.pos)) {
                    m.damage(Math.max(1, m.HT/45), this);
                }
            }

            spend(TICK);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on){
                for (int i : arenaPositions){
                    Emitter e = CellEmitter.get(i);
                    e.pour(MagicMissile.MagicParticle.FACTORY, 0.2f);
                    arenaEmitters.add(e);
                }
            } else {
                for (Emitter e : arenaEmitters){
                    e.on = false;
                }
                arenaEmitters.clear();
            }
        }

        private static final String ARENA_POSITIONS = "arena_positions";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[arenaPositions.size()];
            for (int i = 0; i < values.length; i ++)
                values[i] = arenaPositions.get(i);
            bundle.put(ARENA_POSITIONS, values);

            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray( ARENA_POSITIONS );
            for (int value : values) {
                arenaPositions.add(value);
            }

            left = bundle.getInt(LEFT);
        }
    }

}
