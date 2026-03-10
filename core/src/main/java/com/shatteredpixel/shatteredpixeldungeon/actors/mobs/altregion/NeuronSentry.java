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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalSentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NeuronSentrySprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NeuronSentry extends Mob implements Callback {

	{
		spriteClass = NeuronSentrySprite.class;

		HP = HT = 200;
        defenseSkill = 0;

		EXP = 10;
		maxLvl = -2;

		viewDistance = Light.DISTANCE;

		state = WANDERING = new Waiting();

		properties.add(Property.IMMOVABLE);
        properties.add(Property.LARGE);
        properties.add(Property.MINIBOSS);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}
	private boolean shouldwarn = true;

	public int wherestrike = -1;

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				//|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
				//Neuron Sentries ignore line of sight, maybe the visual effect should be changed from a magic beam
				  || (fieldOfView != null && fieldOfView[enemy.pos]);
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (shouldwarn)
		{
			wherestrike = enemy.pos;
			for (int i : PathFinder.CROSS2) {
				sprite.parent.add(new TargetedCell(i + wherestrike, 0xFF0000));
			}
			spend(TICK);
			shouldwarn = false;
			return true;
		}
		else {
			for (int i : PathFinder.CROSS2) {
				CellEmitter.get(i + wherestrike).burst(SparkParticle.FACTORY, 5);
                Char t = Actor.findChar(i + wherestrike);
                if (t != null && t.alignment != alignment) {
                    int dmg = Random.NormalIntRange(9, 27);
                    dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
                    enemy.damage(dmg, new DM100.LightningBolt());
                }
				sprite.zap(wherestrike);

            }
			shouldwarn = true;
			spend(TICK);
			return true;
		}

	}

    public boolean foundPos = false;

    private static final String SHOULDWARN = "shouldwarn";
    private static final String STRIKEPOS = "strikepos";
    private static final String FOUNDPOS = "foundpos";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( SHOULDWARN, shouldwarn );
        bundle.put( STRIKEPOS, wherestrike );
        bundle.put( FOUNDPOS, foundPos );

    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        shouldwarn = bundle.getBoolean(SHOULDWARN);
        wherestrike = bundle.getInt(STRIKEPOS);
        foundPos = bundle.getBoolean(FOUNDPOS);

    }

    @Override
    protected boolean act() {

        int timer = 0;
        int p = pos;

        //Find valid location to stay at
        while (!foundPos && timer < 200) {
            timer++;
            int closest = 100;
           for (Mob  mob : Dungeon.level.mobs)
           {
               if (mob.id() != this.id() && mob instanceof NeuronSentry && ((NeuronSentry)mob).foundPos)
               {
                   if(Dungeon.level.distance(p, mob.pos) < closest) closest = Dungeon.level.distance(p, mob.pos);
               }
               }

               if (closest > 9 && Dungeon.level.map[p] == Terrain.EMPTY) {
                    foundPos = true;
                   ScrollOfTeleportation.teleportToLocation( this, p );
                   //Dungeon.level.occupyCell(this, p);
               }
               else p = Dungeon.level.randomRespawnCell(this);


        }

        return super.act();

    }


	@Override
	public void damage( int dmg, Object src ) {
        shouldwarn = true;
        wherestrike = -1;
		Buff.prolong(this, Paralysis.class, dmg / 5);
	}
	@Override
	public void call() {
		next();
	}

	private class Waiting extends Wandering{

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			//always notices the hero
			if (enemyInFOV) {

				return noticeEnemy();

			} else {
                shouldwarn = true;
                wherestrike = -1;
				return continueWandering();

			}
		}

		@Override
		protected boolean noticeEnemy() {
			spend(Actor.TICK);
			return super.noticeEnemy();
		}
	}

}
