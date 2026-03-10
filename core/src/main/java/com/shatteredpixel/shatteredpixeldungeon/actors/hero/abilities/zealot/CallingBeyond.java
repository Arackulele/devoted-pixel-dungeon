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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.zealot;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class CallingBeyond extends ArmorAbility {

	{
		baseChargeUse = 60f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	private static Char throwingChar;

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (target != null) {

			hero.sprite.operate(hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);

			armor.charge -= chargeUse(hero);

			final Char thrower = hero;
			final int finalTargetCell = target;
			throwingChar = thrower;
			final BombAbility.BombItem item = new BombAbility.BombItem();
			thrower.sprite.zap(finalTargetCell);
			((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
					reset(thrower.sprite,
							finalTargetCell,
							item,
							new com.watabou.utils.Callback() {
								@Override
								public void call() {
									item.onThrow(finalTargetCell);
									thrower.next();
								}
							});

			armor.updateQuickslot();
			Invisibility.dispel();
			hero.spendAndNext(Actor.TICK);

		}

	}

	@Override
	public int icon() {
		return HeroIcon.CALLING_BEYOND;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.FORBIDDEN_KNOWLEDGE, Talent.FORTIFIED_CONCIOUSNESS, Talent.CAUTION_TO_THE_WIND, Talent.HEROIC_ENERGY};
	}


	public static class BombAbility extends Buff {

		public int bombPos = -1;
		private int timer = 3;

		private int range = 3;

		private ArrayList<com.watabou.noosa.particles.Emitter> smokeEmitters = new ArrayList<>();

		@Override
		public boolean act() {

			if (smokeEmitters.isEmpty()){
				fx(true);
			}


			//Caution to the wind logic
			com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
			for (int cell = 0; cell < com.watabou.utils.PathFinder.distance.length; cell++) {

				if (com.watabou.utils.PathFinder.distance[cell] < Integer.MAX_VALUE) {
					Char ch = Actor.findChar(cell);
					if (ch != null && ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.CAUTION_TO_THE_WIND)) {

						Buff.prolong(Dungeon.hero, Recharging.class, Dungeon.hero.pointsInTalent(Talent.CAUTION_TO_THE_WIND));

					}
				}
			}

			if (Dungeon.hero.pointsInTalent(Talent.FORBIDDEN_KNOWLEDGE) == 4 && timer == 3) timer = 2;


			com.watabou.utils.PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
			if (timer == 3) {
				FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.WARNING);
			} else if (timer == 2){
				FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
			} else if (timer == 1){
				FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.WARNING);
			} else {
				com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
				for (int cell = 0; cell < com.watabou.utils.PathFinder.distance.length; cell++) {

					if (com.watabou.utils.PathFinder.distance[cell] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(cell);
						if (ch != null && ch != Dungeon.hero) {
							int dmg = com.watabou.utils.Random.NormalIntRange(30, 70);
							dmg -= ch.drRoll();

							if (dmg > 0) {
								WandOfMagicMissile m = new WandOfMagicMissile();
								ch.damage(dmg, m);
								if (Dungeon.hero.hasTalent(Talent.FORBIDDEN_KNOWLEDGE)) Buff.affect(ch, Daze.class, Dungeon.hero.pointsInTalent(Talent.FORBIDDEN_KNOWLEDGE) + 1);
							}


						}
						else if (ch == Dungeon.hero){
							int dmg = com.watabou.utils.Random.NormalIntRange(30, 70);
							dmg -= ch.drRoll();

							if (Dungeon.hero.hasTalent(Talent.FORTIFIED_CONCIOUSNESS))
							{
								switch (Dungeon.hero.pointsInTalent(Talent.FORTIFIED_CONCIOUSNESS))
								{
									default:
									case 1:
										dmg *= 0.5f;
										break;
									case 2:
										dmg *= 0.25f;
										break;
									case 3:
										dmg = 0;
										break;
								}

							}

							if (dmg > 0) {
								WandOfMagicMissile m = new WandOfMagicMissile();
								if (Dungeon.hero.pointsInTalent(Talent.FORTIFIED_CONCIOUSNESS) < 4)ch.damage(dmg, m);
								else {

                                    Dungeon.hero.SetHealth((int) (Dungeon.hero.HP + dmg * 0.25f));
									Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString((int)(dmg * 0.25f)), FloatingText.HEALING);

								}
							}


						}
					}

				}

				Heap h = Dungeon.level.heaps.get(bombPos);
				if (h != null) {
					for (Item i : h.items.toArray(new Item[0])) {
						if (i instanceof BombAbility.BombItem) {
							h.remove(i);
						}
					}
				}
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
				detach();
				return true;
			}

			timer--;
			spend(Actor.TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && bombPos != -1){
				com.watabou.utils.PathFinder.buildDistanceMap( bombPos, com.watabou.utils.BArray.not( Dungeon.level.solid, null ), range );
				for (int i = 0; i < com.watabou.utils.PathFinder.distance.length; i++) {
					if (com.watabou.utils.PathFinder.distance[i] < Integer.MAX_VALUE) {
						com.watabou.noosa.particles.Emitter e = CellEmitter.get(i);
						e.pour( BloodParticle.FACTORY, 0.25f );
						smokeEmitters.add(e);
					}
				}
			} else if (!on) {
				for (com.watabou.noosa.particles.Emitter e : smokeEmitters){
					e.burst(ChallengeParticle.FACTORY, 4);
				}
			}
		}

		private static final String BOMB_POS = "bomb_pos";
		private static final String TIMER = "timer";

		@Override
		public void storeInBundle(com.watabou.utils.Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BOMB_POS, bombPos );
			bundle.put( TIMER, timer );
		}

		@Override
		public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
			super.restoreFromBundle(bundle);
			bombPos = bundle.getInt( BOMB_POS );
			timer = bundle.getInt( TIMER );
		}

		public static class BombItem extends Item {

			{
				dropsDownHeap = true;
				unique = true;

				image = ItemSpriteSheet.MARKING_ROCK;
			}

			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}

			@Override
			public void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, BombAbility.class).bombPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, BombAbility.class).bombPos = cell;
				}
			}


		}
	}

}
