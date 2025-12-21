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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Ruby;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Vault extends Mob {

	{
		spriteClass = VaultSprite.class;

		HP = HT = 60;

		maxLvl = -2;

		properties.add(Char.Property.INORGANIC);
		properties.add(Char.Property.IMMOVABLE);
		properties.add(Char.Property.STATIC);

		state = HUNTING;

	}

	private int gemstotal = Random.Int(3) + 6;

	private boolean enemiesnear = false;

	@Override
	protected boolean act() {

		enemiesnear = false;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Dungeon.level.distance(mob.pos, pos) < 8 && mob.alignment == this.alignment && !(mob instanceof Vault)) {
				enemiesnear = true;
			}
		}

		if (sprite != null ) sprite.idle();

		spend(Actor.TICK);

		return true;
	}



	@Override
	public boolean isInvulnerable(Class effect) {
		//immune to damage when inactive
		return  enemiesnear || super.isInvulnerable(effect);
	}

	@Override
	public void beckon(int cell) {
		//do nothing
	}

	public void DropGem()
	{

		if (gemstotal > 0) {
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS9[Random.Int(9)];
			} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs] && !Dungeon.level.dry[pos + ofs]);
			Dungeon.level.drop(new Ruby(), pos + ofs).sprite.drop(pos);
			gemstotal--;
			if (BountyHunter.Quest.Type() == BountyHunter.Quest.CRYSTAL) QuakeWolf.instance.totalrubies--;
			else TrollKnight.instance.totalrubies--;
		}

	}

	@Override
	public void rollToDropLoot() {
        for (int i = 0; i < 10; i++) DropGem();

		super.rollToDropLoot();

	}

	@Override
	public boolean interact(Char c) {
		return true;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (dmg >= 15){
			//takes 15/16/17/18/19/20 dmg at 15/17/20/24/29/36 incoming dmg
			dmg = 14 + (int)(Math.sqrt(8*(dmg - 14) + 1) - 1)/2;
		}
		if (dmg >= 2 && !isInvulnerable(src.getClass())) DropGem();
		super.damage(dmg, src);

	}

	@Override
	public void die(Object cause) {
		if (Dungeon.level.heroFOV[pos])com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.FALLING, 0.9f, 0.6f);

		super.die(cause);
	}

	private static final String GEMSTOTAL = "target_neighbour";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GEMSTOTAL, gemstotal);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		gemstotal = bundle.getInt(GEMSTOTAL);
	}

}
