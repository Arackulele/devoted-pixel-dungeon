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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NeuronSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PuppetSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

public class Neuron extends Mob {
	
	{
		spriteClass = NeuronSprite.class;

        HP = HT = 60;
        defenseSkill = 22;
        viewDistance = Light.DISTANCE;

		EXP = 11;
		maxLvl = 26;

		properties.add(Property.INORGANIC);
	}

    //0: Green ( Posion ), 1: Red ( Explosive ), 2: Blue ( Knockback )
    public int form = 0;

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

			sprite.zap( goalpos );



	}

	public void onZapComplete(int c) {
        spend(1f);
        Char hitc = Actor.findChar(c);
        if (hitc != null) DealDamageRanged(hitc);
        else Splash.at(c, 0x5bd47b, 3);

        targetingPos = c;

        next();

	}

    private void DealDamageRanged(Char hitc)
    {

        if (Char.hit(this, hitc, true)) {

            int dmg = Random.NormalIntRange(4, 11);
            dmg = Math.round(dmg * AscensionChallenge.statModifier(this));

            hitc.damage(dmg, new Banshee.BansheeStare());

            if (hitc == Dungeon.hero && !hitc.isAlive()) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "bolt_kill"));
            }
        } else {
            hitc.sprite.showStatus(CharSprite.NEUTRAL, hitc.defenseVerb());
        }

    }

    private void UpdateSprite()
    {

        switch(form)
        {
            default:
            case 0:
                ((NeuronSprite)sprite).c = 0;
                break;
            case 1:
                ((NeuronSprite)sprite).c = 11;
                break;
            case 2:
                ((NeuronSprite)sprite).c = 22;
                break;
        }

        ((NeuronSprite) sprite).UpdateAnimPos();
        sprite.update();

    }

    private String PHASE = "phase";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, form);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        form = bundle.getInt(PHASE);

    }

    private boolean updatedInitial = false;

    private int targetingPos = -1;

    @Override
    protected boolean act() {

        if (!updatedInitial) {
            UpdateSprite();
            updatedInitial = true;
        }

        if (targetingPos != -1)
        {
            Class Blobtype = null;


            if (form == 0) 	Blobtype = Fire.class;
            if (form == 1) 	Blobtype = Freezing.class;
            if (form == 2) 	Blobtype = Electricity.class;


            GameScene.add(Blob.seed(targetingPos , 5, Blobtype));
            for (int i : PathFinder.NEIGHBOURS4) {
                GameScene.add(Blob.seed(i+targetingPos , 2, Blobtype));
            }
            targetingPos = -1;

            form++;
            if (form > 2) form = 0;

            UpdateSprite();

            spend(TICK);
            return true;

        }

    return super.act();
    }

    @Override
    public String description() {
        String desc = super.description();
        if (form == 0) desc += "\n\n" + Messages.get(this, "fire");
        if (form == 1) desc += "\n\n" + Messages.get(this, "ice");
        if (form == 2) desc += "\n\n" + Messages.get(this, "elec");

        return desc;
    }

}
