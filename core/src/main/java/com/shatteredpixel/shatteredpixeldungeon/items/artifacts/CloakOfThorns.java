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

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

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
			}
			else if (charge < 1) QuestDone = true;
			}

			return true;
		}

	}



}
