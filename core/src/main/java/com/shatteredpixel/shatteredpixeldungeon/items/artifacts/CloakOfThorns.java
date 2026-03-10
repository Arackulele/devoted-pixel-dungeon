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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.ThornLasher;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class CloakOfThorns extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_THORNCLOAK;

		levelCap = 0;

		charge = chargeCap = 200;

		//defaultAction = AC_ROOT;
	}

	public boolean QuestDone = false;

	public String QUESTDONE = "QuestDone";

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(QUESTDONE, QuestDone);
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		QuestDone = bundle.getBoolean(QUESTDONE);
		super.restoreFromBundle(bundle);
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if (QuestDone){
			return new ItemSprite.Glowing( 0xFFFF00 );
		}
		return null;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thorny();
	}

	public class Thorny extends Artifact.ArtifactBuff {
		@Override
		public boolean act() {
			spend( TICK );
			if (!QuestDone)
			{
			if (Dungeon.level.map[target.pos] != Terrain.SOLID && charge > 0 && Blob.volumeAt(target.pos, Thorns.class) == 0) {
				GameScene.add(Blob.seed(target.pos, 25, Thorns.class));
				charge-= 1f;
				Item.updateQuickslot();

                boolean visible = false;

                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (Dungeon.level.heroFOV[mob.pos]) {
                        visible = true;
                    }
                }

                if (!visible && Random.IntRange(0, 10) == 7) {
                    ThornLasher w = new ThornLasher();

                    int p = -1;
                    for (int i : PathFinder.NEIGHBOURS8)
                    {
                        if (p == -1 && Actor.findChar(i + target.pos) == null && Dungeon.level.passable[i + target.pos] ) p = i + target.pos;

                    }
                    if (p != -1) {
                        w.pos = p;
                        w.state = w.HUNTING;
                        GameScene.add(w, 3f);
                        Dungeon.level.occupyCell(w);

                        w.sprite.alpha(0);
                        w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

                        w.sprite.emitter().burst(LeafParticle.GENERAL, 5);
                    }
                }

            }
			else if (charge < 1) QuestDone = true;
			}

			return true;
		}

	}



}
