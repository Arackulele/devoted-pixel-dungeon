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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LavaPiranhaSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.watabou.utils.BArray;
import com.watabou.utils.Random;

public class LavaPiranha extends Piranha {

	{
		spriteClass = LavaPiranhaSprite.class;

		baseSpeed = 2f;

		EXP = 7;
		maxLvl = -2;

		HP = HT = 30;
		defenseSkill = 16;

		state = SLEEPING;

		immunities.add(Burning.class);

	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 5, 18 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 24;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, Dungeon.depth);
	}

	@Override
	protected boolean getCloser( int target ) {

		if (rooted) {
			return false;
		}

		int step = Dungeon.findStep( this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true );
		int stepregular = Dungeon.findStep( this, target, BArray.and(Dungeon.level.passable, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else if (stepregular != -1){
			sprite.attack(stepregular);
			if (Dungeon.level.heroFOV[stepregular])com.watabou.noosa.audio.Sample.INSTANCE.play(Assets.Sounds.READ, 1, 0.5f);

			this.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "bite"));
			Level.set(stepregular, Terrain.WATER);
			GameScene.updateMap(stepregular);
			Buff.prolong(this, Paralysis.class, 2f);
			return true;
		}
		else return false;

	}


}
