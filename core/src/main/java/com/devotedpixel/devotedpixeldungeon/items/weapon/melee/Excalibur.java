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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public class Excalibur extends MeleeWeapon {

	{
		image = ItemSpriteSheet.EXCALIBUR;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;
		ACC = 1.1f;
		tier = 6;

		unique = true;
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+2) +
				lvl*(tier+1);
	}

	@Override
	public int proc( Char attacker, Char defender, int damage ) {

		if (attacker.HP > attacker.HT*0.9 || attacker.HP < attacker.HT*0.1) Buff.prolong(defender, Paralysis.class, 2 + this.buffedLvl());

		return damage;
	}

	@Override
	public int STRReq(int lvl) {
		return STRReq(tier-2, lvl); //14 base strength req, down from 18
	}

}
