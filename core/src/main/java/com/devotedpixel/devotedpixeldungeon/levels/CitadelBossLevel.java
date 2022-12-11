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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thymor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yuria;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wendar;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BlastFurnace;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CitadelPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;

import java.util.ArrayList;

public class CitadelBossLevel extends Level {


	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public void playLevelMusic() {
		if (locked){
			Music.INSTANCE.play(Assets.Music.CITY_BOSS, true);
		//if wall isn't broken
		} else if (map[14 + 13*width()] == Terrain.SIGN){
			Music.INSTANCE.end();
		} else {
			Music.INSTANCE.playTracks(
					new String[]{Assets.Music.CITY_1, Assets.Music.CITY_2, Assets.Music.CITY_2},
					new float[]{1, 1, 0.5f},
					false);
		}
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CITADEL;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CITADEL;
	}

	private static int WIDTH = 33;
	private static int HEIGHT = 42;

	public static Rect mainArena = new Rect(4, 14, 29, 37);
	public static Rect spawnArena = new Rect(8, 20, 26, 31);
	public static Rect gate = new Rect(14, 13, 19, 14);

	public static Rect impsquare = new Rect(20, 5, 26, 11);

	public static Rect barrier = new Rect(0, 13, 33, 14);

	public static Rect full = new Rect(0, 0, 33, 42);

	private ImpShopRoom impShop;

	private ArenaVisuals customArenaVisuals;
	private static final String BOSS = "boss";

	@Override
	protected boolean build() {

		boss = null;
		killed = false;

		setSize(WIDTH, HEIGHT);

		Painter.fill(this, full, Terrain.CHASM);

		Painter.fill(this, barrier, Terrain.WALL);

		//These signs are visually overridden with custom tile visuals
		Painter.fill(this, gate, Terrain.SIGN);

		//set up main boss arena
		Painter.fillDiamond(this, mainArena, Terrain.EMPTY);




		buildEntrance();
		//buildCorners();

		new CitadelPainter().paint(this, null);

		//setup exit area above main boss arena
		Painter.fill(this, 0, 3, width(), 4, Terrain.CHASM);
		Painter.fill(this, 6, 7, 21, 1, Terrain.CHASM);
		Painter.fill(this, 10, 8, 13, 1, Terrain.CHASM);
		Painter.fill(this, 12, 9, 9, 1, Terrain.CHASM);
		Painter.fill(this, 13, 10, 7, 1, Terrain.CHASM);
		Painter.fill(this, 14, 3, 5, 10, Terrain.EMPTY);

		//fill in special floor, statues, and exits
		Painter.fill(this, 15, 2, 3, 3, Terrain.EMPTY_SP);
		Painter.fill(this, 16, 5, 1, 6, Terrain.EMPTY_SP);
		Painter.fill(this, 15, 0, 3, 3, Terrain.EXIT);

		Painter.fill(this, impsquare, Terrain.EMPTY);

		impShop = new ImpShopRoom();
		impShop.set(impsquare);
		Painter.set(this, impShop.center(), Terrain.PEDESTAL);
		Painter.set(this, 19, 7 , Terrain.EMPTY);



		int exitCell = 16 + 2*width();
		LevelTransition exit = new LevelTransition(this, exitCell, LevelTransition.Type.REGULAR_EXIT);
		exit.set(14, 0, 18, 2);
		transitions.add(exit);

		CustomTilemap customVisuals = new CityEntrance();
		customVisuals.setRect(0, 0, width(), 11);
		customTiles.add(customVisuals);

		customVisuals = new EntranceOverhang();
		customVisuals.setRect(0, 0, width(), 11);
		customWalls.add(customVisuals);

		customVisuals = customArenaVisuals = new ArenaVisuals();
		customVisuals.setRect(0, 12, width(), 27);
		customTiles.add(customVisuals);





		return true;

	}

	private void spawnShop(){
		while (impShop.itemCount() >= 7*(impShop.height()-2)){
			impShop.bottom++;
		}
		impShop.spawnShop(this);
	}

	private static final String YURIA = "YURIA";

	private static final String WENDAR = "WENDAR";

	private static final String IMP_SHOP = "IMP_SHOP";

	private static final String KILLED = "KILLED";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );
		bundle.put( IMP_SHOP, impShop );
		bundle.put( KILLED, killed );
		bundle.put( BOSS, boss );
		bundle.put( YURIA, yuria );
		bundle.put( WENDAR, wendar );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		boss = (Mob)bundle.get( BOSS );
		BossHealthBar.assignBoss(boss);

		wendar = (Mob)bundle.get( WENDAR );
		yuria = (Mob)bundle.get( YURIA );

		killed = bundle.getBoolean( KILLED );

		impShop = (ImpShopRoom) bundle.get( IMP_SHOP );
		if (Imp.Quest.isCompleted() && !impShop.shopSpawned()){
			spawnShop();
		}


		//pre-1.3.0 saves, modifies exit transition with custom size
		if (bundle.contains("exit")){
			LevelTransition exit = getTransition(LevelTransition.Type.REGULAR_EXIT);
			exit.set(14, 0, 18, 2);
			transitions.add(exit);
		}

		for (CustomTilemap c : customTiles){
			if (c instanceof ArenaVisuals){
				customArenaVisuals = (ArenaVisuals) c;
			}
		}
	}


	@Override
	protected void createMobs() {

	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = randomRespawnCell(null);
			} while (pos == entrance());
			drop( item, pos ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}

	}

	@Override
	public int randomRespawnCell( Char ch ) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = entrance() + i;
			if (passable[cell]
					&& Actor.findChar(cell) == null
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])){
				candidates.add(cell);
			}
		}

		if (candidates.isEmpty()){
			return -1;
		} else {
			return Random.element(candidates);
		}
	}

	@Override
	public boolean setCellToWater(boolean includeTraps, int cell) {

		return super.setCellToWater(includeTraps, cell);
	}

	@Override
	public void occupyCell(Char ch) {


		super.occupyCell( ch );
		int gatePos = pointToCell(new Point(gate.left, gate.top));
		if (ch == Dungeon.hero && Dungeon.level.distance(ch.pos, entrance()) >= 4 && !killed) {
			seal();
		}

	}

	public static Mob boss;
	public static Mob yuria;
	public static Mob wendar;

	@Override
	public void seal() {
		if (!locked) {
			super.seal();
			Statistics.qualifiedForBossChallengeBadge = true;

			int entrance = entrance();
			set(entrance, Terrain.WALL);
			set(entrance + (WIDTH*2)+ 0, Terrain.WALL);
			set(entrance - (WIDTH*2)+ 0, Terrain.WALL);
			set(entrance + 2, Terrain.WALL);
			set(entrance - 2, Terrain.WALL);

			GameScene.updateMap();

			Heap heap = Dungeon.level.heaps.get(entrance);
			if (heap != null) {
				int n;
				do {
					n = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
				} while (!Dungeon.level.passable[n]);
				Dungeon.level.drop(heap.pickUp(), n).sprite.drop(entrance);
			}


			Char ch = Actor.findChar(entrance);
			if (ch != null) {
				int n;
				do {
					n = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
				} while (!Dungeon.level.passable[n]);
				ch.pos = n;
				ch.sprite.place(n);
			}

			GameScene.updateMap(entrance);
			Dungeon.observe();

			CellEmitter.get(entrance).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Camera.main.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);


			boss = new Thymor();
			yuria = new Yuria();
			wendar = new Wendar();
			boss.state = boss.WANDERING;
			boss.yell(Messages.get(Thymor.class, "notice"));
			yuria.state = yuria.WANDERING;
			wendar.state = wendar.WANDERING;
			BossHealthBar.assignBoss(boss);
			do {
				boss.pos = pointToCell(Random.element(spawnArena.getPoints()));
			} while (!openSpace[boss.pos] || map[boss.pos] == Terrain.EMPTY && map[boss.pos] == Terrain.CHASM || Actor.findChar(boss.pos) != null);
			GameScene.add(boss);



			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.play(Assets.Music.CITY_BOSS, true);
				}
			});
		}
	}

	public void wendarappearance()
	{
		wendar.yell(Messages.get(Wendar.class, "notice"));
		do {
			wendar.pos = pointToCell(Random.element(spawnArena.getPoints()));
		} while (!openSpace[wendar.pos] || map[wendar.pos] == Terrain.EMPTY && map[wendar.pos] == Terrain.CHASM  || Actor.findChar(wendar.pos) != null);
		GameScene.add(wendar);

	}

	public void yuriaappearance()
	{
		yuria.yell(Messages.get(Yuria.class, "notice"));
		do {
			yuria.pos = pointToCell(Random.element(spawnArena.getPoints()));
		} while (!openSpace[yuria.pos] || map[yuria.pos] == Terrain.EMPTY && map[yuria.pos] == Terrain.CHASM || Actor.findChar(yuria.pos) != null);
		GameScene.add(yuria);


	}

	private static boolean killed = false;


	@Override
	public void unseal() {
		super.unseal();
		killed = true;

		set( entrance(), Terrain.ENTRANCE );

		set(entrance() + (WIDTH*2)+ 0, Terrain.DOOR);
		set(entrance() - (WIDTH*2)+ 0, Terrain.DOOR);
		set(entrance() + 2, Terrain.DOOR);
		set(entrance() - 2, Terrain.DOOR);

		int i = 14 + 13*width();
		for (int j = 0; j < 5; j++){
			set( i+j, Terrain.EMPTY );
			if (Dungeon.level.heroFOV[i+j]){
				CellEmitter.get(i+j).burst(BlastParticle.FACTORY, 10);
			}
		}
		GameScene.updateMap();

		if (customArenaVisuals != null) customArenaVisuals.updateState();

		Dungeon.observe();


		if (Imp.Quest.isCompleted()) {
			spawnShop();
		}

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				Music.INSTANCE.end();
			}
		});

	}


	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Messages.get(CitadelLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CitadelLevel.class, "high_grass_name");
			case Terrain.WATER:
				return Messages.get(CitadelLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return super.tileDesc( tile ) + "\n\n" + Messages.get(ForgeLevel.class, "water_desc");
			case Terrain.ENTRANCE:
				return Messages.get(CitadelLevel.class, "entrance_desc");
			case Terrain.EXIT:
				//city exit is used
				return Messages.get(CitadelLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(CitadelLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return Messages.get(CitadelLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CitadelLevel.class, "bookshelf_desc");

			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		//CitadelL.addForgeVisuals(this, visuals);
		return visuals;
	}

	/**
	 * semi-randomized setup for entrance and corners
	 */

	private static final short h = -1; //used when a tile shouldn't be changed
	private static final short W = Terrain.WALL;

	private static final short A = Terrain.STATUE;

	private static final short c = Terrain.CHASM;
	private static final short D = Terrain.DOOR;
	private static final short e = Terrain.EMPTY;

	private static final short n = Terrain.BOOKSHELF;

	private static short[] entrance1 = {
			c, c, W, e, e, W, W, W,
			c, c, e, A, e, e, e, e,
			W, e, e, e, e, e, e, e,
			e, A, e, e, e, e, e, e,
			e, e, e, e, e, e, e, e,
			W, e, e, e, e, W, W, D,
			W, e, e, e, A, W, e, e,
			W, e, e, e, e, D, e, e,
	};


	private static short[][] entranceVariants = {
			entrance1
	};

	private void buildEntrance(){
		int entrance = 16 + 25*width();

		//entrance area
		int NW = entrance - 7 - 7*width();
		int NE = entrance + 7 - 7*width();
		int SE = entrance + 7 + 7*width();
		int SW = entrance - 7 + 7*width();

		short[] entranceTiles = Random.oneOf(entranceVariants);
		for (int i = 0; i < entranceTiles.length; i++){
			if (i % 8 == 0 && i != 0){
				NW += (width() - 8);
				NE += (width() + 8);
				SE -= (width() - 8);
				SW -= (width() + 8);
			}

			if (entranceTiles[i] != n) map[NW] = map[NE] = map[SE] = map[SW] = entranceTiles[i];
			NW++; NE--; SW++; SE--;
		}

		Painter.set(this, entrance, Terrain.ENTRANCE);
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
	}



	/**
	 * Visual Effects
	 */

	public static class CityEntrance extends CustomTilemap{

		{
			texture = Assets.Environment.CITADEL_BOSS;
		}

		private static short[] entryWay = new short[]{
				-1,  7,  7,  7, -1,
				-1,  1,  2,  3, -1,
				 8,  1,  2,  3, 12,
				16,  9, 10, 11, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				24, 25, 26, 27, 28
		};

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			int entryPos = 0;
			for (int i = 0; i < data.length; i++){

				//override the entryway
				if (i % tileW == tileW/2 - 2){
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i] = entryWay[entryPos++];

				//otherwise check if we are on row 2 or 3, in which case we need to override walls
				} else {
					if (i / tileW == 2) data[i] = 13;
					else if (i / tileW == 3) data[i] = 21;
					else data[i] = -1;
				}
			}
			v.map( data, tileW );
			return v;
		}

	}

	public static class EntranceOverhang extends CustomTilemap{

		{
			texture = Assets.Environment.CITADEL_BOSS;
		}

		private static short[] entryWay = new short[]{
				 0,  7,  7,  7,  4,
				 0, 15, 15, 15,  4,
				-1, 23, 23, 23, -1,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1,
		};

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			int entryPos = 0;
			for (int i = 0; i < data.length; i++){

				//copy over this row of the entryway
				if (i % tileW == tileW/2 - 2){
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i] = entryWay[entryPos++];
				} else {
					data[i] = -1;
				}
			}
			v.map( data, tileW );
			return v;
		}

	}

	public static class ArenaVisuals extends CustomTilemap {

		{
			texture = Assets.Environment.CITADEL_BOSS;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			updateState();

			return v;
		}

		public void updateState() {
			if (vis != null) {
				int[] data = new int[tileW * tileH];
				int j = Dungeon.level.width() * tileY;
				for (int i = 0; i < data.length; i++) {

			if (Dungeon.level.map[j] == Terrain.INACTIVE_TRAP) {
						data[i] = 37;
					} else if (gate.inside(Dungeon.level.cellToPoint(j))) {
						int idx = Dungeon.level.solid[j] ? 40 : 32;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i] = idx;
						j += 4;
					} else {
						data[i] = -1;
					}

					j++;
				}
				vis.map(data, tileW);
			}
		}

		@Override
		public String name(int tileX, int tileY) {
			int i = tileX + tileW * (tileY + this.tileY);
			if (Dungeon.level.map[i] == Terrain.INACTIVE_TRAP) {
				return Messages.get(CavesBossLevel.class, "wires_name");
			} else if (gate.inside(Dungeon.level.cellToPoint(i))) {
				return Messages.get(ForgeBossLevel.class, "barrier_name");
			}

			return super.name(tileX, tileY);
		}

		@Override
		public String desc(int tileX, int tileY) {
			int i = tileX + tileW * (tileY + this.tileY);
			if (Dungeon.level.map[i] == Terrain.INACTIVE_TRAP) {
				return Messages.get(CavesBossLevel.class, "wires_desc");
			} else if (gate.inside(Dungeon.level.cellToPoint(i))) {
				if (Dungeon.level.solid[i]) {
					return Messages.get(ForgeBossLevel.class, "barrier_desc");
				} else {
					return Messages.get(ForgeBossLevel.class, "barrier_desc_broken");
				}
			}
			return super.desc(tileX, tileY);
		}

	}
}
