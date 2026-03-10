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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MoleSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class Mole extends Mob {
	
	{
		spriteClass = MoleSprite.class;

		HP = HT = 50;
		defenseSkill = 17;

		EXP = 9;
		maxLvl = 17;

		viewDistance = 5;

		loot = MysteryMeat.class;
		lootChance = 0.125f;

		HUNTING = new Mole.Hunting();

	}

	public boolean digging = false;

	public String DIGGING = "digging";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DIGGING, digging);
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		digging = bundle.getBoolean(DIGGING);
		super.restoreFromBundle(bundle);
	}

	@Override
	public float speed() {
		return super.speed() * (digging ? 2 : 0.5f);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(8, 21);
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(1, 7);
	}

	@Override
	public int attackSkill(Char target) {
		return 22;
	}

	@Override
	public boolean act() {

		if (state != HUNTING && digging)
		{
            Emerge();
			return true;
		}

		return super.act();

	}

	public void SpreadEffect(int p)
	{
		Char ch = Actor.findChar( pos + com.watabou.utils.PathFinder.NEIGHBOURS8[p] );
		if (ch != null && ch.isAlive()) {
			Buff.affect(ch, Slow.class, 2f);
		}

	}

    public void Emerge() {

        //This is somewhat inefficient but otherwise id probably have to put emerging in the movement code, which would prevent the mole from popping up from right under an enemy

        Char ch = findChar(pos);


        if (ch == null) {
            for (Mob m : Dungeon.level.mobs) {
                if (m.pos == pos && m != this) ch = m;
            }

            if (ch == null && Dungeon.hero.pos == pos) ch = Dungeon.hero;
        }

        if (ch != null) {

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

        digging = false;
        ((MoleSprite) sprite).setEmerge();
        com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.DIG);
        spend(1f);

    }

    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            if (!digging && enemyInFOV && Dungeon.level.distance(enemy.pos, pos) > 1) {

                digging = true;
                ((MoleSprite) sprite).setSubmerge();
                com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.DIG);
                spend(1f);

                return true;
            } else if (digging && enemyInFOV && Dungeon.level.distance(enemy.pos, pos) < 2) {

                Emerge();

				for (int i = 0; i < com.watabou.utils.PathFinder.NEIGHBOURS8.length; i++) {
					CellEmitter.get(pos + com.watabou.utils.PathFinder.NEIGHBOURS8[i]).burst(SmokeParticle.FACTORY, 5);
					SpreadEffect(i);

                }
				return true;
			}

			return super.act(enemyInFOV, justAlerted);

		}
	}





}
