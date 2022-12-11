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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Bundle;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class DeathStick extends DamageWand {

	{
		image = ItemSpriteSheet.DEATHSTICK;

		collisionProperties = Ballistica.PROJECTILE;

		unique = true;
		bones = false;
	}

	public int min(int lvl){
		return (int)(3+lvl*1.5);
	}

	public int max(int lvl){
		return (int)(5+lvl*2.5);
	}
	private int effectamount = 1;
	@Override
	public void onZap(Ballistica bolt) {

		if (this.level() < 3) effectamount = 1;
		else if (this.level() < 8) effectamount = 2;
		if (this.level() < 15) effectamount = 3;
		else effectamount = 4;



		Char ch = Actor.findChar( bolt.collisionPos );
		if (ch != null) {
			ch.damage(damageRoll(), this);
		}

		while (effectamount > 0) {
			int randomwand = Random.Int(11);
			int a;
			Wand w;
			switch (randomwand) {
				case 1:
				default:
					w = new WandOfMagicMissile();
					break;
				case 2:
					w = new WandOfBlastWave();
					break;
				case 3:
					 w = new WandOfCorrosion();
					break;
				case 4:
					w = new WandOfCorruption();
					break;
				case 5:
					w = new WandOfMagicMissile();
					for (int offset : PathFinder.NEIGHBOURS9) {
						if (!Dungeon.level.solid[bolt.collisionPos + offset]) {

							GameScene.add(Blob.seed(bolt.collisionPos + offset, 2 + this.level(), Fire.class));

						}
					}
					break;
				case 6:
					w = new WandOfFrost();
					break;
				case 7:
					w = new WandOfLightning();
					break;
				case 8:
					w = new WandOfLivingEarth();
					break;
				case 9:
					w = new WandOfTransfusion();
					break;
				case 10:
					w = new WandOfWarding();
					break;
				case 11:
					w = new WandOfPrismaticLight();
					break;

			}
			a=((int)this.level()/2)+1;
			while (a>0 && w!=null) {
				w.upgrade();
				a--;
			}
			w.onZap(bolt);

			effectamount--;
		}

	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.DEATHSTICK,
				curUser.sprite,
				bolt.collisionPos,
				callback);

		if (bolt.dist > 10){
			m.setSpeed(bolt.dist*20);
		}
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}


	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

		//acts like unstable
		new DeathStickOnHit().proc( staff, attacker, defender, damage);
	}

	private static class DeathStickOnHit extends Unstable {
		@Override
		protected float procChanceMultiplier(Char attacker) {
			return Wand.procChanceMultiplier(attacker);
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( Random.Int( 0x1000000 ) );
		particle.am = 2f;
		particle.setLifespan(0.5f);
		particle.setSize( 1f, 2f);
		particle.shuffleXY(0.7f);
		float dst = Random.Float(10f);
		particle.x -= dst;
		particle.y += dst;
	}

	protected int initialCharges() {
		return 4;
	}


	}

