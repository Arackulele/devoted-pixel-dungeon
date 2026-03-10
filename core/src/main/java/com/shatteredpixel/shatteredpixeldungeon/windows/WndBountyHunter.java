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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfOvergrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShielding;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BountyHunterSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class WndBountyHunter extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 180;

	private static final int GAP  = 2;

	public WndBountyHunter(BountyHunter troll, Hero hero ) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle titlebar = new IconTitle();
		titlebar.icon( troll.sprite() );
		titlebar.label( Messages.titleCase( troll.name() ) );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );

		RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "prompt", BountyHunter.Quest.favor), 6 );
		message.maxWidth( width );
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		ArrayList<RedButton> buttons = new ArrayList<>();

		int rockblockcost = BountyHunter.Quest.freePickaxe ? 0 : 250;
		RedButton rockblock = new RedButton(Messages.get(this, "pickaxe", rockblockcost), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBountyHunter.class, "pickaxe_verify") + (rockblockcost == 0 ? "\n\n" + Messages.get(WndBountyHunter.class, "pickaxe_free") : ""),
						Messages.get(WndBountyHunter.class, "pickaxe_yes"),
						Messages.get(WndBountyHunter.class, "pickaxe_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							if (BountyHunter.Quest.pickaxe.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", BountyHunter.Quest.pickaxe.name()) ));
							} else {
								Dungeon.level.drop( BountyHunter.Quest.pickaxe, Dungeon.hero.pos ).sprite.drop();
							}
							BountyHunter.Quest.favor -= rockblockcost;
							BountyHunter.Quest.pickaxe = null;
							WndBountyHunter.this.hide();

							if (!BountyHunter.Quest.rewardsAvailable()){
								Notes.remove( Notes.Landmark.BOUNTYHUNTER );
							}
						}
					}
				});
			}
		};
		rockblock.enable(BountyHunter.Quest.pickaxe != null && BountyHunter.Quest.favor >= rockblockcost);
		buttons.add(rockblock);

		int lootbagcost = 500 + 1000*BountyHunter.Quest.reforges;
		RedButton lootbag = new RedButton(Messages.get(this, "reforge", lootbagcost), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						new BountyHunterSprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBountyHunter.class, "smith_verify"),
						Messages.get(WndBountyHunter.class, "smith_yes"),
						Messages.get(WndBountyHunter.class, "smith_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							BountyHunter.Quest.favor -= 500 + 1000*BountyHunter.Quest.reforges;
							BountyHunter.Quest.reforges++;
							WndBountyHunter.this.hide();
							GameScene.show(new WndBountyHunter.WndLootBag(troll, hero));
						}
					}
				});
			}
		};
        lootbag.enable(BountyHunter.Quest.favor >= lootbagcost);
        buttons.add(lootbag);


        int hardenCost = 500 + 1000*BountyHunter.Quest.hardens;
		RedButton harden = new RedButton(Messages.get(this, "harden", hardenCost), 6){
			@Override
			protected void onClick() {
				GameScene.selectItem(new ReinforceSelector());
			}
		};
		harden.enable(BountyHunter.Quest.favor >= hardenCost);
		buttons.add(harden);

		int upgradeCost = 1000 + 1000*BountyHunter.Quest.upgrades;
		RedButton upgrade = new RedButton(Messages.get(this, "upgrade", upgradeCost), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						new BountyHunterSprite(),
						Messages.titleCase(Messages.get(WndBountyHunter.class, "inspiration")),
						Messages.get(PotionOfDivineInspiration.class, "select_tier"),
						Messages.titleCase(Messages.get(TalentsPane.class, "tier", 1)),
						Messages.titleCase(Messages.get(TalentsPane.class, "tier", 2)),
						Messages.titleCase(Messages.get(TalentsPane.class, "tier", 3)),
						Messages.titleCase(Messages.get(TalentsPane.class, "tier", 4))
				){
					@Override
					protected boolean enabled(int index) {
						return true;
					}

					@Override
					protected void onSelect(int index) {
						super.onSelect(index);

						if (index != -1){
                            Buff.affect(Dungeon.hero, PotionOfDivineInspiration.DivineInspirationTracker.class).setBoosted(index+1);

                            boolean unspentTalents = false;
							for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
								if (Dungeon.hero.talentPointsAvailable(i) > 0){
									unspentTalents = true;
									break;
								}
							}
							if (unspentTalents){
								StatusPane.talentBlink = 10f;
								WndHero.lastIdx = 1;
							}

							int upgradeCost = 1000 + 1000*BountyHunter.Quest.upgrades;
							BountyHunter.Quest.favor -= upgradeCost;
							BountyHunter.Quest.upgrades++;

                            WndBountyHunter.this.hide();

							GameScene.showlevelUpStars();

							Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
							Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.3f, 0.7f, 1.2f);
							Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
						}
					}

					@Override
					public void onBackPressed() {
						//do nothing
					}
				});

			}
		};
		upgrade.enable(BountyHunter.Quest.favor >= upgradeCost);
		buttons.add(upgrade);

		RedButton smith = new RedButton(Messages.get(this, "smith", 2000), 6){
			@Override
			protected void onClick() {
				GameScene.selectItem(new InfuseSelector());
			}
		};
		smith.enable(BountyHunter.Quest.favor >= 2000);
		buttons.add(smith);

		RedButton cashOut = new RedButton(Messages.get(this, "cashout"), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBountyHunter.class, "cashout_verify", (int)(BountyHunter.Quest.favor / 50)),
						Messages.get(WndBountyHunter.class, "cashout_yes"),
						Messages.get(WndBountyHunter.class, "cashout_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							new EnergyCrystal(BountyHunter.Quest.favor / 50).doPickUp(Dungeon.hero, Dungeon.hero.pos);
							BountyHunter.Quest.favor = 0;
							WndBountyHunter.this.hide();
						}
					}
				});
			}
		};
		cashOut.enable(BountyHunter.Quest.favor > 0);
		buttons.add(cashOut);

		float pos = message.bottom() + 3*GAP;
		for (RedButton b : buttons){
			b.leftJustify = true;
			b.multiline = true;
			b.setSize(width, b.reqHeight());
			b.setRect(0, pos, width, b.reqHeight());
			b.enable(b.active); //so that it's visually reflected
			add(b);
			pos = b.bottom() + GAP;
		}

		resize(width, (int)pos);

	}

	public static class WndLootBag extends Window {

		private static final int WIDTH      = 128;
		private static final int BTN_SIZE	= 28;
		private static final int BTN_GAP	= 4;
		private static final int GAP		= 2;

		public WndLootBag( BountyHunter troll, Hero hero ){
			super();

			IconTitle titlebar = new IconTitle();
			titlebar.icon(troll.sprite());
			titlebar.label(Messages.titleCase(troll.name()));

			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(WndBountyHunter.class, "lootprompt"), 6 );

			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			if (BountyHunter.Quest.smithRewards == null || BountyHunter.Quest.smithRewards.isEmpty()){
				BountyHunter.Quest.generateRewards(false);
			}

			int count = 0;
			for (Item i : BountyHunter.Quest.smithRewards){
				count++;
				ItemButton btnReward = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new RewardWindow(troll, hero, item()));
					}
				};
				btnReward.item( i );
				btnReward.setRect( count*(WIDTH - BTN_GAP) / BountyHunter.Quest.smithRewards.size() - BTN_SIZE,
						message.top() + message.height() + BTN_GAP,
						BTN_SIZE, BTN_SIZE );
				add( btnReward );

			}

			resize(WIDTH, (int)message.bottom() + 2*BTN_GAP + BTN_SIZE);

		}

		@Override
		public void onBackPressed() {
			//do nothing
		}

		private class RewardWindow extends WndInfoItem {

			public RewardWindow( BountyHunter troll, Hero hero, Item item ) {
				super(item);

				RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
					@Override
					protected void onClick() {
						WndBountyHunter.WndLootBag.RewardWindow.this.hide();

						if (item instanceof Weapon && BountyHunter.Quest.smithEnchant != null){
							((Weapon) item).enchant(BountyHunter.Quest.smithEnchant);
						} else if (item instanceof Armor && BountyHunter.Quest.smithGlyph != null){
							((Armor) item).inscribe(BountyHunter.Quest.smithGlyph);
						}

						item.identify(false);
						Sample.INSTANCE.play(Assets.Sounds.EVOKE);
						Item.evoke( Dungeon.hero );
						if (item.doPickUp( Dungeon.hero )) {
							GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );
						} else {
							Dungeon.level.drop( item, Dungeon.hero.pos ).sprite.drop();
						}
						WndLootBag.this.hide();
						BountyHunter.Quest.smithRewards = null;

						if (!BountyHunter.Quest.rewardsAvailable()){
							Notes.remove( Notes.Landmark.BOUNTYHUNTER );
						}
					}
				};
				btnConfirm.setRect(0, height+2, width/2-1, 16);
				add(btnConfirm);

				RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
					@Override
					protected void onClick() {
						WndBountyHunter.WndLootBag.RewardWindow.this.hide();
					}
				};
				btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
				add(btnCancel);

				resize(width, (int)btnCancel.bottom());
			}
		}

	}
	private class ReinforceSelector extends WndBag.ItemSelector {

		@Override
		public String textPrompt() {
			return Messages.get(WndBountyHunter.class, "reinforceprompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isUpgradable()
					&& item.isIdentified() && !item.cursed
					&& !(item instanceof Artifact)
					&& item.level() > 0;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {

				Dungeon.hero.bountyhunterbuff += 1;
				Dungeon.hero.attackSkill += item.level();
				Dungeon.hero.defenseSkill += item.level();
                Dungeon.hero.updateHT(false);

				BountyHunter.Quest.favor -= 500 + 1000*BountyHunter.Quest.hardens;
				BountyHunter.Quest.hardens++;

				WndBountyHunter.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				item.degrade(item.level());
				item.identify();

				if (!BountyHunter.Quest.rewardsAvailable()){
					Notes.remove( Notes.Landmark.BOUNTYHUNTER );
				}
			}
		}
	}

	private class InfuseSelector extends WndBag.ItemSelector {

		@Override
		public String textPrompt() {
			return Messages.get(WndBountyHunter.class, "infuseprompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
            //There is just soooo many exceptions to this, maybe they should instead be limited by something else or be weaker if infused
			return !item.isUpgradable()
					&& item.isIdentified() && !item.cursed
					&& (
					(item instanceof Potion &&
					!(item instanceof PotionOfHealing) && !(item instanceof PotionOfShielding) && !(item instanceof ElixirOfAquaticRejuvenation) && !(item instanceof ElixirOfHoneyedHealing) && !(item instanceof ElixirOfOvergrowth) &&
					!(item instanceof PotionOfStrength) && !(item instanceof PotionOfMastery) && !(item instanceof ElixirOfMight) &&
                    !(item instanceof PotionOfExperience) && !(item instanceof PotionOfDivineInspiration))
					|| (item instanceof Scroll && !(item instanceof ScrollOfUpgrade))
					|| (item instanceof Plant.Seed && !(item instanceof Sungrass.Seed))
					|| item instanceof Runestone
					|| (item instanceof Bomb && !(item instanceof RegrowthBomb))
					|| (item instanceof Scroll && !(item instanceof ScrollOfUpgrade))
					|| (item instanceof Spell && !(item instanceof MagicalInfusion))
					|| item instanceof Stylus
					|| item instanceof Torch
					|| item instanceof RemainsItem
					);

		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				Item newitem;

				if (item.quantity() > 1)newitem = item.split(1);
				else newitem = item;

				newitem.isInfused = true;
				newitem.stackable = false;
				Dungeon.level.drop( newitem, Dungeon.hero.pos ).sprite.drop();
				BountyHunter.Quest.favor -= 2000;
				BountyHunter.Quest.smiths++;

                Badges.validateRemainsInfuse(newitem);

				WndBountyHunter.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);;

				if (!BountyHunter.Quest.rewardsAvailable()){
					Notes.remove( Notes.Landmark.BOUNTYHUNTER );
				}
			}
		}
	}

}
