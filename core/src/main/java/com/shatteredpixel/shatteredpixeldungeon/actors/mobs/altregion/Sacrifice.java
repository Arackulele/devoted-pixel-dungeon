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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SacrificeSprite;
import com.watabou.utils.*;

import java.util.ArrayList;

public class Sacrifice extends Mob {

	{
		spriteClass = SacrificeSprite.class;

		HP = HT = 75;
		defenseSkill = 22;
		viewDistance = (int)(Light.DISTANCE * 1.5);

		EXP = 9; //for corrupting
		maxLvl = -2;

		HUNTING = new Hunting();

		baseSpeed = 2f;

		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 10, 15 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 30;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 4);
	}

	public void onZapComplete(){
		if (connection == null || connection.sprite == null || !connection.isAlive()){
			return;
		}

		//heal skeleton first
		if (connection.HP < connection.HT){

            int TotalHeal;
            //Heal a bit more for bulky enemies but dont be as polarizing
            TotalHeal = connection.HT/25 + 10;
            TotalHeal -= Math.min(connection.HP/100, 10);

			connection.HP = Math.min(connection.HP + TotalHeal, connection.HT);
			if (connection.sprite.visible) {
				connection.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString( connection.HT/5 ), FloatingText.HEALING );
			}

			//otherwise give it shielding
		} else if (connection.buff(Barrier.class) == null) {


            Buff.affect(connection, Barrier.class).setShield(20);

        }

		next();
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);
        if (connection != null && !connection.isAlive()) connection = null;
		//ToDo: Different source check
		if (CheckFOV().size() == 0 && connection == null) Buff.affect( this, Terror.class, 10f ).object = Dungeon.hero.id();

	}

	private Mob connection;

	private int storedConnectionID;

	private static final String CONNECTION = "connection";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (connection != null) {
			bundle.put(CONNECTION, connection.id());
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains( CONNECTION )){
			storedConnectionID = bundle.getInt( CONNECTION );
		}
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (connection == null && CheckFOV().size() > 0)
		{
			Mob bestone = null;

			for (Mob m : CheckFOV())
			{

				if (bestone == null) bestone = m;
				else if (bestone.HP < m.HP) bestone = m;

			}

			connection = bestone;
			sprite.attack(bestone.pos);
			int oldpos = pos;
			ScrollOfTeleportation.appear(this, bestone.pos);
			ScrollOfTeleportation.appear(bestone, oldpos);
            connection.state = connection.HUNTING;

			spend(1f);
			return true;

		}
		else return super.doAttack(enemy);
	}

	private ArrayList<Mob> CheckFOV()
	{

		ArrayList<Mob> all = new ArrayList<>();

		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (fieldOfView != null && fieldOfView[mob.pos] && !(mob instanceof Sacrifice) && !Char.hasProp(mob, Char.Property.MINIBOSS) && !Char.hasProp(mob, Property.BOSS) && mob.alignment == this.alignment) {
				all.add(mob);
			}
		}

		return all;

	}

	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (storedConnectionID != -1){
				Actor ch = findById(storedConnectionID);
				storedConnectionID = -1;
					connection = (Mob)ch;
			}

			enemySeen = enemyInFOV;

			if (enemySeen){
				target = enemy.pos;
			}

			if (enemySeen && connection != null && connection.isAlive()){

				spend(TICK);

					//zap skeleton
					if (connection.HP < connection.HT || connection.buff(Adrenaline.class) == null) {
						if ((sprite != null && sprite.visible) || (connection.sprite != null && connection.sprite.visible)){
							sprite.zap(connection.pos);
							return false;
						} else {
							onZapComplete();
						}
					}
				return true;
				}



				return super.act(enemyInFOV, justAlerted);
		}
	}



}
