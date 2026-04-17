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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.utils.DeviceCompat;

import java.util.ArrayList;

public class v1_X_DevotedChanges {

	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		add_Coming_Soon(changeInfos);
        add_v1_0_0_Beta_8_Changes(changeInfos);
        add_v1_0_0_Beta_7_Changes(changeInfos);
        add_v1_0_0_Beta_Changes(changeInfos);
        add_v1_0_0_Changes(changeInfos);
	}

	public static void add_Coming_Soon( ArrayList<ChangeInfo> changeInfos ) {

		ChangeInfo changes = new ChangeInfo("Coming Soon", true, "");
		changes.hardlight(0xCCCCCC);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Overview and Release date",
				"The 1.0.0 Version of Devoted PD will most likely release sometime in 2026. Outside of some stuff i will add until then, there will probably be some more betas to squash out bugs and make changes to the new content.\n" +
				"\n" +
				"The current beta has not been extensively playtested and balanced, so any feedback is appreciated! Some things might feel too powerful or unfair, but they will be fixed before the full release\n" ));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GARDENS_PAGE), "Lore Pages for the new Regions",
				"These will be added for the **1.0.0 release**, but have not been included to be collected in this beta, since most of them are not finished yet.\n" +
				"\n" +
				"The lore pages will feature some new characters and information about the world, and might also feature an explanation on why the dungeon is so varied now..."));

		changes.addButton( new ChangeButton(Icons.get(Icons.SEED), "Missing artwork",
				"The splash art for the Zealot will be included in the **1.0.0 release**, so you will finally not be seeing the placeholder again. As with some of the new sprites, i have comissioned a great artist to draw the splash art so it will look cool as hell probably.\n" +
						"\n" +
						"I am still undecided on what i want to do with the region splash arts, but they will definetly not stay this way for the new regions in 1.0.0, as showing the wrong regions art and lore when entering the alt-regions just feels wrong. Perhaps these will have placeholders for now, but the goal is to get every new region unique splash art eventually."));

		changes.addButton( new ChangeButton(Icons.get(Icons.STAIRS), "The Main Menu and Artbook",
				"The main menu still features a lot of unchanged things from shattered, such as the background arches and the un-updated credits screen. For the **1.0.0 release**, these will be changed and cleared up so it is more clear that this is not shattered pixel dungeon.\n" +
						"\n" +
						"An artbook will also be added to the main menu, where you can take a look at some concept art, sketches, play the games music at will perhaps and look at things that were removed or didnt make it into the final game. This is just for fun but i am planning this to be in version 1.0.0"));

		changes.addButton( new ChangeButton(Icons.get(Icons.CHANGES), "Future content ( after 1.0.0 )",
				"One thing i am planning to add in the future is a new way to implement legendary items. These will most likely come as a kind of new quest, and not be limited to the class you are playing. The details arent't quite decided on yet, but they will be added back in one way or another." +
						"\n" +
						"Something else that i would like to add is a reason to play the base game regions in devoted. Currently most players just try to turn on all of the alternative regions automatically to get all the new content and leave the base game regions alone, which is ok but one of the reasons i added new regions in the first place was variety, so every run wouldnt feel the same anymore. Perhaps every region will get some unique items ( so there is new stuff to be found even in the old regions ) or something else to make every region worth exploring again." +
						"\n" +
						"Once shattered PD receives its new Imp Quest, Devoted will most likely follow suit and get a more elaborate quest for the dwarven citadel as well. But i want to wait on that until shattereds rework is actually done so most likely this will also not be in 1.0.0" +
						"\n" +
						"Finally, i would like to look into a way to add a sort of 'metaprogression' system to devoted. Not in the way that you can get permanent upgrades through runs, but perhaps gaining a meta-currency that lets you unlock new items that you can then find randomly in any run would be interesting. This might tie into legendary items in some way."

		));



	}

    public static void add_v1_0_0_Beta_8_Changes( ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("Beta 8", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Shattered Version Update",
                "Devoted PD is now based on version 3.3.7 of Shattered Pixel Dungeon\n" +
                        "\n" +
                        "All Items and features in this version of shattered are now also featured in devoted, including randomization features, the new artifact and the WIP city quest ( which will have a citadel variant once it is finished )"

        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "New Challenge!",
                "A 10th Challenge has been added to Devoted: Cursed Habitat\n" +
                        "\n" +
                        "Every Region has a unique special feature when playing with the new challenge enabled, and you will not be able to select your regions\n" ));

        changes.addButton( new ChangeButton(Icons.get(Icons.MAGNIFY), "Visuals",
                "There is a new WIP title screen for devoted that replaces the shattered title screen, made by yours truly\n" +
                        "\n" +
                        "-The foreground decorations are currently just recolors of those from shattered, this will most likely be changed in the future\n"));

        changes.addButton( new ChangeButton(new Image(new DruidSprite()), "Artbook",
                "A WIP version of the Artbook has been added to the title screen:\n" +
                        "\n" +
                        "-This currently only has some concept-art, and exists mostly as a proof of concept, more art will be added tot his before release\n" +
                        "\n" +
                        "-The credits screen has been updated as well\n"));

        changes.addButton( new ChangeButton(Icons.get(Icons.AUDIO), "New music",
                "Two boss fight tracks have been updated in this update\n" +
                        "\n" +
                        "The third and fourth alt-region boss soundtracks have been updated, once more these tracks have been composed dby Cevin-2006!\n"
        ));


        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "-A crash in the final boss fights final phase\n" +
                        "-Enemies dyding instantly upon entering the forge quests sub-floor\n" +
                        "-Other bug fixes, mostly because of the version update to shattered 3.3.7\n"
        ));


    }

    public static void add_v1_0_0_Beta_7_Changes( ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("Beta 7", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(BadgeBanner.image( Badges.Badge.HIGH_SCORE_2.image ), "New Badges and achievements",
                        "**New Badges**\n" +
                                "\n" +
                                "Devoted now features new badges, some of which exist for the feature complete-ness with the regular game ( like boss defeat badges ), but some are completely new and just for fun" +
                                "\n" +
                                "-Every alt-region boss now has a badge for defeating them, including an alternative victory badge for defeating the boss at the end of the void\n",
                                "-Similarly, every alt-region boss now also has a boss challenge badge, granted after defeating them in unique ways\n",
                                "-There is two new badges related to region selection, one for winning a run with only regular regions and one for alternative regions only\n",
                                "-A badge has been added that requires infusing a rather unique item\n"

        ));

        changes.addButton( new ChangeButton(new Image(new EmperorSprite()), "Gnoll Emperor Changes",
                "The Gnoll Emperor fight has received a bit of a rework:\n" +
                        "\n" +
                        "In his boss arena, three crystals now spawn in an inactive state, when the emperor goes in its second phase, these crystals will activate to heal the emperor periodically. While active, these crystals can be defeated. Additionally, the gnoll emperor now no longer leeches life with his main attacks in phase 2.\n" ));

        changes.addButton( new ChangeButton(new Image(new RatBeastSprite()), "Boss changes",
                "A few changes have been made that affect the existing boss fights:\n" +
                        "\n" +
                        "-The rat beast has gained increased accuracy, letting it activate its special attacks more frequently by building up combo meter\n" +
                        "\n" +
                        "-You can now feed the rat beast by throwing food at it. Yay!\n" +
                        "\n" +
                        "-This is technically a bug fix but also relevant to the rat beast fight: The Rat beast now once again inflicts oozing with its barf ( somehow no one reported this to me lol )\n" +
                        "\n" +
                        "-The furnace golem now uses a green indicator to show where you have to go during its blast attack, instead of a red one\n" +
                        "\n" +
                        "-The furnace golem now counts as flying, which makes more visual sense than counting as a grounded enemy\n" +
                        "\n" +
                        "-The furnace golem and other enemies immune to the burning effect no longer avoid touching magma tiles, and will walk across them\n" +
                        "\n" +
                        "-Wendar can no longer summon wardens without the badder bosses challenge enabled\n" +
                        "\n" +
                        "-The final boss no longer has missing or work in progress text, and now has visuals and a sound effect when summoning black holes\n"


        ));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "Badder Bosses changes",
                "The Badder Bosses challenge now displays the changes affecting each boss and every alt-region boss has a badder bosses version:\n" +
                        "\n" +
                        "-The gnoll emperors crystals heal much more agressively when badder bosses is turned on\n" +
                        "\n" +
                        "-The furnace golems badder bosses variant lets it summon real fire elementals as well as performing its special attacks more often\n" +
                        "\n" +
                        "-The dwarven court badder bosses variant buffs each member of the court in a unique way, allowing them to combo better with each other and summon more types of enemies\n" +
                        "\n" +
                        "-The final bosses variant spawns more minions, attacks more and may cause explosions in a variety of colors\n"));

        changes.addButton( new ChangeButton(Icons.get(Icons.REGION_ACTIVE), "Region changes",
                "Some minor changes have been made to certain enemies and visuals in the alt-regions:\n" +
                        "\n" +
                        "-The anvils in the forge have been made more visible, and the region now has a unique statue\n" +
                        "\n" +
                        "-Cult ritualists now need line of sight with their target to activate a challenge arena\n" +
                        "\n" +
                        "-All mobs now have a proper description, previously, a description and name was missing from certain exotic mobs\n" +
                        "\n" +
                        "-Banshees attack animation is significantly faster than it was before\n"));


        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "-Moles should no longer exist on the same tile as other mobs when not burying\n" +
                        "-Certain boss levels were showing the wrong grass sprites before, now they should be accurate\n" +
                        "-The final boss's unique barrier effect now properly uses a different color than regular shielding\n" +
                        "-Other minor bug fixes\n"
        ));


    }

    public static void add_v1_0_0_Beta_Changes( ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes1 = new ChangeInfo("Beta 5-6", true, "");
        changes1.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes1);


        changes1.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "-The cleansing meal talent now actually shortens eating time\n" +
                        "-Talking to the sage corpse without the respective quest item no longer crashes the game\n" +
                        "-Fixed an issue where Strength-affecting modifiers on weapons would not save in a variety of situations or be applied when they shouldnt\n" +
                        "-Dwarven court no longer turns invincible forever after reaching a certain hp threshold\n" +
                        "-Nihilisms Tier 3 now actually reduces the vials stabbing speed\n" +
                        "-The shadow realm should now always properly release players\n" +
                        "-Incorrect debuff icons were showing in a variety of situations, this has now been fixed\n" +
                        "-Abyss Vessels can no longer target things that arent allied with them ( like the players sad ghost )\n" +
                        "-The rat beast no longer respawns after you have already beaten it\n" +
                        "-Flaming moles no longer crash the game when popping up\n" +
                        "\n" +
                        "**Beta 6 fixes**\n" +
                        "-Exotic mobs should no longer be 30 times as common as before\n" +
                        "-The Coldhouse quest can once again be properly completed\n"
        ));

        changes1.addButton( new ChangeButton(new Image(new ImpSprite()), "Coldhouse Quest changes",
                "The Imps Quest in the Citadel can now be beat by defeating Trappers/Wardens respectively, it is no longer impossible to complete."));

        changes1.addButton( new ChangeButton(new Image(new BountyHunterSprite()), "Item Infusion Changes",
                "Item infusions have been updated, making sure items are no longer duplicated. This comes with a visual bug that causes the infusion item cooldown to show up in some incorrect situations, altough the mechanic works as intended otherwise. This visual bug will be fixed in a future beta."
        ));


        changes1.addButton( new ChangeButton(new Image(new RatBeastSprite()), "Badder Bosses",
                "The first two badder bosses variants have been added:\n" +
                        "\n" +
                        "Outside of just being more agressive and using its abilities more often, the gnoll emperor has a variation of its staff strike that is more difficult to dodge and requires the player to be in an open space to properly avoid.\n" +
                        "\n" +
                        "The Rat beasts arena replaces its chilling traps with frost traps, its barf reaches slightly further, and it inflicts broken more rapidly.\n"
        ));


    }



        public static void add_v1_0_0_Changes( ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes1 = new ChangeInfo("Beta 2 - 4", true, "");
        changes1.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes1);

        changes1.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "-Fixed a crash when attempting to zap a magic deflecting troll brawler with a piercing wand infused mages staff\n" +
                        "-Missing text should no longer appear on the mobile settings screen\n" +
                        "-Fixed being able to dodge a Bedrock Automatons charge attack when spam clicking a movement button\n" +
                        "-Resolved a softlock using an ankh against the final boss\n" +
                        "-Lava lake levels should now be less likely to softlock, if you get stuck on a lava tile, reloading the game will spawn you on a safe platform\n" +
                        "-Sages Corpse Artifact Reward should now always spawn properly\n" +
                        "-Sages corpse quest now properly removes the quest items upon completion\n" +
                        "-Resolved various other minor bugs\n" +
                        "\n" +
                        "**Beta 3 fixes**\n" +
                        "-Fixed a bug that caused softlocks while using levitation and flying effects in a lava lake level\n" +
                        "-Fixed a bug that caused the endothermic ring to be able to delete any type of tile, not just water\n" +
                        "-Resolved an issue causing items to duplicate in the shadow realm\n" +
                        "-Resolved the shadow realm soundtrack not playing in game\n" +
                        "-XP Orbs in the shadow realm should no longer spawn outside the level borders\n" +
                        "-Resolved a crash with the dwarven councils combo attacks\n" +
                        "\n" +
                        "**Beta 4 fixes**\n" +
                        "-Fixed a softlock caused by wearing brimstone armor in a lava lake level\n" +
                        "-Fixed an issue that allowed the player to infinitely sell the cold coprses cursed artifact, generating infinite money\n" +
                        "\n" +
                        "**Beta 2 fixes**\n" +
                        "-Fixed a bug that caused softlocks while using levitation and flying effects in a lava lake level\n" +
                        "-Removed the ability to get the cold coprses quest items multiple times, which allowed the player to generate infinite money\n" +
                        "-The Giant no longer clears permanent or unusual buffs, such as removing your infused item buff or your warrior seal cooldown\n" +
                        "-The rat beast no longer respawns upon restarting the game and entering its arena\n" +
                        "-The upgraded stats from bounty hunters 'reinforce self' option no longer disappear in some circumstances ( the health buff has also been buffed )\n" +
                        "-Fixed a bug that caused softlocks while using levitation and flying effects in a lava lake level\n" +
                        "-Barf and Pollen no longer block projectiles\n" +
                        "-Ascension should once again work properly for mobs in the alternate regions\n" +
                        "-Various smaller fixes\n"
        ));

        changes1.addButton( new ChangeButton(new Image(new SageCorpseSprite()), "Coldhouse Quest changes",
                "**Two of the quests of the sage corpse have been made slightly more difficult, to provide a more consistent experience**\n" +
                        "\n" +
                        "The endothermic ring now clears a larger area of water at once, stepping on ember while it is equipped now heats it up instantly\n" +
                        "\n" +
                        "The cloak of thorns now spawns thornlashers if you are out of combat, preventing situations where you can easily farm the quest outside of fights.\n" +
                        "\n"));

        changes1.addButton( new ChangeButton(new Image(new RatSprite()), "Enemy drop changes",
                "Certain enemy drops have been adjusted, since they were unchanged from vanilla enemies before hand, coin distribution should be more consistent now and enemies drop more thematically appropriate gear."));

        changes1.addButton( new ChangeButton(new Image(new WendarSprite()), "Dwarven council changes",
                "When entering their final phase, the dwarven council now gains temporary invincibility, this might change depending on how it is received\n" +
                        "\n" +
                        "In exchange, the councils final attack no longer instantly kills you if you stand close to it, it now never deals more than 100 damage\n"));

        changes1.addButton( new ChangeButton(new Image(new RatBeastSprite()), "Rat beast changes",
                "The Rat beast is getting a long list of ( mostly ) nerfs:\n" +
                        "\n" +
                        "The Barf attack now requires the rat beast to spend a turn charging it up, stopping any actions and showing a particle effect when it is about to use the attack\n" +
                        "Rat Beasts barf now doesnt spread on the water tiles that it removes, reaches less far than before and deals reduced damage\n" +
                        "Rat Beast takes more and deals less damage\n" +
                        "Rat Beast gains less shielding and power in its final phase\n"


        ));


        ChangeInfo changes = new ChangeInfo("v1.0 Beta", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Developer Commentary",
				"_-_ Released December 20th, 2025\n" +
				"_-_ 1,105 days after Devoted v0.4.4\n" +
				"Dev commentary will be added here in the future."));

		changes.addButton( new ChangeButton(Icons.get(Icons.AUDIO), "New music and sound effects",
				"**There are a total of 10 new music tracks featured in this update.**\n" +
						"\n" +
						"Each region and boss fight now has a unique soundtrack, all composed by my friend Cevin-2006!\n" +
						"\n" +
						"Tons of interactions and actaions now have unique sound effects, such as wolves howling :)"
				));

		changes.addButton( new ChangeButton(new Image(new ThymorSprite()), "New boss encounters",
				"**Every boss and boss floor has been completely remade.**\n" +
						"\n" +
						"Some more details are mentioned in the region sections of this changelog, but these are best to see yourselves in game!\n"
		));

		changes.addButton( new ChangeButton(new Image(new HornedToadSprite()), "New Gardens Quest:",
				"**The Gardens Quest is still given by the ghost and follows the same formula, but:**\n" +
						"\n" +
						"There is three new mini bosses that can be fought instead of the regular ones in the gardens quest ( the horned toad, emperor butterfly and elder thornlasher ).\n" +
						"\n" +
						"The floors these are encountered is set in the same way as the sewers bosses.\n" +
						"\n"));

		changes.addButton( new ChangeButton(new Image(new SageCorpseSprite()), "New Coldhouse Quest:",
				"**The Coldhous features a new quest-giver: The Sage Corpse**\n" +
						"\n" +
						"In order to save him from his curse, you must cleanse one of three special 'cursed' items ( The wand of malaise, The endothermic ring, the cloak of thorns ) .\n" +
						"\n" +
						"The quest can reward you with rings, wands or even artifacts depending on the type of item you had to cleanse.\n" +
						"\n"));

		changes.addButton( new ChangeButton(new Image(new BountyHunterSprite()), "New Troll Forge Quest:",
				"**The bounty hunter npc will task you to enter a unique sub-floor region: The sunken city.**\n" +
						"\n" +
						"In the sunken city, lava prevents you from going to most of the level, you must place the path through the level yourself!\n" +
						"\n" +
						"This quest has two variants, that each feature 2 unique enemies with new mechanics and one miniboss each.\n" +
						"\n" +
						"The rewards are completely different from the caves quest too, allowing you to buff your own character in various ways and even offering a way to make consumables unbreakable.\n" +
						"\n"));


		changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.ZEALOT, 4), "Zealot Subclasses",
				"**The Zealots subclasses have been completely overhauled!**\n" +
				"\n" +
				"**The Conjurer** (formerly archangel) now uses alchemical energy in a new menu to summon a wide variety of minions while invincible.\n" +
				"\n" +
				"**The Devotee** still unleashes their power upon stabbing themselves, however, this power is now based on their trinket."));

		changes.addButton( new ChangeButton(HeroSprite.avatar(HeroClass.ZEALOT, 6), "Zealot Armor Abilities",
				"**The Zealot also has a new set of armor abilities, different from before!**\n" +
				"\n" +
				"**Calling Beyond** allows the zealot to mark a giant area, that explodes after some time with high damage.\n" +
				"\n" +
				"**Eternal balance** lets the zealot use one of two different abilities based on their invincibility state, letting them extend their invincibility or move around enemies and themselves.\n" +
				"\n" +
				"**Weal and Woe** still grants increased or decreased ability based on chance, but its buffs work differently and are more extreme.\n"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SPLINTER), "Gardens changes",
				"**The Gardens region has received some major changes:**\n" +
						"\n" +
						"The gnoll druid has an entirely new sprite, butterflies have received some update to theirs.\n" +
						"\n" +
						"Enemy stats and drops have been tweaked, especially gnoll druid ( which can now be countered in more unique ways ).\n" +
						"\n" +
						"Flower pots now spawn in the gardens, which can be bombed to drop plants and flowers.\n" +
						"\n" +
						"The Gnoll Emperor fight has been totally overhauled, and he now has a unique ranged attack pattern and teleport.\n"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.MEAL), "Coldhouse changes",
				"**The Coldhouse region has received some major changes:**\n" +
						"\n" +
						"The coldhouse has received an entirely new sprite sheet, and meltable ice block decorations now spawn here.\n" +
						"\n" +
						"Meatrack, cold corpse and glass knight sprites have been completely overhauled. ALL of the other coldhouse enemies have been removed and replaced.\n" +
						"\n" +
						"Glass knights have been completely changed. Instead of reviving repeatedly, they now do a one time spin attack, spreading frost in a large area.\n" +
						"\n" +
						"Meat-racks can now steal any type of food, carry multiple different types of food, heal when they steal food and are more dangerous when you dont have food. They drop any food on death. The cold corpse now spreads gas more rapidly.\n" +
						"\n" +
						"The Were-Rat is a new enemy that can dash when healthy, closing the gaps on players using ranged attacks. The banshee is a new enemy that can petrify their enemies, making them skip a turn.\n" +
						"\n" +
						"The Rat beast fight has been completely overhauled. It can now perform attack combos, barf at the player, permanently debuff you, trigger traps, and becomes desperate in its final phase.\n"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ESSENCE), "Troll Forge changes",
				"**The Troll Forge region has received some major changes:**\n" +
						"\n" +
						"The Troll Forge has an entirely new sprite sheet, and new unique statues spawn in the forge.\n" +
						"\n" +
						"Granite Troll, troll ranger and troll brawler have received entirely new sprites, while the rock golem sprite has been touched up. Coal elementals no longer appear in the regular Troll Forge\n" +
						"\n" +
						"Granite Trolls now trigger a rockfall on first attacking the player, this deals damage in a cone shape and spreads a new permanent hazard, the magma tile. Troll rangers now flee at low health. The troll brawler now has three variants, deflecting either melee, ranged or magic attacks.\n" +
						"\n" +
						"The subterranean-mole is a new enemy with the ability to dig underground, evading any attacks and sneaking up on the player.\n" +
						"\n" +
						"The furnace golem bossfight has been entirely reworked, gaining new attacks ( which can explode the entire arena ), adding much more depth to the fight and gaining more distinct and fun phases. He also now has a menacing new sprite.\n"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ROYAL_SEAL), "Citadel changes",
				"**The Citadel region has received some major changes:**\n" +
						"\n" +
						"The citadel has received an entirely new sprite sheet including ritual pools of blood. Spooky.\n" +
						"\n" +
						"The Trapper and ritualist have received some sprite updated, while the alchemist and giants have been replaced by new enemies.\n" +
						"\n" +
						"Cult ritualist mechanics have been balanced and Dwarven Cultists now heal when moving whbile invincible.\n" +
						"\n" +
						"The warden is a new enemy that uses ward turrets against you and is quite agile. The judge is an enemy inspired by the old giants, it can clear your positive status effects and cleanse itself, but it is fair, not attacking until you commit a crime.\n" +
						"\n" +
						"The dwarven court bossfight has been entirely reworked, the court can now help eachother perform combo attacks, and can even create a black hole.\n"));

		changes.addButton( new ChangeButton(new Image(new BrainSprite()), "New region!",
				"**The Ashen Void is finally hear, after many years of wait:**\n" +
						"\n" +
						"It comes with many unique enemies and mechanics, unique to the other regions.\n" +
						"\n" +
						"Just like everything else, it has its own soundtrack and new spritesheet.\n" +
						"\n" +
						"Devoted pixel dungeon now has a second final boss, [REDACTED]. You can find it on the 25th floor in the ashen void.\n" +
						"\n" +
						"The amulet of yendor is no longer the only legendary artifact at the bottom of the dungeon...\n"));

		changes.addButton( new ChangeButton(new Image(new PrimeRibSprite()), "New Exotic Enemies",
				"**There are now a total of 5 new exotic enemies in devoted Pd:**\n" +
						"\n" +
						"The royal grub, Revenant, Troll Elder, Flaming Mole and Conjurer have been added as rare variants.\n" +
						"\n" +
						"The Prime rib, just like teh meatrack has entirely new mechanics and sprites.\n" +
						"\n"));

		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
		changes.hardlight(CharSprite.WARNING);
		changeInfos.add(changes);


		changes.addButton( new ChangeButton(new TalentIcon(Talent.ELDRITCH_ENERGY), "Zealot Talents",
				"**The Zealots Talents of the Zealot have been completely redone, allowing for more synergies and unique gameplay!**\n" +
						"\n" +
						"Some talents allow the Zealot to benefit from consumables such as bombs and throwing stones.\n" +
						"\n" +
						"Other talents increase the zealots unique synergy with trinkets.\n" +
						"\n" +
						"A few old talents have been kept and reworked or even retiered, to make for a more streamlined early and mid-game.\n"));

		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TRINKET_CATA), "Zealot unlocking",
				"The Zealot is no longer unlocked automatically and can now be played after crafting a trinket for the first time." ));


		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Devoted has been recoded entirely from scratch, most bugs from earlier versions no longer exist, but some new ones may habe come up along the way."));

		changes.addButton(new ChangeButton(Icons.get(Icons.REGION_ACTIVE), "Region switching availability",
				"For now, switching your region setup no longer requires you to beat the game, but this may be changed once more in the future." ));

		changes.addButton( new ChangeButton(Icons.get(Icons.CLOSE), "Removed Features",
				"**Since Devoted PD has been re-coded from the ground up, i have reconsidered some features and removed them. Some of these may come back in the future in a different form.**\n" +
						"\n" +
						"The legendary quest of the Troll Child has been removed. That includes the minibosses fought in the quest. This quest WILL return in a future update, but in an entirely different form. The current implementation was confusing, the minibosses were inconsistent and not fun to fight, and the quests difficulty and risk was not communicated well to players.\n" +
						"\n" +
						"With the removal of the legendary quest, the five legendary items have been removed as well. None of them will return in their current form, but the concept will not be scrapped forever.\n" +
						"\n" +
						"The texture reworks were mostly removed and reverted back to shattereds original textures. Most of these were not very good and the originals work well for the game. However, the unique sprites for spells and brews are staying ( some have been reworked and there are also some new ones ).\n" +
						"\n"));


	}

}
