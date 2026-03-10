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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Ruby;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.QuakeWolfSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollKnightSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class TrollKnight extends Mob {

	{
		spriteClass = TrollKnightSprite.class;

		HP = HT = 180;
		defenseSkill = 15;

		EXP = 20;

		baseSpeed = 1f;

		properties.add(Property.BOSS);
		properties.add(Property.LARGE);
		properties.add(Property.FIERY);

		SLEEPING = new Sleeping();

		state = SLEEPING;

        immunities.add(Burning.class);

	}

	public int totalrubies = 40;
	public String TOTALRUBIES = "totalrubies";

	public static TrollKnight instance;


	@Override
	public int attackSkill( Char target ) {
		return 22;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(12, 18);
	}

    //Id like this to be 1.5f but would mess with timing
    @Override
    public float attackDelay() {
        return super.attackDelay()*1;
    }


    //3 Different Special Attacks: They take 1 turn to charge and are announced beforehand
    //1.Slam: Slams sword on the ground, dealing damage in a straight line and turning tiles into magma tiles
    //2.Spin: Spin attack similar to the glass Knight. Inflicts bleeding if it hits
    //3.Backstep: Deals 5 low damage ticks and then  dashes 3 tiles away, creating safe ground underneath itself
    private boolean slam = false;
    private boolean spin = false;
    private boolean step = false;

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc(enemy, damage);

        if (Random.Int(2) == 0 && !slam && !spin && !step)
        {

            int probs = Random.Int(12);
            if (probs < 4) {
                slam = true;
                sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollKnight.class, "slam"));
            }
            else if (probs < 8) {
                spin = true;
                sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollKnight.class, "spin"));

            }
            else if (probs < 100){
                step = true;
                sprite.showStatus(CharSprite.POSITIVE, Messages.get(TrollKnight.class, "step"));

            }

        }

        return super.attackProc(enemy, damage);

    }



    @Override
    protected boolean act() {
            if (spin) {
                performSpin();
                return true;
            }
            if (slam) {
                performSlam();
                return true;
            }
            if (step) {
                performStep();
                return true;
            }

        return super.act();
    }

	public void DropGem()
	{

		if (totalrubies > 0) {
			int ofs;
			do {
				ofs = com.watabou.utils.PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop(new Ruby(), pos + ofs).sprite.drop(pos);
			totalrubies--;
		}

	}

	@Override
	public boolean isInvulnerable(Class effect) {
		//immune to damage when inactive
		return  state == SLEEPING || super.isInvulnerable(effect);
	}

	@Override
	public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
		super.restoreFromBundle(bundle);

		instance = this;
		totalrubies = bundle.getInt(TOTALRUBIES);

		if (state != SLEEPING){
			BossHealthBar.assignBoss(this);
		}

	}

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TOTALRUBIES, totalrubies);
	}

	protected class Sleeping extends Mob.Sleeping {

		@Override
		protected void awaken(boolean enemyInFOV) {
			boolean vaultsexist = false;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob instanceof Vault) vaultsexist = true;
			}

			if (!vaultsexist) {
				super.awaken(enemyInFOV);
				enemy = Dungeon.hero;
				BossHealthBar.assignBoss(TrollKnight.this);
			}
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		BountyHunter.Quest.beatBoss();
		com.watabou.noosa.audio.Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, 0.1f);
		PixelScene.shake( 3, 0.7f );

		while (totalrubies > 0)
		{
			DropGem();
		}

		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.dry[i] && Dungeon.level.trueDistance(i, pos) <= 5){
				Level.set(i, Terrain.WATER);
				GameScene.updateMap(i);
				Splash.at(i, 0xed980e, 15);

				Char mob = Actor.findChar(i);
				if (mob != null) Dungeon.level.occupyCell(mob);
			}
		}
	}


    private void performSpin(){
        Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

        Sample.INSTANCE.play(Assets.Sounds.BURNING);

        ConeAOE cone = new ConeAOE(aim,
                2.5f,
                360,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

        sprite.zap(pos);
        spend(1f);

        for (int cell : cone.cells) {
            CellEmitter.get(cell).burst(FlameParticle.FACTORY, 5);
            if (Actor.findChar(cell) != null) Actor.findChar(cell).damage(Random.NormalIntRange(10, 16), new Burning());
        }
        spin = false;
    }

    private void performSlam(){
        Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

        Sample.INSTANCE.play(Assets.Sounds.EVOKE);

        ConeAOE cone = new ConeAOE(aim,
                4f,
                1,
                Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

        sprite.zap(pos);
        spend(1f);

        for (int cell : cone.cells) {
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 5);

            if (Dungeon.level.map[cell] != Terrain.CHASM && Dungeon.level.map[cell] != Terrain.WATER && Dungeon.level.map[cell] != Terrain.EMPTY_SP && Dungeon.level.map[cell] != Terrain.EXIT && Dungeon.level.map[cell] != Terrain.ENTRANCE) {
                Level.set(cell, Terrain.MAGMA_TILE);
                GameScene.updateMap(cell);
            }

            if (Actor.findChar(cell) != null) Actor.findChar(cell).damage(Random.NormalIntRange(10, 16), new Burning());
        }
        slam = false;
    }

    private void performStep(){
        sprite.zap(pos);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            sprite.attack(enemy.pos, new Callback() {
                @Override
                public void call() {
                    enemy.damage(Random.NormalIntRange(2, 7), this);

                    if (finalI == 4)
                    {
                        step = false;

                        Ballistica route = new Ballistica(enemy.pos, pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
                        int dist = 3;

                        int cell = route.path.get(Dungeon.level.distance(pos, enemy.pos) + dist );
                        com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.MISS);
                        if (cell != pos && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
                            sprite.jump(pos, cell, 2f, 0.25f, new com.watabou.utils.Callback() {
                                @Override
                                public void call() {
                                    if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
                                        Door.leave(pos);
                                    }
                                    pos = cell;
                                    Dungeon.level.occupyCell(TrollKnight.this);
                                }
                            });
                        }

                    }
                }
            });
        }
        spend(1f);


    }



}
