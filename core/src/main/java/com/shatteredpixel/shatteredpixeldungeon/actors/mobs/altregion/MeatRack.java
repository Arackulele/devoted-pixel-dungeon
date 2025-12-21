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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MeatRackSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MeatRack extends Mob {

	public Item item;
	
	{
		spriteClass = MeatRackSprite.class;
		
		HP = HT = 34;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 11;

		loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
		lootChance = 0.03f; //initially, see lootChance()

		//WANDERING = new Wandering();
		//FLEEING = new Fleeing();

		properties.add(Char.Property.UNDEAD);
	}

	private static final String STOLENFOOD = "item";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STOLENFOOD, StolenFood );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		for (com.watabou.utils.Bundlable Item : bundle.getCollection( STOLENFOOD )) {
			if (item != null){
				StolenFood.add((Item) item);
			}
		}
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 10 );
	}


	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.THEIF_MISC.count);
	}

	@Override
	public void rollToDropLoot() {
		for(Item F : StolenFood)
		{
			int ofs = com.watabou.utils.PathFinder.NEIGHBOURS8[Random.Int(8)];
			if (F.quantity() != 0) {
				if (!Dungeon.level.solid[pos + ofs] && Dungeon.level.passable[pos + ofs]) {
					Dungeon.level.drop(F,
							pos + ofs
					).sprite.drop();
				} else {
					Dungeon.level.drop(F,
							pos
					).sprite.drop();
				}
			}
		}
		super.rollToDropLoot();
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.THEIF_MISC.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
		return 14;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 3);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (Random.Int(20) >= 9 && enemy == Dungeon.hero) steal((Hero)enemy);

		return damage;
	}

	public ArrayList<Item> StolenFood = new ArrayList<>();
	protected boolean steal( Hero hero ) {

			Food toSteal = hero.belongings.getItem(Food.class);

			Item f;

			if (toSteal != null) {
				f = toSteal.detach(hero.belongings.backpack);
				StolenFood.add(f);
				GLog.n( Messages.get( MeatRack.class, "stole"));


				if (HP < HT) {
					int toHeal = Random.NormalIntRange(1, 5);
					if (this.getClass() == PrimeRib.class) toHeal = Random.NormalIntRange(1, 3);

					HP = Math.min(HT, HP + toHeal);
					sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);
				}
			}
			else {
				GLog.n( Messages.get( MeatRack.class, "anger"));
				Buff.affect(this, Adrenaline.class, 20f);
			}

	return true;
	}

	@Override
	public String description() {
		String desc = super.description();

		if (item != null) {
			desc += Messages.get(this, "carries", item.name() );
		}

		return desc;
	}




}
