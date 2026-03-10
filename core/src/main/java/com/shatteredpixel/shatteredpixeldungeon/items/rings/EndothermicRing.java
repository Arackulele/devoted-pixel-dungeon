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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;

public class EndothermicRing extends Ring {

	{
		image = ItemSpriteSheet.RING_ENDOTHERMIC;
		buffClass = Endothermic.class;

		unique = true;
		bones = false;
	}

    public int totalLeft = 40;

	public String TOTALLEFT = "totalleft";

	public int timer = 16;

	public String TIMER = "timer";

	public boolean QuestDone = false;

	public String QUESTDONE = "QuestDone";

	@Override
	public boolean isUpgradable() {
		return false;
	}
	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	public String statsInfo() {
			String info = Messages.get(this, "stats",
					Integer.toString(totalLeft));

			return info;
	}

	@Override
	public void storeInBundle(com.watabou.utils.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIMER, timer);
		bundle.put(TOTALLEFT, totalLeft);
		bundle.put(QUESTDONE, QuestDone);
	}

	@Override
	public void restoreFromBundle( com.watabou.utils.Bundle bundle ) {
		timer = bundle.getInt(TIMER);
		totalLeft = bundle.getInt(TOTALLEFT);
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
	protected RingBuff buff( ) {
		return new Endothermic();
	}
	public class Endothermic extends RingBuff {
		@Override
		public boolean act() {
			if (!QuestDone)
			{
				if (Dungeon.level.map[target.pos] == Terrain.WATER) {
					Splash.at(DungeonTilemap.tileCenterToWorld(Dungeon.hero.pos), -com.watabou.utils.PointF.PI / 2, com.watabou.utils.PointF.PI / 2, 0x5bc1e3, 3, 0.02f);
                    for (int i : PathFinder.NEIGHBOURS5) {
                        if (Dungeon.level.map[target.pos + i] == Terrain.WATER) {
                            Dungeon.level.set(target.pos + i, Terrain.EMPTY);
                            GameScene.updateMap(target.pos + i);
                            Dungeon.level.discover(target.pos + i);
                        }
                    }

					totalLeft--;
					timer = 14;
				}

                if (Dungeon.level.map[target.pos] == Terrain.EMBERS) {

                    for (int i : PathFinder.NEIGHBOURS5) {
                        GameScene.add(Blob.seed(target.pos + i, 4, Fire.class));
                    }

                } else if (timer < 1) {

				Buff.affect(Dungeon.hero, Burning.class).reignite(Dungeon.hero, 2f);

			}

			if (timer > -1) timer--;

			if (totalLeft < 1) QuestDone = true;
			}


			return super.act();
		}

	}
}
