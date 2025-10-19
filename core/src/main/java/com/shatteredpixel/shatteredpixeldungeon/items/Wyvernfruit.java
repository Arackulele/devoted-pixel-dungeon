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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Broken;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Wyvernfruit extends Item {



	{
		image = ItemSpriteSheet.WYVERNFRUIT;

		stackable = true;
		dropsDownHeap = true;

	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {

		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());

		if (hero.buff(Broken.class) != null){

			if (hero.buff(Broken.class).amount > 1)hero.buff(Broken.class).amount -= 1;
			else Buff.detach(hero, Broken.class);

			GameScene.pickUp( this, pos );

		} else return false;


		com.watabou.noosa.audio.Sample.INSTANCE.play( Assets.Sounds.EAT );
		hero.spendAndNext( TIME_TO_PICK_UP );

		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	

}
