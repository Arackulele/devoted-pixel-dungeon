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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wrath;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Demon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FriendlyEye;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class BloodVial extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_VIAL;

        exp = 0;
        levelCap = 10;

        charge = 0;
        partialCharge = 0;
        chargeCap = Math.min(level()+1, 10);


        unique = true;
        bones = false;

        defaultAction = AC_DRINK;
    }

    public static final String AC_DRINK = "DRINK";
    public static final String AC_STAB = "STAB";
    public static final String COOLDOWN = "cooldown";
    private int damage;
    private Demon demon = null;
    private FriendlyEye eye = null;

    public int bloodvialcooldown;
    public int wrathamount;
    @Override
    protected ArtifactBuff passiveBuff() {
        return new vialRecharge();
    }

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if ((isEquipped( hero ))
                && !cursed
                && hero.buff(MagicImmune.class) == null
                && (charge > 0 || activeBuff != null)) {
            actions.add(AC_DRINK);
        }
        if ((isEquipped( hero ))
                && !cursed
                && hero.buff(MagicImmune.class) == null
                && (charge < chargeCap || activeBuff != null)) {
            actions.add(AC_STAB);
        }
        return actions;
    }



    public void onUpgradeScrollUsed( Hero hero ) {
        if (hero.hasTalent(Talent.CONJURE_BLOOD)){
            if (hero.pointsInTalent(Talent.CONJURE_BLOOD)==2) charge++;
            else if (Random.Int(100)<66) charge++;
        }
    }



    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_DRINK)) {

            if (activeBuff == null) {

                if (!isEquipped(hero))
                    GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                else if (cursed) GLog.i(Messages.get(this, "cursed"));
                else if (charge <= 0) GLog.i(Messages.get(this, "no_charge"));
                else {

                    ArrayList<Integer> spawnPoints = new ArrayList<>();
                    for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                        int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                        if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                            spawnPoints.add(p);
                        }
                    }


                    hero.busy();
                    Sample.INSTANCE.play(Assets.Sounds.MELD);
                    Talent.onArtifactUsed(Dungeon.hero);
                    hero.sprite.operate(hero.pos);
                    Buff.prolong(hero, AnkhInvulnerability.class, 2 + level());
                    charge--;
                    bloodvialcooldown = 5 + level();

                    if (hero.pointsInTalent(Talent.DISTURBED_SENSES) != 0) {
                        if (hero.pointsInTalent(Talent.DISTURBED_SENSES) == 2)  Buff.affect(hero, Haste.class, 4);
                         else if (Random.Int(10)>4)  Buff.affect(hero, Haste.class, 4);
                    }

                    if (hero.subClass == HeroSubClass.ARCHANGEL) {

                        ArrayList<Integer> respawnPoints = new ArrayList<>();

                        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                                respawnPoints.add( p );
                            }
                        }

                        int index = Random.index( respawnPoints );

                        Demon mob = new Demon();
                        mob.summonDemon(hero);
                        GameScene.add( mob );
                        mob.pos = Random.element(spawnPoints);
                        ScrollOfTeleportation.appear( mob, respawnPoints.get( index ) );

                        if (hero.pointsInTalent(Talent.DEMON_EYE) > 0) {
                            FriendlyEye mob2 = new FriendlyEye();
                            mob2.summonDemon(hero);
                            GameScene.add( mob2 );
                            mob2.pos = Random.element(spawnPoints);
                            ScrollOfTeleportation.appear( mob2, respawnPoints.get( index ) );


                        }
                        respawnPoints.remove( index );
                    }

                    if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) != 0) {
                        if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) == 2) {}
                        else if(Random.Int(10)>5) hero.spend(1f);
                    }
                    else hero.spend(1f);
                }

            }

        }
        else if (action.equals(AC_STAB)) {


            if (bloodvialcooldown <= 0) {
                if (!isEquipped(hero))
                    GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                else if (cursed) GLog.i(Messages.get(this, "cursed"));
                else if (charge > chargeCap) GLog.i(Messages.get(this, "no_charge"));
                else if
                (hero.hasTalent(Talent.NIHILISM)) {
                        if (Random.Int(10)<=hero.pointsInTalent(Talent.NIHILISM)) damage = ((int)hero.lvl/2);
                            else damage = (2 + hero.lvl);
                    }
                    else damage = (2 + hero.lvl);
                    hero.spend(2f);
                    hero.busy();
                    Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
                    charge++;
                    hero.damage(damage, this);
                    Talent.onArtifactUsed(Dungeon.hero);
                    hero.sprite.operate(hero.pos);
                    exp+=2;
                    if (hero.pointsInTalent(Talent.PAIN_IS_GAIN) != 0) {
                        Buff.affect(hero, Barrier.class).setShield(2 * hero.pointsInTalent(Talent.PAIN_IS_GAIN));
                    }
                        if (hero.buff(Wrath.class) != null) wrathamount = (int)hero.buff(Wrath.class).visualcooldown();

                        if (hero.subClass == HeroSubClass.DEVOTEE && wrathamount > 0){

                            if (hero.hasTalent(Talent.DIVINE_PROTECTION))
                            {
                                Buff.affect(hero, Bless.class,(int) (wrathamount*0.2)*hero.pointsInTalent(Talent.DIVINE_PROTECTION));
                            };

                            Buff.affect(hero, Healing.class).setHeal(wrathamount,(float)wrathamount,0);
                            //for_talent
                            //Buff.affect(hero, ArtifactRecharge.class);
                            Buff.detach(hero, Wrath.class);
                            Sample.INSTANCE.play( Assets.Sounds.BLAST );

                            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                                if (Dungeon.level.heroFOV[mob.pos]) {
                                    //deals 10%HT, plus half of wrath
                                    mob.damage(Math.round(mob.HT/10f + wrathamount/2), this);
                                    if (hero.pointsInTalent(Talent.FACE_OF_DEATH) > 0) Buff.affect(mob, Charm.class, hero.pointsInTalent(Talent.FACE_OF_DEATH));
                                }
                            }
                        }


                }
            }
            else GLog.w(Messages.get(this, "COOLDOWN"));


    }
    public class vialRecharge extends ArtifactBuff{

        @Override
        public boolean act() {
            if (bloodvialcooldown > 0) {
                updateQuickslot();

                bloodvialcooldown--;
            }
            if (exp >= 3+level()*7 && level() < levelCap) {
                exp-= 3+level()*7;
                upgrade();
                chargeCap = (1 + (level() / 3));
            };
            spend( TICK );
            return true;
        }

    }




}
