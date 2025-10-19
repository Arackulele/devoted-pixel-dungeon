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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.ForgePainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.RockBlock;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BountyHunterSprite;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ForgeLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.FORGE, true);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		return BountyHunter.Quest.spawn(super.initRooms());
	}
	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 7;
		//6 to 7, average 6.333
		return 6+Random.chances(new float[]{2, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//2 to 3, average 2.2
		return 2+Random.chances(new float[]{4, 1});
	}
	
	@Override
	protected Painter painter() {
		return new ForgePainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
				.setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_FORGE;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_FORGE;
	}

	@Override
	protected void createItems() {

		//TrollChild.Quest.spawn(this);
		addForgeVisuals(this);
		buildFlagMaps();
		cleanWalls();

		super.createItems();
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class,
				GrippingTrap.class, RockfallTrap.class,  GuardianTrap.class,
				ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class, GatewayTrap.class, GeyserTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				8, 2, 2, 2, 8,
				2, 2, 2,
				1, 1, 1, 1, 1, 1 };
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Messages.get(ForgeLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(ForgeLevel.class, "high_grass_name");
			case Terrain.WATER:
				return Messages.get(ForgeLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(ForgeLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(ForgeLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(ForgeLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return Messages.get(ForgeLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(ForgeLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		return visuals;
	}
	
	public static void addForgeVisuals( Level level ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.REGION_DECO_ALT) {

				for(int b : com.watabou.utils.PathFinder.NEIGHBOURS8){
					if (level.map[i + b] == Terrain.REGION_DECO_ALT){
						level.map[i] = Terrain.EMPTY_SP;
						break;
					}
				}

			}
		}
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_EXIT
				&& (!BountyHunter.Quest.given() || BountyHunter.Quest.completed() || !BountyHunter.Quest.started())) {

			BountyHunter smith = null;
			for (Char c : Actor.chars()){
				if (c instanceof BountyHunter){
					smith = (BountyHunter) c;
					break;
				}
			}

			if (smith == null || !BountyHunter.Quest.given() || BountyHunter.Quest.completed()) {
				GLog.w(Messages.get(BountyHunter.class, "entrance_blocked"));
			} else {
				final RockBlock pick = hero.belongings.getItem(RockBlock.class);
				com.watabou.noosa.Game.runOnRenderThread(new com.watabou.utils.Callback() {
					@Override
					public void call() {
						if (pick == null){
							GameScene.show( new WndTitledMessage(new BountyHunterSprite(),
									Messages.titleCase(Messages.get(BountyHunter.class, "name")),
									Messages.get(BountyHunter.class, "lost_pick"))
							);
						} else {
							GameScene.show( new WndOptions( new BountyHunterSprite(),
									Messages.titleCase(Messages.get(BountyHunter.class, "name")),
									Messages.get(BountyHunter.class, "quest_start_prompt"),
									Messages.get(BountyHunter.class, "enter_yes"),
									Messages.get(BountyHunter.class, "enter_no")){
								@Override
								protected void onSelect(int index) {
									if (index == 0){
										BountyHunter.Quest.start();
										ForgeLevel.super.activateTransition(hero, transition);
									}
								}
							} );
						}

					}
				});
			}
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}


}