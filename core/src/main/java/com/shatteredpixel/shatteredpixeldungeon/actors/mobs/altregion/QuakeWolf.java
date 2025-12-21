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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.RockBlock;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Ruby;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.QuakeWolfSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.watabou.utils.Random;

public class QuakeWolf extends CragWolf {

	{
		spriteClass = QuakeWolfSprite.class;

		HP = HT = 200;
		defenseSkill = 10;

		EXP = 20;

		baseSpeed = 0.5f;

		properties.add(Property.BOSS);
		properties.add(Property.LARGE);
		properties.add(Property.FIERY);

		SLEEPING = new Sleeping();
		state = SLEEPING;

        loot = new RockBlock().quantity(Random.Int(3) + 1);
        lootChance = 0.51f;

    }

	public int totalrubies = 40;
	public String TOTALRUBIES = "totalrubies";

	public static QuakeWolf instance;


	@Override
	public int attackSkill( Char target ) {
		return 22;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(3, 9);
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.75f;
	}

	@Override
	protected boolean act() {
		boolean result = super.act();

		return result;
	}

	public void DropGem()
	{

		if (totalrubies > 0) {
			int ofs;
			do {
				ofs = com.watabou.utils.PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop(new Ruby(), pos + ofs).sprite.drop(pos);
			totalrubies--;
		}

	}

	@Override
	public boolean isInvulnerable(Class effect) {
		//immune to damage when inactive
		return  state == SLEEPING || super.isInvulnerable(effect);
	}

	@Override
	public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
		super.restoreFromBundle(bundle);

		instance = this;
		totalrubies = bundle.getInt(TOTALRUBIES);

		if (state != SLEEPING){
			BossHealthBar.assignBoss(this);
		}

	}

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TOTALRUBIES, totalrubies);
	}

	protected class Sleeping extends Mob.Sleeping {

		@Override
		protected void awaken(boolean enemyInFOV) {
			boolean vaultsexist = false;
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob instanceof Vault) vaultsexist = true;
			}

			if (!vaultsexist) {
				super.awaken(enemyInFOV);
				Buff.affect( QuakeWolf.this, QuakeWolf.Inferno.class );
				enemy = Dungeon.hero;
				BossHealthBar.assignBoss(QuakeWolf.this);
			}
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		BountyHunter.Quest.beatBoss();
		com.watabou.noosa.audio.Sample.INSTANCE.playDelayed(Assets.Sounds.BLAST, 0.1f);
		PixelScene.shake( 3, 0.7f );

		while (totalrubies > 0)
		{
			DropGem();
		}

		for (int i = 0; i < Dungeon.level.length(); i++){
			if (Dungeon.level.dry[i] && Dungeon.level.trueDistance(i, pos) <= 5){
				Level.set(i, Terrain.WATER);
				GameScene.updateMap(i);
				Splash.at(i, 0xed980e, 15);

				Char mob = Actor.findChar(i);
				if (mob != null) Dungeon.level.occupyCell(mob);
			}
		}
	}


	public static class Inferno extends Buff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		private float scale = 1;
		public String SCALE = "scale";

		@Override
		public void restoreFromBundle(com.watabou.utils.Bundle bundle) {
			super.restoreFromBundle(bundle);
			scale = bundle.getFloat(SCALE);
		}

		@Override
		public void storeInBundle(com.watabou.utils.Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SCALE, scale);
		}


		@Override
		public boolean act() {
			spend( 1f );

			Mob ref = (Mob)target;

			Char t = ref.enemy;

			if (t == null || ref.fieldOfView == null ||ref == null || !(ref.fieldOfView[t.pos])) {
				scale = 1;
				return true;
			}
			else {

				//shoot beams
                if (ref.sprite != null && t.sprite != null) if (ref.sprite.visible || t.sprite.visible )ref.sprite.parent.add(new Beam.InfernoRay(ref.sprite.center(), t.sprite.center()));
				t.damage((int)scale, new Burning());


				if(scale < 20)scale+= 0.5f;

			}

		return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.SACRIFICE;
		}

	}

}
