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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.InfusionCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollExile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GiantSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Giant extends Mob {
	
	{
		spriteClass = GiantSprite.class;
		
		HP = HT = 100;
		defenseSkill = 9;
		
		EXP = 12;
		maxLvl = 22;

		PASSIVE = new Passive();

		state = PASSIVE;

		loot = Random.oneOf(Generator.Category.STONE, Generator.Category.GOLD);
		lootChance = 0.33f;
		//properties.add(Property.LARGE);

	}

	private float cleansemeter = 0;

	public static boolean Guilty = false;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 30 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(1, 12);
	}

	public void damage(int dmg, Object src) {

        //Cleanse is built up more by hitting the giant repeatedly instead of one strong hit
		cleansemeter+= (dmg * 0.3f) + 2;

        //Cleanse is charged a whole lot tho when you deal a ton of damage at once
        if (dmg > (HP * 0.5) ) cleansemeter += 10;

		super.damage(dmg, src);
	}

    @Override
    public boolean act() {

        //Cleanse/Anger meter goes down over time if not fighting, maybe encourages some unique strategy
        if (cleansemeter > 0) cleansemeter-= 0.1f;

        if (enemy != null && Dungeon.level.distance(enemy.pos, pos) < 4 && cleansemeter > 15 && this.state != PASSIVE) {
            cleansemeter = 0;

            sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.4f, 3 );
			this.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "cleanse") );

			//ToDO: Perhaps add Choir/Chanting Sound effect
            Sample.INSTANCE.play( Assets.Sounds.GOLD );

			Ballistica aim = new Ballistica(pos, target, Ballistica.WONT_STOP);

			ConeAOE cone = new ConeAOE(aim,
					5f,
					360,
					Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);

			sprite.zap(pos);
			spend(1f);

			PotionOfHealing.cure(this);

			for (int cell : cone.cells) {

				CellEmitter.get(cell).burst(Speck.factory(Speck.BUBBLE), 2);

				Char f = Actor.findChar(cell);
				if (f != null && f.alignment != alignment) {

					for (Buff b : f.buffs()){
						if (b.type == Buff.buffType.POSITIVE
                                && (b instanceof FlavourBuff  || b instanceof Healing)
                                && !(b instanceof InfusionCD)

                        ){
							f.remove(b);
							return true;
						}
					}
				}
			}


            spend( Actor.TICK );
            return true;

        }
        else return super.act();
    }

	//judges also wander around while passive
	private class Passive extends Mob.Wandering {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			//im sorry this is a lot of for loops
			for (Buff b : buffs()){
				if (b.type == Buff.buffType.NEGATIVE){
					//swap to aggro if we've been debuffed
					state = WANDERING;
					return true;
				}
			}

			for(Mob m : Dungeon.level.mobs){
				//if another giant has deemed you guilty, or the giant sees that a friendly mob has been hurt, it will also deem you guilty
				if ((fieldOfView[m.pos] && m instanceof Giant && m.state == HUNTING) || (fieldOfView[m.pos] && m.alignment == alignment && m.HP < m.HT - 10))
				{
					state = WANDERING;
					return true;
				}

			}

			if (enemyInFOV && justAlerted) {

				return noticeEnemy();

			}
			else return continueWandering();
		}
	}

	private static final String ROCKFALL = "ROCKFALL";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ROCKFALL, cleansemeter);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        cleansemeter = bundle.getFloat( ROCKFALL );
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		super.rollToDropLoot();
	}

	@Override
	public String description() {
		String desc = super.description();
		if (state == PASSIVE){
			desc += "\n\n" + Messages.get(this, "innocent");
		} else {
			desc += "\n\n" + Messages.get(this, "guilty");
		}
		return desc;
	}

}
