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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.BlackHole;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.QuakeWolf;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SageCorpse;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.VoidSnipeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackHoleSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VoidOrbSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class VoidTrapLevel extends Level {

	public static final int SIZE = 32;

    public static VoidTrapLevel instance;
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

        viewDistance = Math.min(4, viewDistance);

    }

    @Override
    public void playLevelMusic() {
            Music.INSTANCE.play(Assets.Music.SHADOW_REALM, true);
    }
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_TRUE_VOID;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_VOID;
	}
	
	@Override
	protected boolean build() {

		
		setSize(35, 35);
		
		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * width() + j] = Terrain.EMPTY;
			}
		}

		
		int entrance = 1;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));


        return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
        AddBelongings(Dungeon.hero.lvl, Dungeon.hero.exp);
        Dungeon.hero.lvl = 1;
        Dungeon.hero.HP = Dungeon.hero.HT = 20;
        Dungeon.hero.exp = 0;
	}

	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {

	}


    @Override
    public Group addWallVisuals() {
        super.addWallVisuals();
        addCityWallVisuals( this, wallVisuals );
        return wallVisuals;
    }

    public static void addCityWallVisuals( Level level, Group group ) {
        for (int i=0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL || level.map[i] == Terrain.SOLID) {
                group.add( new WallFlame( i ) );
            }
        }
    }

    public static class WallFlame extends Emitter {

        private int pos;

        public static final Emitter.Factory factory = new Factory() {
            @Override
            public void emit( Emitter emitter, int index, float x, float y ) {
                VoidSnipeParticle p = (VoidSnipeParticle) emitter.recycle( VoidSnipeParticle.class );
                p.reset( x, y );
            }
            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public WallFlame( int pos ) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.raisedTileCenterToWorld( pos );
            pos( p.x - 2, p.y - 10, 13, 13 );

            pour( factory, 0.07f );
        }

        @Override
        public void update() {
            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
                super.update();
            }

        }

    }

    public void AddBelongings(int heroLVL, int heroxp)
    {

        while (heroLVL > 0)
        {

            Mob m = new VoidOrb();
            do {
                m.pos = randomRespawnCell(m);
            } while (map[m.pos] != Terrain.EMPTY);
            if (m.pos < 0) m.pos = length/2;
            mobs.add(m);
            //GameScene.add(m, 1);
            //ScrollOfTeleportation.teleportChar(m);
            //occupyCell(m);
            heroLVL--;
        }

        Mob m = new ExitPortal();
        m.pos = randomRespawnCell(m);
        if (m.pos < 0) m.pos = length/2;
        mobs.add(m);

        for ( Item i : Dungeon.hero.belongings.getAllItems(Item.class))
        {
            if (!i.isEquipped(Dungeon.hero) && !(i instanceof Bag) && !(i instanceof Trinket)) {
                Item newone = i.detachAll(Dungeon.hero.belongings.backpack);
                int p = randomRespawnCell(null);
                drop(newone, p);
            }
        }

        Buff.affect( Dungeon.hero, TrapAttacks.class );
        PotionOfHealing.cure(Dungeon.hero);
        Buff.detach(Dungeon.hero, Ooze.class);


    }

    public static class VoidOrb extends Mob {

        {
            spriteClass = VoidOrbSprite.class;

            HP = HT = 1;
            defenseSkill = 0;

            maxLvl = -1;

            state = HUNTING;


            properties.add(Char.Property.INORGANIC);
            properties.add(Char.Property.IMMOVABLE);
            properties.add(Char.Property.STATIC);

        }

        public void damage(int dmg, Object src) {
            if (src instanceof Hero) super.damage(dmg, src);
        }

        @Override
        protected boolean act() {



            spend(1f);
            return true;
        }

        @Override
        public void die( Object cause ) {


            Buff.affect( Dungeon.hero, MindVision.class, 2f );
            Buff.affect( Dungeon.hero, Awareness.class, 2f );
            Dungeon.observe();


            Dungeon.hero.earnExp(Dungeon.hero.maxExp(), null);
            super.die(cause);
        }

    }

    public static class ExitPortal extends Mob {

        {
            spriteClass = BlackHoleSprite.class;

            HP = HT = 1;
            defenseSkill = 0;

            maxLvl = -1;

            state = HUNTING;


            properties.add(Char.Property.INORGANIC);
            properties.add(Char.Property.IMMOVABLE);
            properties.add(Char.Property.STATIC);

        }

        @Override
        protected boolean act() {
            spend(1f);
            return true;
        }

        @Override
        public boolean isInvulnerable(Class effect) {

            boolean AllowedToLeave = true;
            for (Mob m : Dungeon.level.mobs)
            {

                if (m instanceof VoidOrb) {
                    AllowedToLeave = false;
                    break;
                }

            }
            //If the player is already max level there shouldnt be any reason to kill more orbs
            if (Dungeon.hero.lvl == 30) AllowedToLeave = true;

            if (!AllowedToLeave)GLog.n( Messages.get(this, "noleave") );

            //immune to damage when inactive
            return  !AllowedToLeave || super.isInvulnerable(effect);
        }

        @Override
        public void die( Object cause ) {

            Buff.detach( Dungeon.hero, TrapAttacks.class );

            Level.beforeTransition();
            Dungeon.hero.interrupt();
            Game.switchScene( InterlevelScene.class );
            InterlevelScene.mode = InterlevelScene.Mode.TRAP;
        }

    }

    public static class TrapAttacks extends Buff {


        private float scale = 1;
        public String SCALE = "scale";

        @Override
        public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
            super.restoreFromBundle(bundle);
            scale = bundle.getFloat(SCALE);
        }

        @Override
        public void storeInBundle(com.watabou.utils.Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SCALE, scale);
        }

        private int BombCooldown = 5;
        private int WraithCooldown = 10;
        private int LaserCooldown = 3;
        private ArrayList<Integer> LaserTiles = new ArrayList<>();
        private ArrayList<Ballistica> Lasers = new ArrayList<>();

        @Override
        public boolean act() {
            spend( 1f );

            //Periodically spawn wraiths
            //Shoot area attacks that the player has to dodge
            //spawn a black hole perhaps
            //spawn bombs that explode on the player

            if (BombCooldown < 1 && Dungeon.hero.HT > 35)
            {

                PathFinder.buildDistanceMap( target.pos, BArray.not( Dungeon.level.solid, null ), 6 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {

                        if (Random.Int(0, 12) == 3) {
                            Bomb b = new Bomb();
                            b.image = ItemSpriteSheet.OBLIVION_SHARD;
                            b.finaldelay = Random.Int(3) + 3;
                            b.subBomb = true;
                            Dungeon.level.drop(b, i).sprite.drop();
                            b.onThrow(i);
                        }

                    }
                }

                BombCooldown = 8;
            }
            else BombCooldown--;

            if (WraithCooldown < 1 && Dungeon.hero.HT > 20)
            {

                    ArrayList<Integer> candidates = new ArrayList<>();
                    //min distance scales based on hero's view distance
                    // wraiths must spawn at least 4/3/2/1 tiles away at view distance of 8(default)/7/4/1
                    int minDist = Math.round(Dungeon.hero.viewDistance/2f);
                    for (int i = 0; i < Dungeon.level.length(); i++){
                        if (Dungeon.level.heroFOV[i]
                                && !Dungeon.level.solid[i]
                                && Actor.findChar( i ) == null
                                && Dungeon.level.distance(i, Dungeon.hero.pos) > minDist){
                            candidates.add(i);
                        }
                    }
                    if (!candidates.isEmpty()){
                        Wraith.spawnAt(Random.element(candidates), CorpseDust.DustWraith.class);
                        Sample.INSTANCE.play(Assets.Sounds.CURSED);
                        WraithCooldown = Random.Int(6, 12);
                    }

                }
            else WraithCooldown--;

            if (LaserCooldown < 1)
            {
                for (int i : LaserTiles)
                {
                    CellEmitter.get(i).burst(MagicMissile.WardParticle.FACTORY, 8);
                    Char ch = Actor.findChar(i);
                    if (ch != null && ch.alignment != Char.Alignment.ENEMY) {
                        //Do percentage based damage to prevent unfairness
                        int dmg = com.watabou.utils.Random.NormalIntRange(Dungeon.hero.HT / 10, (Dungeon.hero.HT / 2) + 3);
                        if (Dungeon.hero.HT < 40) dmg = Math.min(dmg, Dungeon.hero.HP -(Dungeon.hero.HP-1));
                        if (dmg < 0) dmg = 0;

                        WandOfMagicMissile m = new WandOfMagicMissile();
                        ch.damage(dmg, m);
                    }
                }
                for(Ballistica b : Lasers)
                {

                    target.sprite.parent.add(new Beam.DeathRay(DungeonTilemap.raisedTileCenterToWorld(b.sourcePos), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));

                }
                LaserTiles.clear();
                Lasers.clear();

                LaserCooldown = Random.Int(5, 8);
            }
            else if (LaserCooldown == 1)
            {
                for(int i = Random.Int(1, 4); i > 0; i--)
                {
                    int startpos = -1;
                    int endpos = -1;

                    do {
                        startpos = Random.Int(0, Dungeon.level.length);
                    }
                    while (Dungeon.hero.fieldOfView[startpos]);

                    do {
                        endpos = Random.Int(0, Dungeon.level.length);
                    }
                    while (!Dungeon.hero.fieldOfView[endpos]);

                    Ballistica b = new Ballistica(startpos, endpos, Ballistica.WONT_STOP);
                    for (int f : b.path)
                    {
                        LaserTiles.add(f);
                        target.sprite.parent.add(new TargetedCell(f, 0xFF0000));
                    }
                    Lasers.add(b);



                }
                LaserCooldown--;
            }
            else LaserCooldown--;

            return true;
        }

    }


}
