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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;

public abstract class NPC extends Mob {

	{
		HP = HT = 1;
		EXP = 0;

		alignment = Char.Alignment.NEUTRAL;
		state = PASSIVE;
	}

	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos]){
			Bestiary.setSeen(getClass());
		}

		return super.act();
	}

	@Override
	public void beckon( int cell ) {
	}

	@Override
	public void damage(int dmg, Object src) {
		if (Dungeon.hero.hasTalent(Talent.TAG_TEAM))
		{
			int amnt = 0;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.alignment == Char.Alignment.ALLY) {
					amnt++;
				}
			}

			if (amnt < 2) dmg *= (0.9 - (Dungeon.hero.pointsInTalent(Talent.TAG_TEAM)  * 0.05));

		}
		super.damage(dmg, src);
	}
	
}