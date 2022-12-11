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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EvilMageSprite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Excalibur;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DeathStick;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SoulgemRing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class EvilMage extends Mob {

	private static final float SPAWN_DELAY	= 2f;

	
	private int level;
	
	{
		spriteClass = EvilMageSprite.class;

		HP = HT = Dungeon.hero.lvl*25;
		EXP = 0;

		flying = true;

		state = WANDERING;

		properties.add(Property.BOSS);
		properties.add(Property.DEMONIC);
		properties.add(Property.UNDEAD);
	}
	
	private static final String LEVEL = "level";
	private static String FOCUS_COOLDOWN = "focus_cooldown";
	private static final float TIME_TO_ZAP	= 1f;
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.hero.lvl*2, Dungeon.hero.lvl*4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10 + level;
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
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {

			return super.doAttack( enemy );

		} else {

			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}

	protected void zap() {
		spend( TIME_TO_ZAP );

		if (hit( this, enemy, true )) {
			//TODO would be nice for this to work on ghost/statues too
			if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
				Buff.prolong( enemy, Hex.class, 5f );
				Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}

			int dmg = Random.NormalIntRange( Dungeon.hero.lvl/2, Dungeon.hero.lvl );
			enemy.damage( dmg, new DarkBolt() );

			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Badges.validateDeathFromEnemyMagic();
				Dungeon.fail( getClass() );
				//GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}
	protected float focusCooldown = 0;

	@Override
	public void damage( int dmg, Object src ) {


		if (buff(Absorb.class) != null) {
			Absorb f = buff(Absorb.class);
		Buff.affect(this, Healing.class).setHeal(dmg*2,0.75f,0);
		f.detach();
	}
		super.damage( dmg, src );
	}







	@Override
	protected boolean act() {
		boolean result = super.act();
		if (buff(Absorb.class) == null && state == HUNTING && focusCooldown <= 0) {
			Buff.affect( this, Absorb.class );
			focusCooldown = Random.NormalFloat( 6, 7 );
		}
		return result;
	}

	@Override
	protected void spend( float time ) {
		focusCooldown -= time;
		super.spend( time );
	}


	@Override
	public void die( Object cause ) {
		super.die( cause );
		TrollChild.Quest.complete();
		TrollChild.reward = new DeathStick();
	}

	{
		immunities.add(Doom.class);
	}

	public static class Absorb extends Buff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.MIND_VISION;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.25f, 1.5f, 1f);
		}
	}

}


