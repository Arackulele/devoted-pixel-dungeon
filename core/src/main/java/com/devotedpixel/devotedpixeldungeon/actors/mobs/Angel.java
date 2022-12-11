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
import com.shatteredpixel.shatteredpixeldungeon.sprites.AngelSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Sharanga;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SoulgemRing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Angel extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
	{
		spriteClass = AngelSprite.class;
		
		HP = HT = Dungeon.hero.lvl*25;
		EXP = 0;

		state = WANDERING;

		flying = true;


		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}
	
	private static final String LEVEL = "level";
	private int timer;
	private Boolean hasadjacent;

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int(3) == 0 ) {
			GameScene.add(Blob.seed(pos, 40, ConfusionGas.class));
		}

		return damage;
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (Random.Int(2) == 0 ) {
			if (enemy.buff(AnkhInvulnerability.class) != null) Buff.detach(enemy, AnkhInvulnerability.class);
		}

		return super.defenseProc(enemy, damage);
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

	{
		immunities.add(Doom.class);
	}


	@Override
	public void die( Object cause ) {
		super.die( cause );
		TrollChild.Quest.complete();
		TrollChild.reward = new Sharanga();
	}
	{
		immunities.add( ConfusionGas.class );
	}


}
