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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wrath;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.CitadelBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CultistSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Cultist extends Mob {
	
	{
		spriteClass = CultistSprite.class;
		
		HP = HT = 50;
		defenseSkill = 18;
		
		EXP = 7;
		maxLvl = 20;

        loot = Generator.Category.SEED;
		lootChance = 0.3f;
		
		properties.add(Char.Property.UNDEAD);
		properties.add(Char.Property.DEMONIC);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 15, 21 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 24;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}


	private boolean invincibility = true;

	@Override
	public void damage(int dmg, Object src) {

		int newdmg = dmg;

		if (dmg >= this.HP && invincibility == true)
		{
			newdmg = this.HP-1;
			invincibility = false;
			Buff.prolong(this, Invulnerability.class, 4);
			Sample.INSTANCE.play(Assets.Sounds.EVOKE);

			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (fieldOfView != null && fieldOfView[mob.pos] && mob instanceof Cultist && mob.alignment == alignment) {
					Buff.prolong(mob, Invulnerability.class, 4);
				}
			}

		}

		else super.damage(newdmg, src);
	}

	@Override
	public void move( int step, boolean travelling) {

		if (travelling && this.buff(Invulnerability.class) != null) {
			this.HP = Math.min(HP + 5, HT);
			this.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			this.sprite.showStatus(CharSprite.POSITIVE, "+" + 5);
		}
		super.move( step, travelling);
	}

	private static final String INVINCIBILITY = "INVINCIBILITY";
	private static final String ADDPARTNER = "addpartner";

	private boolean addpartner = true;
	@Override
	protected boolean act() {
		//create a child
		if (addpartner == true && !(Dungeon.level instanceof CitadelBossLevel)) {

			ArrayList<Integer> candidates = new ArrayList<>();

			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (Dungeon.level.passable[n]
						&& Actor.findChar(n) == null
						&& (!Char.hasProp(this, Char.Property.LARGE) || Dungeon.level.openSpace[n])) {
					candidates.add(n);
				}
			}

			if (!candidates.isEmpty()) {
				Cultist child = new Cultist();
				addpartner = false;
				if (state != SLEEPING) {
					child.state = child.WANDERING;
				}

				child.pos = Random.element(candidates);

				GameScene.add(child);
				child.addpartner = false;

				Dungeon.level.occupyCell(child);

				if (sprite.visible) {
					Actor.addDelayed(new Pushing(child, pos, child.pos), -1);
				}
			}
		}
		return super.act();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(INVINCIBILITY, invincibility);
		bundle.put( ADDPARTNER, addpartner );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		invincibility = bundle.getBoolean( INVINCIBILITY );
		addpartner = bundle.getBoolean( ADDPARTNER );
	}

}
