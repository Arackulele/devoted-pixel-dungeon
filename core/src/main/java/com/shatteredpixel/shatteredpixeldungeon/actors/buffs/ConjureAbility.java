/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndConjurerSummoning;
import com.watabou.utils.Bundle;

public class ConjureAbility extends FlavourBuff implements ActionIndicator.Action {
	private static final String OBJECT = "object";
	private static final String BONUS = "bonus";

	public static final float DURATION = 4f;

	{
		type = buffType.POSITIVE;
	}

	public void set(float energy) {
	}

	@Override
	public boolean attachTo(Char target) {
		ActionIndicator.setAction(this);
		return super.attachTo(target);
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
	}

	@Override
	public int icon() {
		return BuffIndicator.Conjurer_ENERGY;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public String actionName() {

		return Messages.get(this, "summon");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.CONJURE_MINIONS;
	}

	@Override
	public int indicatorColor() {
		return 0xC6C6C6;
	}

	@Override
	public void doAction() {

		GameScene.show(new WndConjurerSummoning(this));

	}

	public static abstract class ConjureMobs {

		public static ConjureAbility.ConjureMobs[] abilities = new ConjureAbility.ConjureMobs[]{
				new Wisp(),
				new Imp(),
				new Wasp(),
				new Wretch(),
				new Homonculus()
		};

		public String name() {
			return Messages.get(this, "name");
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public abstract int energyCost();

		public abstract Char toSummon();

		public boolean usable(ConjureAbility buff) {
			return Dungeon.energy >= energyCost();
		}

		public String targetingPrompt() {
			return Messages.get(this, "prompt");
		}

		public void doAbility(Hero hero, Integer target) {
			if (target == null || target == -1){
				return;
			}

			int range = 2;

			if (Dungeon.level.distance(hero.pos, target) > range){
				GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range"));
				return;
			}

			if (Actor.findChar(target) != null){
				GLog.w(Messages.get(MeleeWeapon.class, "ability_occupied"));
				return;
			}

			hero.busy();
			com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.MISS);
			Char ch;
			ch = toSummon();
			ch.pos = target;
			GameScene.add((Mob) ch);
			ScrollOfTeleportation.appear(ch, ch.pos);
			Dungeon.energy -= energyCost();

			if (hero.hasTalent(Talent.HIVE_MIND))
			{
				float division = 0;

				switch (hero.pointsInTalent(Talent.HIVE_MIND))
				{
					case 1: division = 0.6f;
					case 2: division = 0.75f;
					case 3: division= 1;
				}

				prolong( ch, Invulnerability.class, hero.buff(Invulnerability.class).cooldown() * division );
			}

			hero.next();

		}


		public static class Wisp extends ConjureAbility.ConjureMobs {
			@Override
			public int energyCost() {
				return 2;
			}

			@Override
			public Char toSummon() {
				return new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wisp();
			}

		}

		public static class Imp extends ConjureAbility.ConjureMobs {
			@Override
			public int energyCost() {
				return 4;
			}

			@Override
			public Char toSummon() {
				return new BlueImp();
			}

		}

		public static class Wasp extends ConjureAbility.ConjureMobs {
			@Override
			public int energyCost() {
				return 6;
			}

			@Override
			public Char toSummon() {
				return new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wasp();
			}

		}

		public static class Wretch extends ConjureAbility.ConjureMobs {
			@Override
			public int energyCost() {
				return 8;
			}

			@Override
			public Char toSummon() {
				return new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wretch();
			}

		}

		public static class Homonculus extends ConjureAbility.ConjureMobs {
			@Override
			public int energyCost() {
				return 12;
			}

			@Override
			public Char toSummon() {
				return new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Homonculus();
			}

		}


	}
}
