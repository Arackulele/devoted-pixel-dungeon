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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ConjurerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LeaderSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.List;

import static com.watabou.utils.Random.Int;

public class Conjurer extends Leader {
	
	{
		spriteClass = ConjurerSprite.class;
		
		HP = HT = 65;
		defenseSkill = 20;
		
		EXP = 11;
		maxLvl = 20;

		loot = Generator.Category.WAND;
		lootChance = 1f;
		
		properties.add(Property.UNDEAD);
		properties.add(Property.DEMONIC);
	}



	@Override
	public boolean act() {

		if (cooldown > 0) cooldown--;

		if (Dungeon.level.distance(Dungeon.hero.pos, pos) < 4 && cooldown < 1 && this.state != SLEEPING) {
			cooldown = 40;

			Sample.INSTANCE.play( Assets.Sounds.WHAIL );

			Buff.affect(Dungeon.hero, CorruptionArena.class).setup(this.pos);
            spend( Actor.TICK );
            return true;

		}
		else return super.act();
	}


	public static class CorruptionArena extends Buff {

		private ArrayList<Integer> arenaPositions = new ArrayList<>();
		private ArrayList<Emitter> arenaEmitters = new ArrayList<>();

		private static final float DURATION = 35;
		int left = 0;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0f, 0f);
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

		public void setup(int pos){

			int dist;
			if (Dungeon.depth == 5 || Dungeon.depth == 10 || Dungeon.depth == 20){
				dist = 4; //smaller boss arenas
			} else {

				boolean[] visibleCells = new boolean[Dungeon.level.length()];
				Point c = Dungeon.level.cellToPoint(pos);
				ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), visibleCells, Dungeon.level.losBlocking, 8);

				dist = 5;
			}

			PathFinder.buildDistanceMap( pos, BArray.or( Dungeon.level.passable, Dungeon.level.avoid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE && !arenaPositions.contains(i)) {
					arenaPositions.add(i);
				}
			}
			if (target != null) {
				fx(false);
				fx(true);
			}

			left = (int) DURATION;

		}

		@Override
		public boolean act() {

			if (!arenaPositions.contains(target.pos)){
				detach();
			}

			left--;
			BuffIndicator.refreshHero();
			if (left <= 0){
				detach();
			}

			Random.shuffle(arenaPositions);

			ArrayList<Mob> PossibleMobs = new ArrayList<>();
			PossibleMobs.add(new Skeleton());
			PossibleMobs.add(new ColdCorpse());
			PossibleMobs.add(new Wraith());
			PossibleMobs.add(new MeatRack());
			Random.shuffle(PossibleMobs);

			if (Random.Int(2) == 1) {

				for (int i : arenaPositions) {

					if (findChar(i) == null && Dungeon.level.passable[i]) {
						Mob m = PossibleMobs.get(0);
						m.pos = i;
						m.state = m.WANDERING;
						m.lootChance = 0;
						GameScene.add(m);
						Dungeon.level.occupyCell(m);

						break;
					}
				}
			}


			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on){
				for (int i : arenaPositions){
					Emitter e = CellEmitter.get(i);
					e.pour(PoisonParticle.MISSILE, 0.07f);
					arenaEmitters.add(e);
				}
			} else {
				for (Emitter e : arenaEmitters){
					e.on = false;
				}
				arenaEmitters.clear();
			}
		}

		private static final String ARENA_POSITIONS = "arena_positions";
		private static final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[arenaPositions.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = arenaPositions.get(i);
			bundle.put(ARENA_POSITIONS, values);

			bundle.put(LEFT, left);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			int[] values = bundle.getIntArray( ARENA_POSITIONS );
			for (int value : values) {
				arenaPositions.add(value);
			}

			left = bundle.getInt(LEFT);
		}
	}

	private static final String COOLDOWN = "COOLDOWN";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COOLDOWN, cooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cooldown = bundle.getFloat( COOLDOWN );
	}



}
