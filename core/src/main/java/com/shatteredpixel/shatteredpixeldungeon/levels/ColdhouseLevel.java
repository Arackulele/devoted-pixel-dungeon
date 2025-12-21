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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.ColdhousePainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SageCorpse;
//import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Beast;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;

public class ColdhouseLevel extends RegularLevel {

	{
		color1 = 0x0CDD6A;
		color2 = 0x20A753;
	}

	@Override
	public void playLevelMusic() {
		if (SageCorpse.Quest.active() || Statistics.amuletObtained){
			Music.INSTANCE.play(Assets.Music.PRISON_TENSE, true);
		} else {
			Music.INSTANCE.play(Assets.Music.COLDHOUSE, true);
		}
	}

	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 6;
		//5 to 6, average 5.5
		return 5+Random.chances(new float[]{1, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//1 to 3, average 2.0
		return 1+Random.chances(new float[]{1, 3, 1});
	}
	
	@Override
	protected Painter painter() {
		return new ColdhousePainter()
				.setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	protected void createItems() {

		//TrollChild.Quest.spawn(this);
        addColdhouseVisuals(this);
        buildFlagMaps();
        cleanWalls();

		super.createItems();
	}

	@Override
	protected void createMobs() {
		SageCorpse.Quest.spawnSageCorpse(this, roomEntrance);
		GameScene.updateMap();
		super.createMobs();
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_COLDHOUSE;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_COLDHOUSE;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				ChillingTrap.class, ShockingTrap.class, ToxicTrap.class, BurningTrap.class, PoisonDartTrap.class,
				AlarmTrap.class, OozeTrap.class, GrippingTrap.class,
				ConfusionTrap.class, FlockTrap.class, SummoningTrap.class, TeleportationTrap.class, GatewayTrap.class, GeyserTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				8, 4, 4, 2, 2,
				2, 2, 2,
				1, 1, 1, 1, 1,4 };
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(ColdhouseLevel.class, "water_name");
			case Terrain.STATUE:
				return Messages.get(ColdhouseLevel.class, "statue_name");
			case Terrain.GRASS:
				return Messages.get(ColdhouseLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(ColdhouseLevel.class, "high_grass_name");
			case Terrain.REGION_DECO:
				return Messages.get(PrisonLevel.class, "region_deco_name");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_alt_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {

			case Terrain.WATER:
				return Messages.get(ColdhouseLevel.class, "water_desc");
			case Terrain.STATUE:
				return Messages.get(ColdhouseLevel.class, "statue_desc");
			case Terrain.WALL_DECO:
				return Messages.get(ColdhouseLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(ColdhouseLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
				return Messages.get(PrisonLevel.class, "region_deco_desc");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_alt_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		return visuals;
	}

	public static void addColdhouseVisuals( Level level ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.REGION_DECO_ALT) {
				level.map[i] = Terrain.CHASM;
			}

		}
	}

	@Override
	public void buildFlagMaps() {
		super.buildFlagMaps();
		for (int i=0; i < length(); i++) {
			if (map[i] == Terrain.REGION_DECO || map[i] == Terrain.REGION_DECO_ALT){
				flamable[i] = true;
			}
		}
	}

	@Override
	public void destroy(int pos) {
		//just like sewer barrels, you can burn ice cubes
		int terr = map[pos];
		if (terr == Terrain.REGION_DECO){
			set(pos, Terrain.WATER);
			Splash.at(pos, 0xFF507B5D, 10);
		} else if (terr == Terrain.REGION_DECO_ALT){
			set(pos, Terrain.EMPTY_SP);
			Splash.at(pos, 0xFF507B5D, 10);
		}
		super.destroy(pos);
	}
	

}