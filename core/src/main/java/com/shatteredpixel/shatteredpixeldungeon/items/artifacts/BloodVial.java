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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ArcaneRoot;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.CrystalApple;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.LargeRadish;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BowFragment;
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
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
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
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.awt.Choice;
import java.lang.reflect.Array;
import java.util.Arrays;
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
    public int bloodvialcooldown;
    public int DevoteeCharges;

    @Override
    protected ArtifactBuff passiveBuff() {
        return new vialRecharge();
    }


    @Override
    public String info() {
        String info = super.info();

        Item ref = null;
        if (hero != null) {
            ref = hero.lastEaten;

        if (hero.subClass == HeroSubClass.DEVOTEE){
            if (ref == null || DevoteeCharges == 0) info += "\n\n" + Messages.get(this, "no_food");
            else {
                info += "\n\n" + Messages.get(this, "descability")  + "\n";
                info += Messages.get(ref, "devotee");
                info += "\n" + Messages.get(this, "devoteedesc", DevoteeCharges);

            }
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

    public void charge()
    {
        charge++;
    }

    @SuppressWarnings("SuspiciousIndentation")
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

                    if (!visible) staling++;
                    else staling = 0;

                    int variableamt = 4 + (staling * staling);

                    if (!anymobs) variableamt += 100;

                    bloodvialcooldown = (int) (variableamt + 0.6 + level());

                    if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) != 0) {
                        if (hero.pointsInTalent(Talent.INSTANT_GRATIFICATION) == 2)  Buff.affect(hero, Haste.class, 4);
                         else Buff.affect(hero, Haste.class, 2);
                    }

                }

            hero.spend(1f);
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
                            int reduction = 2 + Dungeon.hero.pointsInTalent((Talent.NIHILISM));
                            reduction *= (int) 0.1f;
                            damage -= damage * reduction;
                        }

                    if (hero.pointsInTalent(Talent.NIHILISM) == 3) hero.spend(0.33f);
                    else hero.spend(2f);
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
                DevoteeMechanics();

                //visuals
                UpdateVisuals();
                }


                else GLog.w(Messages.get(this, "COOLDOWN"));


        }
    }

    //this logic should probably just be in the individual food classes similar to duelist abilities, but this is barely manageable now
    private void DevoteeMechanics()
    {
        if (hero.subClass == HeroSubClass.DEVOTEE && DevoteeCharges > 0) {
            if (hero.lastEaten instanceof HornOfPlenty) {


                Class<?>[] foodclasses = Catalog.FOOD.items().toArray(new Class[0]);
                Random.shuffle(foodclasses);
                Food Choice1 = (Food) Reflection.newInstance(foodclasses[0]);
                Food Choice2 = (Food) Reflection.newInstance(foodclasses[1]);


                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show(
                                new WndOptions(new ItemSprite(ItemSpriteSheet.ARTIFACT_VIAL3),
                                        Messages.get(BloodVial.class, "horn_title"),
                                        Messages.get(BloodVial.class, "horn_desc"),
                                        Choice1.name(),
                                        Choice2.name()) {

                                    private float elapsed = 0f;

                                    @Override
                                    public synchronized void update() {
                                        super.update();
                                        elapsed += Game.elapsed;
                                    }

                                    @Override
                                    public void hide() {
                                        if (elapsed > 0.5f) {
                                            super.hide();
                                        }
                                    }

                                    @Override
                                    protected void onSelect(int index) {
                                        if (elapsed > 0.2f) {
                                            if (index == 0) hero.lastEaten = Choice1;
                                            if (index == 1) hero.lastEaten = Choice2;
                                            DevoteeCharges++;
                                            DevoteeMechanics();
                                        }
                                    }
                                }
                        );
                    }
                });
            }

            if (hero.lastEaten instanceof Pasty || hero.lastEaten instanceof MeatPie)
            {
                int amnt = hero.HT/6;
                if (GourmandCheck()) amnt = (int)(amnt * 1.5f);
                Buff.affect(hero, Healing.class).setHeal(amnt, 0, 1);
            }
            if (hero.lastEaten instanceof SmallRation || hero.lastEaten instanceof LargeRadish)
            {
                Class type = hero.lastEaten instanceof SmallRation ? Shrink.class :  Grow.class;
                int amnt = 20;
                if (GourmandCheck()) amnt = (int)(amnt * 1.5f);
                Buff.affect(hero, type, amnt);
            }
            else if (hero.lastEaten instanceof ChargrilledMeat || hero.lastEaten instanceof FrozenCarpaccio ||
                    hero.lastEaten instanceof MysteryMeat || hero.lastEaten instanceof PhantomMeat)
            {
                int severity = 3;
                if (GourmandCheck()) severity++;
                for (Char m : GetNearVis(severity)) {
                    if (hero.lastEaten instanceof ChargrilledMeat) Buff.affect( m, Burning.class ).reignite( m );
                    if (hero.lastEaten instanceof FrozenCarpaccio) Freezing.freeze( m.pos );
                    if (hero.lastEaten instanceof MysteryMeat) MysteryMeat.effect(m, false);
                    if (hero.lastEaten instanceof PhantomMeat) ScrollOfTeleportation.teleportChar(m);
                }
            }
            else if (hero.lastEaten instanceof StewedMeat)
            {
                if (GourmandCheck()) Buff.affect(hero, AdrenalineSurge.class).reset(3, 30);
                else Buff.affect(hero, AdrenalineSurge.class).reset(4, 40);
            }
            else if (hero.lastEaten instanceof ArcaneRoot)
            {
                if (GourmandCheck()) Buff.affect(hero, ArcaneRoot.MagicArena.class).setup(hero.pos,30);
                else Buff.affect(hero, ArcaneRoot.MagicArena.class).setup(hero.pos, 20);
            }
            else if (hero.lastEaten instanceof CrystalApple)
            {
                if (GourmandCheck()) hero.Heal(5);
                //This just adds 1 max hp
                Dungeon.hero.bountyhunterbuff += 0.334f;
                Dungeon.hero.updateHT(false);
            }
            else if (hero.lastEaten instanceof Berry)
            {
                if (GourmandCheck()) BowFragment.PlantEffect(hero, 5, true);
                else BowFragment.PlantEffect(hero, 8, true);

            }
            //This HAS to be at the end, since a regular ration is just defined as "Food", but every
            //other piece of food is also an instanceof food
            else if ( hero.lastEaten instanceof MeatPie || hero.lastEaten instanceof SupplyRation || hero.lastEaten instanceof Food )
            {
                ArrayList<Mob> targets = new ArrayList<>();
                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    if (Dungeon.level.heroFOV[mob.pos]) {
                        targets.add(mob);
                    }
                }
                for (Mob mob : targets)
                {
                    int dmg = com.watabou.utils.Random.NormalIntRange(6 + (Dungeon.scalingDepth() /2) - targets.size(), 12 + Dungeon.scalingDepth() - targets.size());
                    if (GourmandCheck()) dmg += 8;
                    if (hero.lastEaten instanceof SupplyRation) dmg *= 0.7f;
                    dmg -= mob.drRoll();

                    mob.damage(dmg, this);
                    if (hero.lastEaten instanceof SupplyRation) Buff.affect(hero, Invisibility.class, targets.size());
                }
            }

            DevoteeCharges--;
            Sample.INSTANCE.play( Assets.Sounds.BLAST );
        }

    }

    private boolean GourmandCheck() {
        return (hero.pointsInTalent(Talent.GOURMAND) == 3 && DevoteeCharges == 3);
    }

    private ArrayList<Char> GetNearVis(int amnt)
    {
        ArrayList<Char> select = new ArrayList<>();
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos]) {
            select.add(mob);
            }
        }
        return Dungeon.level.sortByDist(select, hero);
    }


    private static String STALING = "STALING";
    private static String COOLDOWN = "COOLDOWN";
    private static String REALCHARGE = "REALCHARGE";
    private static String DEVOTEECHARGES = "DEVOTEECHARGES";

    @Override
    public void storeInBundle( Bundle bundle ) {

        bundle.put(COOLDOWN, bloodvialcooldown);
        bundle.put(STALING, staling);
        bundle.put(DEVOTEECHARGES, DevoteeCharges);

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
        DevoteeCharges = bundle.getInt( DEVOTEECHARGES );

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
