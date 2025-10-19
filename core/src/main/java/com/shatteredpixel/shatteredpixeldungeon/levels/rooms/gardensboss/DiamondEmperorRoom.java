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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.gardensboss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.Emperor;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;

public class DiamondEmperorRoom extends StandardRoom {
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		
		Painter.fillEllipse( level, this, 1, Terrain.EMPTY);
		
		for (Room.Door door : connected.values()) {
			door.set( Room.Door.Type.REGULAR );
			Point dir;
			if (door.x == left){
				dir = new Point(1, 0);
			} else if (door.y == top){
				dir = new Point(0, 1);
			} else if (door.x == right){
				dir = new Point(-1, 0);
			} else {
				dir = new Point(0, -1);
			}
			
			Point curr = new Point(door);
			do {
				Painter.set(level, curr, Terrain.EMPTY_SP);
				curr.x += dir.x;
				curr.y += dir.y;
			} while (level.map[level.pointToCell(curr)] == Terrain.WALL);
		}

		//Painter.fill( level, center().x, center().y, 1, 2, Terrain.WALL );
		//Painter.fill( level, center().x, center().y, 1, 1, Terrain.EMPTY_SP );


		Emperor boss = new Emperor();
		boss.pos = level.pointToCell(center());
		level.mobs.add( boss );

		//CustomTilemap vis = new DiamondEmperorRoom.EmperorThrone();
		//vis.pos(center().x, center().y);
		//level.customTiles.add(vis);


	}
	
	@Override
	public boolean canPlaceWater(Point p) {
		return false;
	}

	public static class EmperorThrone extends CustomTilemap {

		{
			texture = Assets.Environment.GARDENS_BOSS;

			tileW = 1;
			tileH = 2;
		}

		private static final int[] layout = new int[]{
				1,
				2
		};

		@Override
		public com.watabou.noosa.Tilemap create() {
			com.watabou.noosa.Tilemap v = super.create();
			v.map(layout, 3);
			return v;
		}

		@Override
		public com.watabou.noosa.Image image(int tileX, int tileY) {
			if ((tileX == 1 && tileY == 0) || tileY == 2){
				return null;
			}
			return super.image(tileX, tileY);
		}
	}

}
