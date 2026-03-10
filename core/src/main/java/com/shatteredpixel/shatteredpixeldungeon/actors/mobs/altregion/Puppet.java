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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Pollen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PuppetSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollRangerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

public class Puppet extends Mob {
	
	{
		spriteClass = PuppetSprite.class;

		HP = HT = 100;
		defenseSkill = 20;
		viewDistance = Light.DISTANCE;

		EXP = 13;
		maxLvl = 26;

		flying = true;

        //The missiles wont be useful, but they will be plentiful
		loot = Random.oneOf(Bomb.class, Generator.Category.MIS_T3);
		lootChance = 0.3f;

		properties.add(Property.INORGANIC);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 30);
	}

	@Override
	public int attackSkill( Char target ) {
		return 30;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 10);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		return damage;
	}

	protected boolean doAttack( Char enemy ) {

		if (level.adjacent( pos, enemy.pos )
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			return super.doAttack( enemy );
		} else {
                counter = 5;
                spend(2f);
            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }

		}
	}

    private int counter = 0;

	private void zap() {

			int goalpos = enemy.pos;
			ArrayList<Integer> possibles = new ArrayList<>();

			int threshold = 25;

			threshold += (distance(enemy)* 10);

			if (Random.Int(100) < threshold) {

				for(int b : PathFinder.NEIGHBOURS8){
					if (!level.solid[goalpos + b]){
						possibles.add(goalpos + b);
						break;
					}
				}
				Random.shuffle(possibles);
				goalpos = possibles.get(0);
			}

			sprite.zap( goalpos );



	}

	public void onZapComplete(int c) {
        Char hitc = Actor.findChar(c);
        if (hitc != null) DealDamageRanged(hitc);
        else Splash.at(c, 0x5bd47b, 3);

        if (counter > 0) {
            zap();
            counter--;
        }
        else next();
	}

    private void DealDamageRanged(Char hitc)
    {

        if (Char.hit(this, hitc, true)) {

            int dmg = Random.NormalIntRange(16, 25);
            dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
			dmg -= hitc.drRoll();
            hitc.damage(dmg, this);
			Sample.INSTANCE.play( Assets.Sounds.HIT_ARROW, 1f, 0.8f );


			if (hitc == Dungeon.hero && !hitc.isAlive()) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "bolt_kill"));
            }
        } else {
            hitc.sprite.showStatus(CharSprite.NEUTRAL, hitc.defenseVerb());
        }

    }

}
