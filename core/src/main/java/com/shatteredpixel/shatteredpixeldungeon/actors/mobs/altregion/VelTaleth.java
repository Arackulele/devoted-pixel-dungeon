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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.VoidSnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VoidBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.VoidTrapLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VelSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.*;

import java.util.ArrayList;

public class VelTaleth extends Mob {

	{
		spriteClass = VelSprite.class;

		HP = HT = 2000;

		EXP = 50;

		//so that allies can attack it. States are never actually used.
		state = HUNTING;

		viewDistance = 12;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
		properties.add(Property.STATIC);
        properties.add(Property.LARGE);
    }

    /*ToDo: Mechanics:
    Phase 1:
    Summons and despawns the following minions, can only sustain 1 minion at once, minions are invincible until they expire
    (Order is random but never repeats the same void essence twice in a row)
    Void Essence: Unearth ( Summons shadowy versions of Ashen Void mobs that are killed in 1 hit, but otherwise posess their regular abilities.
    Void Essence: Snipe ( Fires a ranged projectile every 3 turns, stands still 1 turn before shooting so you can get out of the way , huge damage when hit )
    Void Essence: Bombard ( 3 random tiles around the guardian are marked every turn, the next turn the area around those tiles explodes with a projectile coming from the sky, medium damage for each hit
    Void Essence: Ram ( Every few turns, dashes into the player, inflicting defense debuff, low damage if hit )
    Void Essence: Restrain ( Creates an expanding area around the player that will root them, and deal passive damage every turn the player is standing in it
    Attack:
    Creates a gust of wind that blows the player back, possibly into a chasm or just away from itself
    After taking enough damage: Teleports onto a random different part of the level, leaving a bomb behind.

    Phase 2:
    Can now sustain 2 minions at once that will both stay invincible
    Attacks:
    Keeps gust attack,
    new attack: Summons a black hole on a random tile, that will suck in the player every turn, if you get pulled in or after enough time has passed, the black hole will explode, creating a chasm where it was and dealing are damage.

    Phase 3:
    Brings the player into an empty, completely black level. Takes away all of your levels and items. Once you have collected all of them back, you will have to break a void heart which will deal 100 damage to the boss and return you to the regular world.
    While in the shadow world, you will have to dodge lasers every turn, wraiths will spawn periodically and black holes will pull you in.

    Phase 4:
    Can now sustain 3 minions at once, swapping between minions very often, other attacks become more frequent and powerful.
    At 1 HP: Sends you into the shadow realm one more time where a barrage of bombs and lasers is thrown every turn until you find the void heart, killing it will kill the boss.
    */

    private VelEssence lastEssence;

	private int phase = 0;

	int targetpos;

	private int abilityCooldown = 10;
    private int blackHoleCooldown = 3;

	private ArrayList<Emitter> EmittersSave = new ArrayList<>();

	@Override
	public int attackSkill(Char target) {
		return INFINITE_ACCURACY;
	}

	@Override
	protected boolean act() {

		if (phase == 4 && HP > 300 && !shouldTrap) {
            finalMoment();
            return true;
        }

		//char logic
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		throwItems();

		sprite.hideAlert();
		sprite.hideLost();

		//mob logic
		enemy = chooseEnemy();

		enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
		//end of char/mob logic

		if (phase == 0){
			if (Dungeon.hero.viewDistance >= Dungeon.level.distance(pos, Dungeon.hero.pos)) {
				Dungeon.observe();
			}
			if (Dungeon.level.heroFOV[pos]) {
				notice();
			}
		}
        else {
            int max = 1;
            if (phase > 2) max++;
            if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) max++;


            if (shouldTrap)
            {
				shouldTrap = false;
                TrapPlayer();
            }

            if (blackHoleCooldown < 1 && findBlackHole() == null && phase > 1 )
            {
                Mob m = new BlackHole();
                yell(Messages.get(this, "holespawn"));
                Sample.INSTANCE.play(Assets.Sounds.BEACON, 1f, 1f);



                m.pos = Dungeon.level.randomRespawnCell(m);
                GameScene.add(m, 2);
                ScrollOfTeleportation.teleportChar(m);
                Dungeon.level.occupyCell(m);

                blackHoleCooldown = Random.Int(30, 40);
            }
            if (findEssences().size() < max) {

                VelEssence e;
                boolean permitted = true;

                do {
                    permitted = true;
                    e = VelEssence.random();

                    for (VelEssence ess : findEssences())
                    {
                        if (e.getClass().equals(ess.getClass())) {
                            permitted = false;
                            break;
                        }
                    }

                }
                while ((lastEssence == null || e.getClass().equals( lastEssence.getClass()))
                && !permitted
                );
				addEssence(e);
                lastEssence = e;

			}
			else if (abilityCooldown == 2)
			{

				targetpos = Dungeon.hero.pos;

				Ballistica aim = new Ballistica(pos, targetpos, Ballistica.WONT_STOP);

				ConeAOE cone = new ConeAOE(aim,
						99f,
						10 + ((phase - 1) * 5),
						Ballistica.WONT_STOP);

				for (int cell : cone.cells) {
                    EmittersSave.add(CellEmitter.get(cell));
				}
                for (Emitter e : EmittersSave) {
                    e.pour(PurpleParticle.BURST, 0.1f);
                }




			}
			else if (abilityCooldown == 0)
			{

				Ballistica aim = new Ballistica(pos, targetpos, Ballistica.WONT_STOP);
                boolean affected = false;


                ConeAOE cone = new ConeAOE(aim,
						99f,
						18,
						Ballistica.WONT_STOP);

				for (int cell : cone.cells) {
                    for (Emitter e : EmittersSave) {
                        e.burst(VoidSnipeParticle.MAIN, 4);
                    }

                    EmittersSave.clear();

                    Char affect = Actor.findChar(cell);

                    if (affect != null && affect.alignment != this.alignment)
                    {
                        if (affect == Dungeon.hero) {
                            Statistics.bossScores[4] -= 100;
                        }

                        affected = true;
                        int amnt = Random.Int(11, 38);
                        Buff.affect(this, EldritchBarrier.class).incShield(amnt);
                        BuffEssences();
                        sprite.parent.add(new Chains(sprite.center(),
                                affect.sprite.destinationCenter(),
                                Effects.Type.VOID_DOTS,
                                new Callback() {
                                    public void call() {
                                        next();
                                        affect.sprite.burst( 0x00CC6666, 10 );
                                    }
                                }));

                    }


				}
				abilityCooldown = Random.Int(5, 11);
                if (affected) return false;
			}


			if (abilityCooldown > 0) abilityCooldown -= Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  2 : 1;
            if (blackHoleCooldown > 0 && findBlackHole() == null) blackHoleCooldown--;

        }

		spend(1f);

		return true;
	}

    private void BuffEssences() {

        for ( Char c : Actor.chars() ){
            if (c instanceof VelEssence){
                Buff.prolong(c, Adrenaline.class, 7f);
            }
        }

    }


	@Override
	public void damage( int dmg, Object src ) {

		int preHP = HP;
		super.damage( dmg, src );

		if (phase == 0) return;

		if (phase < 5) {
			HP = Math.max(HP, (HT - 500 * phase)-1);
		}
		int dmgTaken = preHP - HP;

        //Teleport at every 100HP threshold
        int a = (int) (preHP * 0.01f);
        int b = (int) (HP * 0.01f);
        if (b < a && HP < 1950) ScrollOfTeleportation.teleportChar(this);

		//ToDo: Implement phase logic
        if (phase < 5 && HP <= HT - 500*phase) {
            phase++;
			if (phase == 4) shouldTrap = true;
        }

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/3f);
			else                                                    lock.addTime(dmgTaken/2f);
		}

	}


	public void updateVisibility( Level level ){
		int viewDistance = 4;
		if (phase > 1 && isAlive()){
			viewDistance = Math.max(4 - (phase-1), 1);
		}
		if (Dungeon.isChallenged(Challenges.DARKNESS)) {
			viewDistance = Math.min(viewDistance, 2);
		}
		level.viewDistance = viewDistance;
		if (Dungeon.hero != null) {
			if (Dungeon.hero.buff(Light.class) == null) {
				Dungeon.hero.viewDistance = level.viewDistance;
			}
			Dungeon.observe();
		}
	}

	@Override
	public void beckon( int cell ) {
	}

	@Override
	public void clearEnemy() {
		//do nothing
	}

	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

		Bestiary.skipCountingEncounters = true;
		//ToDo: Update this for Vels summons
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof VelEssence) {
				mob.die( cause );
			}
		}
		Bestiary.skipCountingEncounters = false;

		updateVisibility(Dungeon.level);

		GameScene.bossSlain();

        int totaltp = 0;
        totaltp += Dungeon.hero.talentPointsAvailable(1) + Dungeon.hero.talentPointsAvailable(2) + Dungeon.hero.talentPointsAvailable(3) + Dungeon.hero.talentPointsAvailable(4);

        if (totaltp > 6 && (Dungeon.hero.subClass == null || Dungeon.hero.armorAbility == null)) {}
        else Statistics.qualifiedForBossChallengeBadge = false;

		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) && Statistics.spawnersAlive == 4){
			Badges.validateBossChallengeCompleted();
		} else {
			Statistics.qualifiedForBossChallengeBadge = false;
		}
		Statistics.bossScores[4] += 7000;

		Badges.validateTakingTheMick(cause);

		Dungeon.level.unseal();
		super.die( cause );

		yell( Messages.get(this, "defeated") );
	}

	@Override
	public void notice() {
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.play(Assets.Music.VOID_BOSS, true);
				}
			});
			if (phase == 0) {
				phase = 1;
			}
		}
	}

	@Override
	public String description() {
		String desc = super.description();

		if (Statistics.spawnersAlive > 0){
			desc += "\n\n" + Messages.get(this, "desc_spawners");
		}

		return desc;
	}

	private static final String PHASE = "phase";

	public String TARGETPOS = "TARGETPOS";
	public String ABILITYCOOLDOWN = "ABILITYCOOLDOWN";
	public String BLACKHOLECOOLDOWN = "BLACKHOLECOOLDOWN";
	public String SHOULDTRAP = "SHOULDTRAP";
	public String LASTESSENCE = "LASTESSENCE";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PHASE, phase);
		bundle.put(TARGETPOS, TARGETPOS);
		bundle.put(ABILITYCOOLDOWN, abilityCooldown);
		bundle.put(BLACKHOLECOOLDOWN, blackHoleCooldown);
		bundle.put(SHOULDTRAP, shouldTrap);
		bundle.put(LASTESSENCE, lastEssence);

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

        VoidBossLevel level = null;
        if (Dungeon.level instanceof VoidBossLevel) level = (VoidBossLevel)Dungeon.level;

        VoidBossLevel.Boss = this;

		super.restoreFromBundle(bundle);
		phase = bundle.getInt(PHASE);
		if (phase != 0) {
			BossHealthBar.assignBoss(this);
			if (phase == 5) BossHealthBar.bleed(true);
		}
		targetpos = bundle.getInt(TARGETPOS);
		abilityCooldown = bundle.getInt(ABILITYCOOLDOWN);
		blackHoleCooldown = bundle.getInt(BLACKHOLECOOLDOWN);
		shouldTrap = bundle.getBoolean(SHOULDTRAP);
		lastEssence = (VelEssence)bundle.get(LASTESSENCE);

	}

    private ArrayList<VelEssence> findEssences(){
        ArrayList<VelEssence> V = new ArrayList<>();
        for ( Char c : Actor.chars() ){
            if (c instanceof VelEssence){
                V.add((VelEssence)c);
            }
        }
        return V;
    }

    public static BlackHole findBlackHole(){
        ArrayList<VelEssence> V = new ArrayList<>();
        for ( Char c : Actor.chars() ){
            if (c instanceof BlackHole){
                return (BlackHole)c;
            }
        }
        return null;
    }

    public void addEssence(VelEssence essence){
        essence.pos = Dungeon.level.exit();

        CellEmitter.get(Dungeon.level.exit()-1).burst(ShadowParticle.UP, 25);
        CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
        CellEmitter.get(Dungeon.level.exit()+1).burst(ShadowParticle.UP, 25);

        int targetPos = Dungeon.level.exit() + Dungeon.level.width();

        if (!Dungeon.isChallenged(Challenges.STRONGER_BOSSES)
                && (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep)){
            essence.pos = targetPos;
        } else if (Actor.findChar(targetPos-1) == null || Actor.findChar(targetPos-1) instanceof Sheep){
            essence.pos = targetPos-1;
        } else if (Actor.findChar(targetPos+1) == null || Actor.findChar(targetPos+1) instanceof Sheep){
            essence.pos = targetPos+1;
        } else if (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep){
            essence.pos = targetPos;
        }

        if (Actor.findChar(essence.pos) instanceof Sheep){
            Actor.findChar(essence.pos).die(null);
        }

        essence.TimeUntilDeath = Random.Int(17, 45);

        GameScene.add(essence, 4);
        Actor.add( new Pushing( essence, Dungeon.level.exit(), essence.pos ) );
        Dungeon.level.occupyCell(essence);
    }

    private boolean shouldTrap = false;

    private void TrapPlayer()
    {
		shouldTrap = false;

        Level.beforeTransition();
        Dungeon.hero.interrupt();
		InterlevelScene.mode = InterlevelScene.Mode.TRAP;
        Game.switchScene( InterlevelScene.class );
    }

	private void finalMoment()
	{
		damage(300, Dungeon.hero);
		if (Dungeon.level.heroFOV[pos])com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.FALLING, 0.9f, 0.6f);

	}


}
