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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class RingOfSouls extends Buff {

	
	{
		type = buffType.POSITIVE;
		announced = false;
	}
	private static int ringlvl = 0;

	public static void setLvl(int level){
		ringlvl = level;
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			Item.updateQuickslot();
			if (target == Dungeon.hero) ((Hero) target).updateHT(false);
			return true;
		}
		return false;
	}

	@Override
	public void detach() {
		super.detach();
		if (target == Dungeon.hero) ((Hero) target).updateHT(false);
		Item.updateQuickslot();
	}

	//called in Item.buffedLevel()
	public static int reduceLevel( int level ){

		 return (int)(level+1+(ringlvl*0.34));
		}


	@Override
	public int icon() {
		return BuffIndicator.ROS;
	}

	
}
