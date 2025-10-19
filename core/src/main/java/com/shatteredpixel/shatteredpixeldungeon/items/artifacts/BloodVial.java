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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
//import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wrath;
//import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Demon;
//import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FriendlyEye;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.watabou.utils.PathFinder;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;

public class BloodVial extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_VIAL3;

        exp = 0;
        levelCap = 10;

        charge = (1 + (level() / 3));
        partialCharge = 0;
        chargeCap = (1 + (level() / 3));


        unique = true;
        bones = false;

        defaultAction = AC_DRINK;
    }

    public static final String AC_DRINK = "DRINK";
    public static final String AC_STAB = "STAB";
    private int damage;
        // private Demon demon = null;
        // private FriendlyEye eye = null;

    public int bloodvialcooldown;
    @Override
    protected ArtifactBuff passiveBuff() {
        return new vialRecharge();
    }


    @Override
    public String info() {
        String info = super.info();

        Trinket ref = null;
        if (Dungeon.hero != null) {
            ref = Dungeon.hero.belongings.getItem(Trinket.class);

        if (Dungeon.hero.subClass == HeroSubClass.DEVOTEE){
            if (ref == null) info += "\n\n" + Messages.get(this, "no_trinket");
            else info += "\n\n" + Messages.get(ref, "conjurer_desc");
        }
        }

        return info;
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



    /*public static void onUpgradeScrollUsed( Hero hero ) {
        if (hero.hasTalent(Talent.CONJURE_BLOOD)) {
            BloodVial vial = hero.belongings.getItem(BloodVial.class);
            if (vial != null) {
                if (hero.pointsInTalent(Talent.CONJURE_BLOOD) == 2) {
                    vial.charge();
                    vial.charge();
                }
                else if(hero.hasTalent(Talent.CONJURE_BLOOD)) vial.charge();
            }
        }
    }*/
    public Boolean BloodVialEmpty;
    public void getcharge()
    {
        if (this.charge > 0) BloodVialEmpty = false;
                else BloodVialEmpty = true;


    }

    private int staling;

    //Censer buffs are listed in order of severity for being selected
    static ArrayList<Class<? extends Buff>> censerbuffs = new ArrayList<>();
    static{
        censerbuffs.add(Weakness.class);
        censerbuffs.add(Weakness.class);
        censerbuffs.add(Cripple.class);
        censerbuffs.add(Blindness.class);

        censerbuffs.add(Chill.class);
        censerbuffs.add(Ooze.class);
        censerbuffs.add(Roots.class);
        censerbuffs.add(Burning.class);
        censerbuffs.add(Poison.class);
    };

    Trap[] trapClasses = new Trap[]{
            new ChillingTrap(),new ShockingTrap(),new ToxicTrap(),new WornDartTrap(),
            new AlarmTrap(),new OozeTrap(),
            new ConfusionTrap() ,new FlockTrap() ,new SummoningTrap() ,new TeleportationTrap() ,new GatewayTrap()
    };

    public void charge()
    {
        charge++;
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
                    Buff.prolong(hero, Invulnerability.class, 2 + (int) (level() * 0.5 + 1));

                    if (Dungeon.hero.subClass == HeroSubClass.CONJURER) Buff.affect(Dungeon.hero, ConjureAbility.class, 2 + (int) (level() * 0.5 + 1));

                    charge--;

                    //visuals
                    UpdateVisuals();

                    boolean visible = false;
                    boolean anymobs = false;

                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                        anymobs = true;
                        if (Dungeon.level.heroFOV[mob.pos]) {
                            visible = true;
                        }
                    }

                    if (visible == false) staling++;
                    else staling = 0;

                    int variableamt = 4 + (staling * staling);

                    if (anymobs == false) variableamt += 100;

                    bloodvialcooldown = (int) (variableamt + 0.6 + level());

                    if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) != 0) {
                        if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) == 2)  Buff.affect(hero, Haste.class, 4);
                         else Buff.affect(hero, Haste.class, 2);
                    }

                }

                if (hero.pointsInTalent(Talent.NIHILISM) == 3) hero.spend(0.33f);
                else hero.spend(1f);
            }

        } else if (action.equals(AC_STAB)) {

            Item.updateQuickslot();

            if (bloodvialcooldown <= 0) {
                if (!isEquipped(hero))
                    GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                else if (cursed) GLog.i(Messages.get(this, "cursed"));
                else if (charge > chargeCap) GLog.i(Messages.get(this, "no_charge"));



                    else damage = (int)(2 + (hero.lvl * 0.75));

                    if (Dungeon.hero.hasTalent(Talent.NIHILISM))
                    {
                        int reduction = 20 + Dungeon.hero.pointsInTalent((Talent.NIHILISM));
                        reduction *= 0.01;
                        damage -= damage * reduction;
                    }

                    hero.spend(2f);
                    hero.busy();
                    Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
                    charge++;
                    hero.damage(damage, this);
                    Talent.onArtifactUsed(Dungeon.hero);
                    hero.sprite.operate(hero.pos);
                    exp+=2;
                    if (Dungeon.hero.hasTalent(Talent.PAIN_IS_GAIN)) {
                        Buff.affect(hero, Barrier.class).setShield(1 + hero.pointsInTalent(Talent.PAIN_IS_GAIN));
                    }

                    if (Dungeon.hero.hasTalent(Talent.UNHOLY_WRATH)){
                        Buff.prolong(Dungeon.hero, Talent.UnholyWrathTracker.class, 20f);
                    }




                //Logic for Devotee Blast
                if (hero.subClass == HeroSubClass.DEVOTEE && hero.buff(Wrath.class) != null && hero.buff(Wrath.class).cooldown() > 9) {


                    Trinket ref = Dungeon.hero.belongings.getItem(Trinket.class);

                    //no trinket: Deal damage proportional to wrath
                    if (ref == null)
                    {

                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                //deals 10%HT, plus half of wrath
                                mob.damage(Math.round(mob.HT/10f + (int)hero.buff(Wrath.class).cooldown()/2), this);
                            }
                        }

                    }
                    //Chaotic Censer: Applies random debuffs to enemies, severity based on wrath
                    if (ref instanceof ChaoticCenser) {
                        ArrayList<Mob> affectable = new ArrayList<>();
                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }

                        com.watabou.utils.Random.shuffle(affectable);
                        int howmany = (int)hero.buff(Wrath.class).cooldown() - 10;
                        howmany = howmany / 10;
                        howmany += 1;
                        int selection = howmany / 5 + com.watabou.utils.Random.Int(-3, 3);

                        selection = Math.max(1, selection);

                        for (Mob mob: affectable ) {
                            if (howmany > 0)
                                Buff.affect(mob, censerbuffs.get(Math.min(selection, 8)));
                        }
                    }
                    //Dimensional Sundial:
                    if (ref instanceof DimensionalSundial) {

                        Calendar cal = GregorianCalendar.getInstance();
                        if (cal.get(Calendar.HOUR_OF_DAY) >= 20 || cal.get(Calendar.HOUR_OF_DAY) <= 7) {
                            GameScene.add( Blob.seed( hero.pos, 50 + (int)hero.buff(Wrath.class).cooldown()*2, SmokeScreen.class ) );
                        } else {
                            Buff.prolong( hero, Bless.class, 1f + hero.buff(Wrath.class).cooldown()/4 );
                        }


                    }
                    //Exotic Crystals:
                    if (ref instanceof ExoticCrystals) {
                        boolean givenitem = false;

                        int trueWrath = (int)hero.buff(Wrath.class).cooldown() - 10;

                        int StoneChance = 100 - trueWrath;
                        int ScrollChance = 20 + trueWrath;
                        int ExoticChance = 0;
                        if (trueWrath >= 50) ExoticChance += trueWrath;
                        if (trueWrath >= 100) ScrollChance = 0;

                        int r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        if (r < StoneChance)
                        {
                            Item st = Generator.random( Generator.Category.SEED );
                            Dungeon.level.drop(st, hero.pos).sprite.drop();
                            givenitem = true;
                        }
                        r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        Item sc = Generator.random( Generator.Category.POTION );
                        if (r < ScrollChance && !givenitem)
                        {

                            Dungeon.level.drop(sc, hero.pos).sprite.drop();
                            givenitem = true;
                        }
                        r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        if (r < ExoticChance && !givenitem)
                        {

                            Item ex = Generator.random(ExoticPotion.regToExo.get(sc));
                            Dungeon.level.drop(ex, hero.pos).sprite.drop();
                            givenitem = true;
                        }

                        if (!givenitem)
                        {
                            Item st = Generator.random( Generator.Category.SEED );
                            Dungeon.level.drop(st, hero.pos).sprite.drop();
                            givenitem = true;
                        }

                    }
                    //Petrified Seed: Stuns adjacent enemies for a specified amount of time based on wrath
                    if (ref instanceof PetrifiedSeed) {

                        ArrayList<Integer> validtiles = new ArrayList<>();
                        ArrayList<Mob> validmobs = new ArrayList<>();
                        for (int i : PathFinder.NEIGHBOURS9){
                            validtiles.add(hero.pos+i);
                        }

                        for (int validtile : validtiles) {
                            Char ch = Actor.findChar(validtile);
                            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                                Buff.prolong(ch, Paralysis.class, (int)(hero.buff(Wrath.class).cooldown()/3) + 1);
                            }
                        }

                    }
                    //Eye of Newt: Shoots a deathgaze at random enemy in sight, damage is based on wrath amount
                    //ToDo: Maybe it shouldnt just shoot a wand of disint
                    if (ref instanceof EyeOfNewt) {
                        ArrayList<Mob> affectable = new ArrayList<>();

                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }
                        if (affectable.size() > 0)
                        {
                            com.watabou.utils.Random.shuffle(affectable);
                            WandOfDisintegration disint = new WandOfDisintegration();
                            disint.level((int)(hero.buff(Wrath.class).cooldown()/10));
                            disint.tryToZap(hero, affectable.get(0).pos);
                        }

                    }
                    //Ferret Tuft:
                    if (ref instanceof FerretTuft) {
                        Buff.prolong( hero, Flighty.class, 2f + hero.buff(Wrath.class).cooldown()/4 );
                    }
                    //Mossy Clump: Spread grass and plants around while rooting enemies, amount of grass spread and turns rooted based on wrath
                    if (ref instanceof MossyClump) {
                        PathFinder.buildDistanceMap(hero.pos, com.watabou.utils.BArray.not(Dungeon.level.solid, null), 2);
                        for (int i = 0; i < PathFinder.distance.length; i++) {
                            if (PathFinder.distance[i] < Integer.MAX_VALUE) {

                                //CellEmitter.get(i).burst(Speck.factory(Speck.), 5);

                                int terr = Dungeon.level.map[i];

                                if (!(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
                                        terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS)) {

                                } else if (Char.hasProp(Actor.findChar(i), Char.Property.IMMOVABLE)) {

                                } else if (Dungeon.level.plants.get(i) != null) {

                                } else {
                                    if (terr != Terrain.HIGH_GRASS && terr != Terrain.FURROWED_GRASS) {
                                        int r = com.watabou.utils.Random.NormalIntRange(1, 100);

                                        if (r < hero.buff(Wrath.class).cooldown()) {
                                            Level.set(i, Terrain.GRASS);
                                            GameScene.updateMap(i);
                                        }
                                    }
                                    Char ch = Actor.findChar(i);
                                    if (ch != null) {
                                        if (ch instanceof DwarfKing) {
                                            Statistics.qualifiedForBossChallengeBadge = false;
                                        }
                                        Buff.prolong(ch, Roots.class, 1f + hero.buff(Wrath.class).cooldown() / 4);
                                    }
                                }
                            }
                        }
                    }
                    //Mimic Tooth: Deal half of wrath bleeding to all adjacent enemies
                    if (ref instanceof MimicTooth)
                    {
                        ArrayList<Integer> validtiles = new ArrayList<>();
                        ArrayList<Mob> validmobs = new ArrayList<>();
                        for (int i : PathFinder.NEIGHBOURS9){
                            validtiles.add(hero.pos+i);
                        }

                        for (int validtile : validtiles) {
                            Char ch = Actor.findChar(validtile);
                            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                                Buff.affect(ch, Bleeding.class).set((int)hero.buff(Wrath.class).cooldown()/2);
                            }
                        }

                    }
                    //Parchment Scrap: Gives random scroll, stone or exotic scroll based on wrath
                    if (ref instanceof ParchmentScrap)
                    {
                        boolean givenitem = false;

                        int trueWrath = (int)hero.buff(Wrath.class).cooldown() - 10;

                        int StoneChance = 100 - trueWrath;
                        int ScrollChance = 20 + trueWrath;
                        int ExoticChance = 0;
                        if (trueWrath >= 50) ExoticChance += trueWrath;
                        if (trueWrath >= 100) ScrollChance = 0;

                        int r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        if (r < StoneChance && !givenitem)
                        {
                            Item st = Generator.random( Generator.Category.STONE );
                            Dungeon.level.drop(st, hero.pos).sprite.drop();
                            givenitem = true;
                        }
                        r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        Item sc = Generator.random( Generator.Category.SCROLL );
                        if (r < ScrollChance && !givenitem)
                        {
                            Dungeon.level.drop(sc, hero.pos).sprite.drop();
                            givenitem = true;
                        }
                        r = com.watabou.utils.Random.NormalIntRange(1, 100);
                        if (r < ExoticChance && !givenitem)
                        {

                            Item ex = Generator.random(ExoticScroll.regToExo.get(sc));
                            Dungeon.level.drop(ex, hero.pos).sprite.drop();
                            givenitem = true;
                        }

                        if (!givenitem)
                        {
                            Item st = Generator.random( Generator.Category.STONE );
                            Dungeon.level.drop(st, hero.pos).sprite.drop();
                            givenitem = true;
                        }

                    }
                    //Petrified Seed: Stuns adjacent enemies for a specified amount of time based on wrath
                    if (ref instanceof PetrifiedSeed) {

                        ArrayList<Integer> validtiles = new ArrayList<>();
                        ArrayList<Mob> validmobs = new ArrayList<>();
                        for (int i : PathFinder.NEIGHBOURS9){
                            validtiles.add(hero.pos+i);
                        }

                        for (int validtile : validtiles) {
                            Char ch = Actor.findChar(validtile);
                            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                                Buff.prolong(ch, Paralysis.class, (int)(hero.buff(Wrath.class).cooldown()/3) + 1);
                            }
                        }

                    }
                    //Rat Skull:
                    if (ref instanceof RatSkull) {

                        Buff.affect(hero, AdrenalineSurge.class).reset(3 + (int)(hero.buff(Wrath.class).cooldown()/10), hero.buff(Wrath.class).cooldown());

                    }
                    //Salt Cube: Poisons visible enemies, for every poisoned enemy you gain shielding, amount poisoned determined by wrath
                    if (ref instanceof SaltCube) {
                        ArrayList<Mob> affectable = new ArrayList<>();
                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }

                        com.watabou.utils.Random.shuffle(affectable);
                        int howmany = (int)hero.buff(Wrath.class).cooldown() - 10;
                        int shieldamnt = 0;

                        for (Mob mob: affectable ) {
                            if (howmany > 0)
                                Buff.affect(mob, Poison.class).set(8 + (howmany / 10));
                                shieldamnt += 5;
                        }

                        Buff.affect(hero, Barrier.class).setShield(shieldamnt);

                    }
                    //Thirteen Leaf Clover: Deal twice wrath damage to a random visible enemy, 1 in 13 chance to target all enemies instead
                    if (ref instanceof ThirteenLeafClover)
                    {
                        boolean luck = false;
                        if (com.watabou.utils.Random.Int(13) == 1) luck = true;
                        ArrayList<Mob> affectable = new ArrayList<>();

                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                if (luck) mob.damage(Math.round(5 + (int)hero.buff(Wrath.class).cooldown() * 2), this);
                                else affectable.add(mob);
                            }
                        }
                        if (affectable.size() > 0)
                        {
                            com.watabou.utils.Random.shuffle(affectable);
                            affectable.get(0).damage(Math.round(5 + (int)hero.buff(Wrath.class).cooldown() * 2), this);

                        }
                    }
                    //Blood Vial: Heal for half of wrath
                    if (ref instanceof VialOfBlood)
                    {
                        Buff.affect(hero, Healing.class).setHeal((int)hero.buff(Wrath.class).cooldown()/2, 0.05f, 0);
                    }
                    //Shard of Oblivion:
                    if (ref instanceof ShardOfOblivion) {

                        ArrayList<Mob> affectable = new ArrayList<>();
                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }

                        com.watabou.utils.Random.shuffle(affectable);
                        int howmany = (int)hero.buff(Wrath.class).cooldown() - 10;
                        howmany = howmany / 10;

                        for (Mob mob: affectable ) {
                            if (howmany > 0)
                            {
                                if (ScrollOfTeleportation.teleportChar(mob)){

                                        if (((Mob) mob).state == ((Mob) mob).HUNTING) ((Mob) mob).state = ((Mob) mob).WANDERING;
                                        ((Mob) mob).beckon(Dungeon.level.randomDestination( mob ));
                                    if (!Char.hasProp(mob, Char.Property.BOSS) && !Char.hasProp(mob, Char.Property.MINIBOSS)) {
                                        Buff.affect(mob, Paralysis.class, howmany *= 3);
                                    }

                                }

                            }
                        }

                    }
                    //Trap Mechanism: Throws traps on enemies based on wrath amount and instantly activates them
                    if (ref instanceof TrapMechanism) {

                        ArrayList<Mob> affectable = new ArrayList<>();
                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }

                        com.watabou.utils.Random.shuffle(affectable);
                        int howmany = (int)hero.buff(Wrath.class).cooldown() - 10;
                        howmany = howmany / 10;

                        for (Mob mob: affectable ) {
                            if (howmany > 0)
                                if (Dungeon.level.passable[mob.pos] && Dungeon.level.map[mob.pos] != Terrain.EXIT&& Dungeon.level.map[mob.pos] != Terrain.ENTRANCE) {
                                    Level.set(mob.pos, Terrain.SECRET_TRAP);
                                    Trap t = trapClasses[com.watabou.utils.Random.Int(trapClasses.length)];

                                    Dungeon.level.setTrap(t, mob.pos);
                                    Dungeon.level.discover(mob.pos);
                                    t.activate();
                                    t.disarm();

                                }
                        }

                    }
                    //Wondrous Resin: Throw an amount of cursed wand zaps based on wrath
                    if (ref instanceof WondrousResin)
                    {

                        ArrayList<Mob> affectable = new ArrayList<>();
                        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                            if (Dungeon.level.heroFOV[mob.pos]) {
                                affectable.add(mob);
                            }
                        }

                        com.watabou.utils.Random.shuffle(affectable);
                        int howmany = (int)hero.buff(Wrath.class).cooldown() - 10;
                        howmany = howmany / 10;
                        WandOfMagicMissile temp = new WandOfMagicMissile();

                        for (Mob mob: affectable ) {
                            if (howmany > 0)
                                //not sure if i need to do a callback here, test this later
                            CursedWand.cursedZap(temp,
                                    Dungeon.hero,
                                    new Ballistica(Dungeon.hero.pos, mob.pos, Ballistica.MAGIC_BOLT), new com.watabou.utils.Callback() {
                                        @Override
                                        public void call() {
                                            WondrousResin.forcePositive = true;
                                        }
                                    });


                        }

                    }

                    Sample.INSTANCE.play( Assets.Sounds.BLAST );
                    Buff.detach( hero, Wrath.class );

                }

                //visuals
                UpdateVisuals();
                }


                else GLog.w(Messages.get(this, "COOLDOWN"));


        }
    }

    private static String STALING = "STALING";
    private static String COOLDOWN = "COOLDOWN";

    private static String REALCHARGE = "REALCHARGE";
    @Override
    public void storeInBundle( Bundle bundle ) {

        bundle.put(COOLDOWN, bloodvialcooldown);
        bundle.put(STALING, staling);


        //this is here because of some stupid bug where it just does not update the charge cap automatically in the restoure function, so that also resets the charge to 1
        //so i have to manually save and apply it again later
        bundle.put(REALCHARGE, charge);

        super.storeInBundle(bundle);

    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        chargeCap = (1 + (level() / 3));
        charge = bundle.getInt( REALCHARGE );
        staling = bundle.getInt( STALING );
        bloodvialcooldown = bundle.getInt( COOLDOWN );

        Item.updateQuickslot();
        UpdateVisuals();
    }

    public class vialRecharge extends ArtifactBuff{

        @Override
        public boolean act() {
            Item.updateQuickslot();
            if (bloodvialcooldown > 0) {
                Item.updateQuickslot();

                bloodvialcooldown--;
            }
            if (exp >= 3+level()*7 && level() < levelCap) {
                exp-= 3+level()*7;
                upgrade();
                chargeCap = (1 + (level() / 3));
                Item.updateQuickslot();
            };
            spend( Actor.TICK );
            return true;
        }

    }


    public void UpdateVisuals()
    {

        if (charge == chargeCap)
        {
            image = ItemSpriteSheet.ARTIFACT_VIAL3;
            defaultAction = AC_DRINK;
        }
        else if (charge > 0) {
            image = ItemSpriteSheet.ARTIFACT_VIAL2;
            defaultAction = AC_DRINK;
        }
        else {
            image = ItemSpriteSheet.ARTIFACT_VIAL1;
            defaultAction = AC_STAB;
        }

    }


}
