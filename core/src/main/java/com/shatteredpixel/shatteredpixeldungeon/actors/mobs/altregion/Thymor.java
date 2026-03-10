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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Smog;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.RoyalSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.levels.CitadelBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThymorSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WendarSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thymor extends Mob {

	{
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  800 : 600;
		EXP = 10;
		defenseSkill = 30;
		spriteClass = ThymorSprite.class;
		baseSpeed = 1f;

		flying = true;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);

	}

	private float timer;

	private float combocooldown = 2f;

	public boolean TYCombo = false;
	public boolean TWCombo = false;
	public boolean YWCombo = false;
    public boolean TripleCombo = false;

	@Override
	protected void spend( float time ) {

        if (Dungeon.hero != null )
        {
            //This kind of allows some strats where you immediately equip and unequip something but its probably not that bad
            if (Dungeon.hero.belongings.weapon != null ||
                Dungeon.hero.belongings.armor != null ||
                Dungeon.hero.belongings.ring != null ||
                Dungeon.hero.belongings.artifact != null ||
                Dungeon.hero.belongings.secondWep != null ||
                Dungeon.hero.belongings.misc != null
            ) Statistics.qualifiedForBossChallengeBadge = false;

        }

		timer+= time;

		if (HP * 2 <= HT && combocooldown > 0
		&& (!TYCombo && !TWCombo && !YWCombo && !TripleCombo)
		) combocooldown--;


		if (combocooldown <= 0) {

			CharSprite YuriaSprite = null;
			CharSprite WendarSprite = null;

			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob instanceof Yuria) YuriaSprite = mob.sprite;
				if (mob instanceof Wendar) WendarSprite = mob.sprite;
			}

			int t = Random.Int(100);

            if ((HP * 4 <= HT) && Random.Int(100) > 50)
            {
                TripleCombo = true;
                this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
                YuriaSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
                WendarSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
            }
            else {
			if (t <= 33 && YuriaSprite != null) 	  {
				TYCombo = true;
				this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
				YuriaSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
			}
			else if (t <= 66 && WendarSprite != null) {
				TWCombo = true;
				this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
				WendarSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
			}
			else if (t <= 101 && YuriaSprite != null && WendarSprite != null) {
				YWCombo = true;
				YuriaSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
				WendarSprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "charge") );
			}
            }

            if ((HP * 4 <= HT)) combocooldown = 10;
            else if ((HP * 2 <= HT)) combocooldown = 15;
			if (TripleCombo) combocooldown *= 3;
		}


		super.spend( time );
	}

	@Override
	protected boolean getCloser( int target ) {
		if (TYCombo) target = level.yuria.pos;
		if (TWCombo) target = level.wendar.pos;
		return super.getCloser( target );
	}

	boolean Yuriaexists = false;
	boolean Wendarexists = false;

	@Override
	protected boolean act() {

		BossHealthBar.assignBoss(this);
		if ((HP * 2 <= HT)) BossHealthBar.bleed(true);

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		((CitadelBossLevel)Dungeon.level).boss = this;

		if (Actor.findChar(target) == null) target = Dungeon.hero.pos;

		if (!level.yuria.isAlive() || !level.wendar.isAlive()) die(null);

		if (timer >= 100 && !Yuriaexists) {
			level.yuriaappearance();
			Yuriaexists = true;
		}
		if (timer >= 200 && !Wendarexists) {
			level.wendarappearance();
			Wendarexists = true;
		}

		if (TripleCombo && Dungeon.level.distance(level.yuria.pos, pos) < 2 && Dungeon.level.distance(level.wendar.pos, pos) < 2) {

			VoidBombTracker nova = Buff.append(Dungeon.hero, VoidBombTracker.class);
			nova.pos = pos;

			this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "void") );

			TripleCombo = false;
            TYCombo = false;
            TWCombo = false;
            YWCombo = false;

			spend(3f);
			return true;
		}

		if (TYCombo && Dungeon.level.distance(level.yuria.pos, pos) < 2)
		{

			Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.5f);
			PixelScene.shake(2, 0.5f);


			Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

			sprite.play( ((ThymorSprite)sprite).cast );

			Sample.INSTANCE.play(Assets.Sounds.EVOKE);

			ConeAOE cone = new ConeAOE(aim,
					5f,
					70,
					Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

			sprite.zap(pos);
			spend(1f);

			for (int cell : cone.cells) {
				CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 5);
				if (Actor.findChar(cell) != null) {
					Actor.findChar(cell).damage(Random.NormalIntRange(6, 8), this);
					Buff.affect(Actor.findChar(cell), Paralysis.class, 4f);
				}
			}

			TYCombo = false;

			spend(TICK);
			return true;
		}



		return super.act();

	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 3 ) == 0) {
			Buff.affect( enemy, CourtCurse.class, CourtCurse.DURATION );
		}

		return super.attackProc( enemy, damage );
	}

	CitadelBossLevel level;



	@Override
	public void damage(int dmg, Object src) {

		if (Dungeon.level instanceof CitadelBossLevel) level = (CitadelBossLevel)Dungeon.level;

		timer += (dmg/4f);
        if (dmg > 40) dmg = ((int) (36 + (dmg * 0.1)));


        boolean doInvinc = false;
        //When reaching final phase, instantly activate triple combo
        if (HP * 4 >= HT && ( (HP - dmg/2.5f ) * 4 <= HT )) doInvinc = true;

		super.damage((int)(dmg/2.5f), src);
		level.yuria.HP=this.HP;
		level.wendar.HP=this.HP;

        if (doInvinc)
        {
            doInvinc = false;
            Buff.affect(this, Invulnerability.class, 8f);
            Buff.affect(level.yuria, Invulnerability.class, 8f);
            Buff.affect(level.wendar, Invulnerability.class, 8f);

            TripleCombo = true;

        }
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 11, 20 );
	}


	@Override
	public int attackSkill(Char target) {
		int attack = 16;
		if (HP * 2 <= HT) attack = 22;
		return attack;
	}

    @Override
    public float attackDelay() {
        return Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ?  0.5f : 1;
    }


	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 7);
	}


	{
		immunities.add(Sleep.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
		resistances.add(Doom.class);
		immunities.add(Fire.class);
		immunities.add(Smog.class);
	}

	private static final String TIMER = "TIMER";

	private static final String YURIAEXISTS = "YURIAEXISTS";

	private static final String WENDAREXISTS = "WENDAREXISTS";
	@Override
	public void storeInBundle(Bundle bundle) {

		bundle.put(TIMER, timer);
		bundle.put(YURIAEXISTS, Yuriaexists);
		bundle.put(WENDAREXISTS, Wendarexists);


		super.storeInBundle(bundle);


	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);

		timer = bundle.getFloat( TIMER );
		Wendarexists = bundle.getBoolean( WENDAREXISTS );
		Yuriaexists = bundle.getBoolean( YURIAEXISTS );

		BossHealthBar.assignBoss(this);
		if ((HP * 2 <= HT)) BossHealthBar.bleed(true);


	}


	@Override
	public void die(Object cause) {

		super.die(cause);

		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			mob.die(null);
		}

		Dungeon.level.unseal();

		GameScene.bossSlain();

		Camera.main.shake( 3, 1f );

		if (Dungeon.level.solid[pos]){
			Heap h = Dungeon.level.heaps.get(pos);
			if (h != null) {
				for (Item i : h.items) {
					Dungeon.level.drop(i, pos + Dungeon.level.width());
				}
				h.destroy();
			}
			Dungeon.level.drop(new RoyalSeal(), pos + Dungeon.level.width()).sprite.drop(pos);
		} else {
			Dungeon.level.drop(new RoyalSeal(), pos).sprite.drop();
		}

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge) {
			Badges.validateBossChallengeCompleted();
		}
        Statistics.bossScores[3] += 4000;


		yell(Messages.get(this, "defeated"));

        next();
	}



}
