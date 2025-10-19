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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class EternalBalance extends ArmorAbility {

	{
		baseChargeUse = 30f;
	}

	@Override
	public String targetingPrompt() {
		if (Dungeon.hero.buff(Invulnerability.class) == null){
			return Messages.get(this, "prompt");
		}
		return super.targetingPrompt();
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return dst;
	}

	@Override
	public boolean useTargeting() {
		return false;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {




		//hero.sprite.emitter().burst(LeafParticle.GENERAL, 10);

		if (Dungeon.hero.buff(Invulnerability.class) == null)
		{

			if (target == null || Actor.findChar(target) == null){
				GLog.w( Messages.get(this, "charlack") );
				return;
			}

			Char ch = Actor.findChar(target);

			if (Dungeon.level.adjacent(ch.pos, hero.pos))
			{

				//trace a ballistica to our target (which will also extend past them
				Ballistica trajectory = new Ballistica(hero.pos, ch.pos, Ballistica.STOP_TARGET);
				//trim it to just be the part that goes past them
				trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
				//knock them back along that ballistica
				WandOfBlastWave.throwChar(ch, trajectory, 4 + Dungeon.hero.pointsInTalent(Talent.GRAY_MATTER), true, true, hero);
				if (Dungeon.hero.hasTalent(Talent.SUBZERO_KICK)) {
					//ToDO: Fix frost getting immediately dispelled
					Buff.affect(ch, Frost.class, Math.min(4, trajectory.dist));

					if (Dungeon.hero.pointsInTalent(Talent.SUBZERO_KICK) > 1) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[hero.pos + offset]) {

								GameScene.add(Blob.seed(hero.pos + offset, (Dungeon.hero.pointsInTalent(Talent.SUBZERO_KICK) * 2 )- 2, Freezing.class));

							}
						}
					}
				}
				else Buff.affect(ch, Paralysis.class, Math.min( 4 + Dungeon.hero.pointsInTalent(Talent.GRAY_MATTER), trajectory.dist));
				hero.spendAndNext(Actor.TICK);

				if (ch.isAlive() && trajectory.dist > 1)
				{
					//ToDO: Cap at enemy position
				ScrollOfTeleportation.appear( hero, trajectory.path.get(trajectory.dist-1 ));
				Sample.INSTANCE.play( Assets.Sounds.PUFF );
				Dungeon.level.occupyCell( hero );
				}
			}
			else {
				GLog.w( Messages.get(this, "dst") );
				return;
			}

		}

		if (Dungeon.hero.buff(Invulnerability.class) != null) {

			PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid, null), 1 + hero.pointsInTalent(Talent.GRAY_MATTER));
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {

					CellEmitter.get(i).burst(Speck.factory(Speck.LIGHT), 5);

					if (Actor.findChar(i) instanceof Mob) {

						Char ch = Actor.findChar(i);

						if (ch != null && ch.isAlive() && ch.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[ch.pos]) {
							int damage = Math.round(Random.NormalIntRange(12 + (Dungeon.hero.pointsInTalent(Talent.SUSTAINED_PRESSURE) * 3), 25));
							damage = Math.round(damage);
							Buff.prolong(Dungeon.hero, Invulnerability.class, Dungeon.hero.buff(Invulnerability.class).cooldown() + 1);
							ch.damage(damage, this);
							if (Dungeon.hero.hasTalent(Talent.SUSTAINED_PRESSURE))
								Buff.prolong(ch, Hex.class, Dungeon.hero.pointsInTalent(Talent.SUSTAINED_PRESSURE));
						}
					}
				}
			}
			hero.spendAndNext(Actor.TICK);
		}





		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();

	}



	@Override
	public int icon() {
		return HeroIcon.ETERNAL_BALANCE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SUSTAINED_PRESSURE, Talent.SUBZERO_KICK, Talent.GRAY_MATTER, Talent.HEROIC_ENERGY};
	}
}
