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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.BloodVial;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class v0_1_Changes {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo( "v0.1.X", true, "");
		changes.hardlight( Window.TITLE_COLOR);
		changeInfos.add(changes);

		add_v0_4_X_Changes(changeInfos);
		add_v0_3_X_Changes(changeInfos);
		add_v0_2_X_Changes(changeInfos);
		add_v0_1_1_Changes(changeInfos);
		add_v0_1_0_Changes(changeInfos);
	}

	public static void add_v0_4_X_Changes( ArrayList<ChangeInfo> changeInfos ){

		ChangeInfo changes = new ChangeInfo("v0.4.x", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Developer Commentary",
				"_-_ Released December 6th, 2022\n" +
						"_-_ Fourth Update\n" +
						"\n" +
						"Insane true and also real.\n" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.GOLD), "Old feature changes!",
				"The Zealots Blood Vial now stales, this means the cooldown gets increased if you are not near an enemy and increased even more if you have already killed all mobs on a floor, this is to prevent a lot of cheese.\nmore texture updates\nA few mobs have been reworked\nThe deathstick now has unique visuals and a unique battlemage effect\nThe Troll Child can no longer die For real this time" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "Talent Changes!",
				"A few of the Zealots Talents have been reworked again, see the changes in game.\n" ));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ESSENCE), "New Region",
				"The third new Region is _The Forge_\n\n" +
						"This housing complex of the granite trolls replaces the Caves in half of runs! Including new enemies, rooms and a new boss along with its drops"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ROYAL_SEAL), "New Region 2",
				"The fourth new Region is _The dwarven citadel_\n\n" +
						"This dwarven military base filled with cultists replaces the Dwarven metropolis in half of runs! Including new enemies, rooms and a new boss along with its drops"));

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"Balance Tweaks:\n" +
						"_-_ The beast boss has been nerfed again\n" +
						"_-_ Various changed to enemies and their drops\n" +
						"\n" +
						"Misc:\n" +
						"_-_ Devoted now has a new logo, made by Cevin_2006\n" +
						"_-_ The Sprites for the evil sage have been overhauled"));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes") + " 1",
				"Fixed\n" +
						"_-_ troll child dying issues\n" +
						"_-_ issues with the wrath built up by the devotee\n" +
						"_-_ various issues with the soulgem ring"));

	}

	public static void add_v0_3_X_Changes( ArrayList<ChangeInfo> changeInfos ){

		ChangeInfo changes = new ChangeInfo("v0.3.x", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Developer Commentary",
				"_-_ Released November 20th, 2022\n" +
						"_-_ Third Update\n" +
						"\n" +
						"Hey this is a really big update, and only 2 weeks after the last one.\n" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.GOLD), "Old feature changes!",
				"The Zealots T2 Talents have changed to actually work and not be overpowered.\nIve gone ahead and updated some old textures\nThe Troll Child can no longer die" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "Talent Changes!",
				"A few of the Zealots Talents have been reworked again, see the changes in game.\n" ));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SPLINTER), "New Region",
				"The first new Region is _The Garden_\n\n" +
						"This overgrown and long abandoned underground greenhouse replaces the Sewers in half of runs! Includng new enemies, rooms and a new boss along with its drops"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.MEAL), "New Region 2",
				"The second new Region is _The Coldhouse_\n\n" +
						"This below-freezing region used for storing and preparing food replaces the Prisons in half of runs! Includng new enemies, rooms and a new boss along with its drops"));

		changes.addButton( new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"Balance Tweaks:\n" +
						"_-_ The Deathstick has been significantly buffed\n" +
						"_-_ Minor nerfs to the excalibur\n" +
						"\n" +
						"Misc:\n" +
						"_-_ Devoted now has a new logo, made by Cevin_2006\n" +
						"_-_ The Sprites for the evil sage have been overhauled"));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes") + " 1",
				"Fixed\n" +
						"_-_ troll child saving/spawning issues\n" +
						"_-_ some bugs with mystical items\n" +
						"_-_ the envoy healing way too much, to the point where it was unbeatable"));

	}

	public static void add_v0_2_X_Changes( ArrayList<ChangeInfo> changeInfos ){

		ChangeInfo changes = new ChangeInfo("v0.2.x", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Developer Commentary",
				"_-_ Released November 5th, 2022\n" +
						"_-_ Second Update\n" +
						"\n" +
						"New quest n stuff.\n" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.GOLD), "Quality of Life Changes!",
				"The Quickslot for the Blood Vial now automatically updates to stabbing/drinking.\n" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "Talent Changes!",
				"A few of the Zealots Talents have been reworked.\n" ));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SOULGEM_RING), "New Mystical Items",
				"Each Class can now get a unique _Mystical Item_\n\n" +
						"These Items are exceptionally Powerful and can help you in several different ways"));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes") + " 1",
				"Fixed\n" +
						"_-_ blood vial charges reset upon load\n" +
						"_-_ imp sprite being broken\n" +
						"_-_ other minor bugs"));

	}
	
	public static void add_v0_1_1_Changes( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo("v0.1.x", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.DEVO), "Developer Commentary",
				"_-_ Released October 27th, 2022\n" +
				"_-_ First one real\n" +
				"\n" +
				"Zealot Moment.\n" ));

		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "Title Screen Updates!",
						"Theres a new title screen background now.\n" ));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes") + " 1",
				"Fixed\n" +
						"_-_ game crash after defeating the second boss as the zealot\n" +
						"_-_ various talents of the zealot not working\n" +
						"_-_ hells eye being unable to die and bugging out the game\n" +
						"_-_ the blood vial charges being reset upon reload\n" +
						"_-_ debug features still being enabled"));

				changes.addButton( new ChangeButton(new Image(Assets.Sprites.TROLL_CHILD, 0, 90, 12, 15), "New Major Quest",
						"There is a new Class: the _Zealot._\n\n" +
								"Theres a new major quest that can appear almost anywhere through the dungeon! This involves fighting a very difficult, class based miniboss and the reward is your classes mystical item. But you shouldnt always accept this Quest, it can often lead to your doom."));
	}
	
	public static void add_v0_1_0_Changes( ArrayList<ChangeInfo> changeInfos ){
		
		ChangeInfo changes = new ChangeInfo("v0.1.0", false, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton( new ChangeButton(new Image(Assets.Sprites.ZEALOT, 0, 90, 12, 15), "New Class",
				"There is a new Class: the _Zealot._\n\n" +
						"The Zealot is a class based on risk and reward, they gain slightly more health than other heroes and start with a unique blood vial"));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_VIAL), "New Artifact",
				"The Zealot starts with a unique artifact: the _Blood Vial._\n\n" +
						"The Blood Vial gains upgrades as it is used, but it doesnt charge automatically. In order to charge it, you have to stab yourself which deals percentage based damage. Drinking this Blood will make you invincible for a few turns"));


		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "New Talents",
				"The _Zealot._ also comes with all T1-T4 Talents\n\n" +
						"These Talents focus on taking damage and unique interactions with their blood vial."));

		changes.addButton( new ChangeButton(Icons.get(Icons.TALENT), "New Subclasses",
				"The _Zealot._ also comes with 2 Subclasses\n\n" +
						"The Archangel focuses on summoning allies when you _drink blood_.\n\n" +
				"The Devotee gets bonus effects upon _stabbing yourself_ with your vial."));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARMOR_ZEALOT), "New Class Armor",
				"The Zealot, like other classes has a class armor with 3 different abilities: the _Heroes Veil._\n\n" +
						"_Final Prayer_: The Zealot casts a _final prayer_, damaging themselves, hopping out of harms way and healing slowly back to health\n\n" +
						"_Weal and Woe_: The Zealot _calls out to the heavens_ and leaves their fate in the hands of the gods!\n\n" +
						"_Hells Eye_: The Zealot summons _hells eye_ to unleash mayhem on enemies!"


				));

		changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.WAND_FIREBOLT), "Sprite Updates!",
				"Ive gone ahead and updated a bunch of sprites for items._\n\n" +
						"_Meelee Weapons_: The Wood colors on the different tiers are now more consistent and higher tier items now have a more powerful look along with some other misc changes.\n\n" +
						"_Wands_: Wand sprites have been overhauled entirely to look more fitting within the games overall style.\n\n" +
						"_Rings_: Rings now have more variety outside of just gem color, like the metal they are made of and location and shape of their gem.\n\n" +
						"_Artifacts_: Artifacts have gotten some minor texture changes to make the shading a little more consistent and to make them a bit mroe vibrant.\n\n" +
						"_Spells+Brews_: Brews and spells have been given unique textures that fit their effects instead of being single color sprites that dont signify their use.\n\n" +
						"_other changes_: Scrolls and Stones have gotten some better contrast on the symbols on them."


		));


	}


	
}
