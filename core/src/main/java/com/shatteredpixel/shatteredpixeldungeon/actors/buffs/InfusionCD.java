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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.FurnaceGolem;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.particles.BitmaskEmitter;
import com.watabou.utils.Bundle;

public class InfusionCD extends FlavourBuff {

	public static final float DURATION = 100f;

    public boolean PrematureDetach = false;

    public Item ToGiveBack;

	{
		type = buffType.POSITIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.TIME;
	}

    @Override
    public void detach() {

        //Perhaps this should check more often, so its more responsive
        //Or less, so it uses less ressources, either way this is not a great solution to infusion to work with all items
        //since it looks weird visually to get the buff and then have it removed
        boolean FoundHeap = false;
        for (Heap h : Dungeon.level.heaps.valueList())
        {
            for (Item item : h.items){
                if ((item.isInfused)){
                    FoundHeap = true;
                    break;
                }
                if (FoundHeap) break;
            }
        }

        for (Item item : Dungeon.hero.belongings){
            if ((item.isInfused) || FoundHeap){
                FoundHeap = true;
                break;
            }
        }

        if (FoundHeap || ToGiveBack == null)
        {
                PrematureDetach = true;
                super.detach();
                return;
        }

        Dungeon.hero.sprite.emitter().burst(EnergyParticle.FACTORY, 15);

        if (ToGiveBack.doPickUp( Dungeon.hero )) {

        } else {
            Dungeon.level.drop( ToGiveBack, Dungeon.hero.pos ).sprite.drop();
        }

        super.detach();
    }

    public boolean buffer = true;

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", cooldown(), ToGiveBack.name());
	}

    private static final String ITEMTOGIVE = "item_to_give";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEMTOGIVE, ToGiveBack);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ToGiveBack = (Item) bundle.get(ITEMTOGIVE);
    }
}
