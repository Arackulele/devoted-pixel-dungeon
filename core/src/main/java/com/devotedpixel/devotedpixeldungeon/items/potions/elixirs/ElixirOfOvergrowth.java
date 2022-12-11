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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.StaffSplinter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.watabou.utils.PathFinder;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import java.util.ArrayList;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class ElixirOfOvergrowth extends Elixir {
	
	{
		image = ItemSpriteSheet.ELIXIR_WILD;
	}
	
	@Override
	public void apply(Hero hero) {
		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			Buff.affect(hero, Overgrowth.class).set(Overgrowth.DURATION);
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (40 + 30);
	}

	public static class Overgrowth extends Buff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		public static final float DURATION	= 40f;

		protected int left;

		private static final String LEFT	= "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );

		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			left = bundle.getInt( LEFT );
		}

		public void set( float duration ) {
			this.left = (int)duration;
		}


		@Override
		public boolean act() {

			int b = Dungeon.level.map[target.pos];
			if (b == Terrain.GRASS || b == Terrain.FURROWED_GRASS || b == Terrain.HIGH_GRASS )
			{
				target.HP += (target.HT/55)+1;
				target.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			}

			ArrayList<Integer> positions = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
					positions.add(p);
			}


			for (int i : positions){
				int t = Dungeon.level.map[i];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(i) == null && Random.Int( 6 ) == 0){
					Level.set(i, Terrain.HIGH_GRASS);
					GameScene.updateMap(i);
					CellEmitter.get( i ).burst( LeafParticle.LEVEL_SPECIFIC, 4 );
				}
		}



			if (left <= 0){
				detach();
			} else {
				spend(TICK);
				left--;
			}
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.IMBUE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0f, 0.75f, 0f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - left) / DURATION);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(left);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", left);
		}


	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		{
			inputs =  new Class[]{PotionOfHealing.class, StaffSplinter.class};
			inQuantity = new int[]{1, 1};

			cost = 6;

			output = ElixirOfOvergrowth.class;
			outQuantity = 1;
		}

	}

}
