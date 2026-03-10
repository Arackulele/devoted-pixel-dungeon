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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EquipmentDisabled;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BedrockGolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RockGolemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class BedrockGolem extends Mob {

	{
		spriteClass = BedrockGolemSprite.class;

		HP = HT = 200;
		defenseSkill = 11;

		EXP = 12;
		maxLvl = 25;

        loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR, WildEnergy.class);
		lootChance = 0.125f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 22, 27 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 25;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 9);
	}

	public int DashActive = 0;

	public Ballistica route;

	public int counter = 0;

	@Override
	protected boolean act() {

		if (DashActive == 1)
		{
			this.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "charged"));
			DashActive = 2;
			spend(1f);
			return true;
		}
		else if (DashActive == 2)
		{
			counter = 0;

			route = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_SOLID );

			DashTile();


			DashActive = 0;
            return false;
		}
		else return super.act();

	}

	public void DashTile()
	{

		if (counter <= route.dist)
		{
			if (Dungeon.level.map[pos] == Terrain.CHASM) {
				next();
				return;
			}

			((BedrockGolemSprite)sprite).DashATile(route.path.get(counter));
			counter++;
		}
		else {

			for (int i = 0; i < com.watabou.utils.PathFinder.NEIGHBOURS8.length; i++) {
				PixelScene.shake(1.1f, 0.3f);
				CellEmitter.get(pos + com.watabou.utils.PathFinder.NEIGHBOURS8[i]).burst(ShadowParticle.UP, 5);
				Char ch = Actor.findChar( pos + com.watabou.utils.PathFinder.NEIGHBOURS8[i] );
				if (ch != null && ch.isAlive()) {
					Buff.affect(ch, EquipmentDisabled.class);
				}

			}

			spend(2f);
			next();
		}

	}

    private int DashesPerformed = 0;

    @Override
	public void damage(int dmg, Object src) {
		if (HP - dmg < HT * 0.75f && HP > HT * 0.75f && DashesPerformed == 0) {
            DashActive = 1;
            DashesPerformed++;
        }
		if (HP - dmg < HT * 0.5f && HP > HT * 0.5f&& DashesPerformed == 1 && DashActive == 0) {
            DashActive = 1;
            DashesPerformed++;
        }
		if (HP - dmg < HT * 0.25f && HP > HT * 0.25f && DashesPerformed == 2 && DashActive == 0) {
            DashActive = 1;
            DashesPerformed++;
        }

		//if (DashActive == 1) dmg = 0;

		super.damage(dmg, src);
	}


	private final String DASHACTIVE = "dashactive";

	private final String DASHAMOUNT = "dashamount";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DASHACTIVE, DashActive);
		bundle.put(DASHAMOUNT, DashesPerformed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		DashActive = bundle.getInt(DASHACTIVE);
		DashesPerformed = bundle.getInt(DASHAMOUNT);
	}

    public void PushAway(int pos)
    {

        if (Actor.findChar(pos) != null) {

            int pushPos = pos;
            for (int c : PathFinder.NEIGHBOURS8) {
                if (findChar(pos + c) == null
                        && Dungeon.level.passable[pos + c]
                        && (Dungeon.level.openSpace[pos + c] || !hasProp(findChar(pos), Property.LARGE))
                        && Dungeon.level.trueDistance(pos, pos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
                    pushPos = pos + c;
                }
            }


            //push enemy, or wait a turn if there is no valid pushing position
            if (pushPos != pos) {
                Char ch = findChar(pos);
                Actor.add(new Pushing(ch, ch.pos, pushPos));

                ch.pos = pushPos;
                Dungeon.level.occupyCell(ch);


            }
        }


    }

}
