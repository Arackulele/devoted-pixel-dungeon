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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpareRibsSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SpareRibs extends MeatRack {

	
	{
		spriteClass = SpareRibsSprite.class;
		
		HP = HT = 25;
		defenseSkill = 10;
		
		EXP = 7;
		maxLvl = 12;

		loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
		lootChance = 1f; //initially, see lootChance()

		properties.add(Property.DEMONIC);
	}

	@Override
	protected boolean steal( Hero hero ) {

		MysteryMeat raw = hero.belongings.getItem(MysteryMeat.class);
		FrozenCarpaccio frozen = hero.belongings.getItem(FrozenCarpaccio.class);
		StewedMeat stewed = hero.belongings.getItem(StewedMeat.class);
		ChargrilledMeat charred = hero.belongings.getItem(ChargrilledMeat.class);

		if (raw != null) {
			this.HP+=3;
			raw.detach(hero.belongings.backpack);
			raw.updateQuickslot();
			GLog.n( Messages.get( SpareRibs.class, "stole"));
			amount++;
		} else if (frozen != null) {
			this.HP+=6;
			frozen.detach(hero.belongings.backpack);
			frozen.updateQuickslot();
			GLog.n( Messages.get( SpareRibs.class, "stole"));
			amount++;
		} else if (stewed != null) {
			this.HP+=5;
			stewed.detach(hero.belongings.backpack);
			stewed.updateQuickslot();
			GLog.n( Messages.get( SpareRibs.class, "stole"));
			amount++;
		} else if (charred != null) {
			this.HP+=5;
			charred.detach(hero.belongings.backpack);
			charred.updateQuickslot();
			GLog.n( Messages.get( SpareRibs.class, "stole"));
			amount++;
		} else {
			GLog.n( Messages.get( MeatRack.class, "anger"));
			Buff.affect(this, Adrenaline.class, 40f);
			Buff.affect(this, Stamina.class, 40f);
		}

		return true;
	}

}
