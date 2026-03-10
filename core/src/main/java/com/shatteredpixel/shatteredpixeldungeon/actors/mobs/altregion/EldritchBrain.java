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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShielding;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BrainSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EldritchBrain extends Mob {

	{
		spriteClass = BrainSprite.class;

		HP = HT = 300;
		defenseSkill = 0;

		EXP = 15;
		maxLvl = 29;

		state = PASSIVE;

		loot = Random.oneOf(PotionOfHealing.class, PotionOfParalyticGas.class, PotionOfShielding.class);
		lootChance = 1f;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
        //ToDo: Add Eldritch as a property
        //Might be immune to some status effects, and one element is more effective ( consider lightning )
		//properties.add(Property.DEMONIC);
		properties.add(Property.STATIC);
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

	private float spawnCooldown = 0;

	public boolean spawnRecorded = false;

	@Override
	protected boolean act() {
		if (!spawnRecorded){
			Statistics.spawnersAlive++;
			spawnRecorded = true;
		}

		alerted = false;

        if (enemy != null && AbilityScore > 20){

            //Choose between one of these abilities
            //1: Beckon mobs to brain location ( maybe only some )
            //2: Push away player and inflict vertigo
            //3: Spawn grub enemy or some other weak fodder
            AbilityScore -= 20;

            int t = Random.Int(0, 3);
            switch (t)
            {
                default:
                case 0:
                    sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
                    Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );

                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                        if (Random.Int( 7 ) < 4) mob.beckon(this.pos);
                    }
                    break;
                case 1:
                    Sample.INSTANCE.play( Assets.Sounds.BLAST );
                    WandOfBlastWave.BlastWave.blast(pos);
                    Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
                    //trim it to just be the part that goes past them
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                    //knock them back along that ballistica
                    WandOfBlastWave.throwChar(enemy, trajectory, 2, true, false, this);
                    Buff.affect(enemy, Vertigo.class, 10);
                    break;
                case 2:
                    Sample.INSTANCE.play( Assets.Sounds.DEGRADE );
                    GameScene.add( Blob.seed(pos, 90, ParalyticGas.class));
                    break;
            }

            spend(3f);
            return true;
        }
        else return super.act();

    }

	@Override
	public void damage(int dmg, Object src) {
		if (dmg >= 20){
			//takes 20/21/22/23/24/25/26/27/28/29/30 dmg
			// at   20/22/25/29/34/40/47/55/64/74/85 incoming dmg
			dmg = 19 + (int)(Math.sqrt(8*(dmg - 19) + 1) - 1)/2;
            AbilityScore+= dmg;
		}
		spawnCooldown -= dmg;
		super.damage(dmg, src);
	}

    private int AbilityScore = 0;

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.ELDRITCH_BRAIN;
	}

	@Override
	public void die(Object cause) {
		if (spawnRecorded){
			Statistics.spawnersAlive--;
			Notes.remove(landmark());
		}
		GLog.h(Messages.get(this, "on_death"));

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob instanceof NeuronSentry) {
                mob.die(this);
            }
        }

		super.die(cause);
	}

	public static final String SPAWN_COOLDOWN = "spawn_cooldown";
	public static final String SPAWN_RECORDED = "spawn_recorded";
    public static final String ABILITYSCORE = "abilityscore";


    @Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPAWN_COOLDOWN, spawnCooldown);
		bundle.put(SPAWN_RECORDED, spawnRecorded);
        bundle.put(ABILITYSCORE, AbilityScore);
    }

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
		spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
        AbilityScore = bundle.getInt(ABILITYSCORE);
	}

}
