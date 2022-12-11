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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PaladinSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Excalibur;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SoulgemRing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Paladin extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
	{
		spriteClass = PaladinSprite.class;
		
		HP = HT = Dungeon.hero.lvl*25;
		EXP = 0;

		flying = true;

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

		Ballistica route = new Ballistica(this.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
		int cell = route.collisionPos;

		for (int i : PathFinder.NEIGHBOURS8) {
			hasadjacent=true;
			Char mob = Actor.findChar(this.pos + i);
			if (mob != null) {
				if (timer >=6){
					if (mob.pos == this.pos + i) {
						Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, Ballistica.MAGIC_BOLT);
						int strength = 4;
						WandOfBlastWave.throwChar(mob, trajectory, strength, true, true, this.getClass());
						timer=0;
					}
				}
			else timer++;
			}
			}
		if (timer==0 && this.HP < this.HT && hasadjacent==true) this.HP += this.HT/25;

	return super.act();
	}

	{
		immunities.add(Doom.class);
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
		TrollChild.reward = new Excalibur();
	}

}
