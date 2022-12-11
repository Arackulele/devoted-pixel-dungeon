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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EnvoySprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Excalibur;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SoulgemRing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Envoy extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
	
	{
		spriteClass = EnvoySprite.class;

		HP = HT = Dungeon.hero.lvl*25;
		EXP = 0;
		
		flying = true;

		baseSpeed = 1.5f;

		state = WANDERING;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}

	private static final String LEVEL = "level";
	private int timer;
	private Boolean hasadjacent;
	@Override
	protected boolean act() {

		hasadjacent=false;



		target = Dungeon.hero.pos;

		for (int i : PathFinder.NEIGHBOURS8) {
			Char character = Actor.findChar(this.pos + i);
			//Buff.affect(character, Weakness.class, 4);
			hasadjacent=true;
		}

		if (hasadjacent==false && this.HP < this.HT) {
			this.HP += this.HT/25;
		}

		return super.act();
	}


	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		adjustStats();
	}

	{
		immunities.add(Doom.class);
	}

	@Override
	public void damage( int dmg, Object src ) {


		ArrayList<Integer> spawnPoints = new ArrayList<>();
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = this.pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add(p);
			}
		}

		if (Random.Int(3) == 1) {

			ArrayList<Integer> respawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = this.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
					respawnPoints.add(p);
				}
				int index = Random.index(respawnPoints);

				if (respawnPoints.size() > 0) {
					Wraith mob = new Wraith();
					GameScene.add(mob);
					mob.pos = Random.element(spawnPoints);
					ScrollOfTeleportation.appear(mob, respawnPoints.get(index));
				}


			}
		}
		super.damage( dmg, src );
	}


	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.hero.lvl*2, Dungeon.hero.lvl*4 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10 + Dungeon.hero.lvl;
	}

	public void adjustStats() {
		this.level = level;
		this.HP = Dungeon.hero.lvl*25;
		this.HT = this.HP;
		defenseSkill = Dungeon.hero.lvl*2;
		enemySeen = true;
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}


	@Override
	public void die( Object cause ) {
		super.die( cause );
		TrollChild.Quest.complete();
		TrollChild.reward = new SoulgemRing();
	}

}
