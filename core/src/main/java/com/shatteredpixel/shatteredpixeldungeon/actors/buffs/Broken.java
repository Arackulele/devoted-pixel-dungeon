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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Broken extends Buff {
	
	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public int amount = 0;
	
	@Override
	public int icon() {
		return BuffIndicator.BROKEN;
	}

	@Override
	public String iconTextDisplay() {
		if (amount > 0){
			return Integer.toString(amount);
		} else {
			return "";
		}
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", amount);
	}

	public static String AMOUNT = "amount";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(AMOUNT, amount);
	}

	@Override
	public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
		super.restoreFromBundle(bundle);
		amount = bundle.getInt(AMOUNT);
	}



}
