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
		
		add_v0_1_1_Changes(changeInfos);
		add_v0_1_0_Changes(changeInfos);
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
