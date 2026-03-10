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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Ruby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.MiningLevelPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BountyHunter;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.RockBlock;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BountyHunterSprite;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LavaLakeLevel extends ForgeLevel {

	@Override
	public String tilesTex() {
		switch (BountyHunter.Quest.Type()){
			default:
				return Assets.Environment.TILES_FORGE;
			case BountyHunter.Quest.CRYSTAL:
				return Assets.Environment.TILES_FORGE;
			case BountyHunter.Quest.GNOLL:
				return Assets.Environment.TILES_FORGE;
		}

	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.CAVES_TENSE, true);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		initRooms.add ( roomEntrance = new MineEntrance());

		//spawns 1 giant, 3 large, 6-8 small, and 1-2 secret cave rooms
		StandardRoom s;
		s = new MineGiantRoom();
		s.setSizeCat();
		initRooms.add(s);

		int rooms = 4;
		for (int i = 0; i < rooms; i++){
			s = new MineLargeRoom();
			s.setSizeCat();
			initRooms.add(s);
		}

		rooms = Random.NormalIntRange(4, 6);
		for (int i = 0; i < rooms; i++){
			s = new MineSmallRoom();
			s.setSizeCat();
			initRooms.add(s);
		}

		rooms = Random.NormalIntRange(1, 2);
		for (int i = 0; i < rooms; i++){
			initRooms.add(new MineSecretRoom());
		}

		return initRooms;
	}

	@Override
	protected Painter painter() {
		return new MiningLevelPainter()
				.setGold(Random.NormalIntRange(42, 46))
				.setWater(0.1f, 6)
				.setGrass(0.1f, 3);
	}

	@Override
	public int mobLimit() {
		//1 fewer than usual
		return super.mobLimit()+5;
	}

	@Override
	public Mob createMob() {
		switch (BountyHunter.Quest.Type()){
			case BountyHunter.Quest.CRYSTAL:
				return new LavaPiranha();
			default:
			case BountyHunter.Quest.GNOLL:
				return new Pyronaut();
		}
	}

	@Override
	public float respawnCooldown() {
        //normal enemies respawn much more slowly here
		return 2*TIME_TO_RESPAWN;
	}

	@Override
	protected void createItems() {
		ReplaceWalls(this);

        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            for (Item i : bonesItems) {
                drop(i, cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();


        for (int i = 10; i > 0; i--) {
            addItemToSpawn(new RockBlock().quantity(Random.Int(4) + 3));
        }
        addItemToSpawn(Generator.randomUsingDefaults(Generator.Category.FOOD));


        for(Item i : itemsToSpawn)
        {
            drop( i, randomDropCell() );
        }

    }


	@Override
	protected int randomDropCell( Class<?extends Room> roomType ) {
		int tries = 100;
		while (tries-- > 0) {
			Room room = randomRoom( roomType );
			if (room == null){
				return -1;
			}
			if (room != roomEntrance) {
				int pos = pointToCell(room.random());
				if (passable[pos] && !solid[pos]
						&& pos != exit()
						&& heaps.get(pos) == null
						&& room.canPlaceItem(cellToPoint(pos), this)
						&& findMob(pos) == null
						&& !water[pos]) {

					Trap t = traps.get(pos);

					//items cannot spawn on traps which destroy items
					if (t == null ||
							! (t instanceof BurningTrap || t instanceof BlazingTrap
									|| t instanceof ChillingTrap || t instanceof FrostTrap
									|| t instanceof ExplosiveTrap || t instanceof DisintegrationTrap
									|| t instanceof PitfallTrap)) {

						return pos;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.MINE_CRYSTAL:
				return Messages.get(LavaLakeLevel.class, "crystal_name");
			case Terrain.MINE_BOULDER:
				return Messages.get(LavaLakeLevel.class, "boulder_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_ENTRANCE
				&& !BountyHunter.Quest.completed()) {


			String warnText;
			Ruby gold = hero.belongings.getItem(Ruby.class);
			int goldAmount = gold == null ? 0 : gold.quantity();
			if (goldAmount < 10){
				warnText = Messages.get(BountyHunter.class, "exit_warn_none");
			} else if (goldAmount < 20){
				warnText = Messages.get(BountyHunter.class, "exit_warn_low");
			} else if (goldAmount < 30){
				warnText = Messages.get(BountyHunter.class, "exit_warn_med");
			} else if (goldAmount < 40){
				warnText = Messages.get(BountyHunter.class, "exit_warn_high");
			} else {
				warnText = Messages.get(BountyHunter.class, "exit_warn_full");
			}

			if (!BountyHunter.Quest.bossBeaten()){
				switch (BountyHunter.Quest.Type()){
					case BountyHunter.Quest.CRYSTAL: warnText += "\n\n" + Messages.get(Blacksmith.class, "exit_warn_crystal"); break;
					case BountyHunter.Quest.GNOLL: warnText += "\n\n" + Messages.get(Blacksmith.class, "exit_warn_gnoll"); break;
				}
			}

			String finalWarnText = warnText;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndOptions( new BountyHunterSprite(),
							Messages.titleCase(Messages.get(BountyHunter.class, "name")),
							finalWarnText,
							Messages.get(BountyHunter.class, "exit_yes"),
							Messages.get(BountyHunter.class, "exit_no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								BountyHunter.Quest.complete();
								LavaLakeLevel.super.activateTransition(hero, transition);
							}
						}
					} );
				}
			});
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}

	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.WALL:
				return Messages.get(LavaLakeLevel.class, "wall_desc");
			case Terrain.WALL_DECO:
				return super.tileDesc(tile) + "\n\n" +  Messages.get(LavaLakeLevel.class, "gold_extra_desc");
			case Terrain.MINE_CRYSTAL:
				return Messages.get(LavaLakeLevel.class, "crystal_desc");
			case Terrain.MINE_BOULDER:
				return Messages.get(LavaLakeLevel.class, "boulder_desc");
			case Terrain.BARRICADE:
				return Messages.get(LavaLakeLevel.class, "barricade_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		visuals.clear(); //we re-add these in wall visuals
		addNewVisuals( this, visuals );
		return visuals;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_LAVALAKE;
	}

	public static void addNewVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WATER) {
				group.add( new HallsLevel.Stream( i ) );
			}
		}
	}

	public static void ReplaceWalls( Level level ) {

		ArrayList<Integer> VaultPositions = new ArrayList<>();

		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			if (mob instanceof Vault || mob instanceof QuakeWolf || mob instanceof TrollKnight) {
				VaultPositions.add(mob.pos);
			}
		}

		VaultPositions.add(level.entrance());


		boolean[] patch = Patch.generate(level.width, level.height, 0.62f, 5, true );
		for (int i= 14*level.width(); i < level.length(); i++) {
				if (patch[i - 14 * level.width()]) {

					boolean safety = false;
					for (int pos : VaultPositions) {
						if (level.distance(pos, i) < 3) safety = true;
					}
					if (level.map[i] != Terrain.EMPTY_SP && !safety)level.map[i] = Terrain.WATER;
				}
		}

		boolean[] patch2 = Patch.generate(level.width, level.height, 0.12f, 2, true );
		for (int i= 14*level.width(); i < level.length(); i++) {
			if (patch2[i - 14 * level.width()]) {
				if (level.map[i] == Terrain.WALL )level.map[i] = Terrain.EMPTY;
			}
		}

		GameScene.updateMap();
		level.buildFlagMaps();
		level.cleanWalls();


	}

	@Override
	public int randomRespawnCell( Char ch ) {
		int cell;
		int count = 0;
		do {

			//more checks for this type specifically
			if (++count > 100) {
				return -1;
			}

			cell = Random.Int( length() );

		} while ((Dungeon.level == this && heroFOV[cell])
				|| !passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar( cell ) != null
				|| (!ch.flying && Dungeon.level.map[cell] == Terrain.WATER)
				|| ((ch instanceof Piranha | ch instanceof Pyronaut) && Dungeon.level.map[cell] != Terrain.WATER)
		);
		return cell;
	}


    @Override
    public void restoreFromBundle( Bundle bundle ) {

        super.restoreFromBundle(bundle);


    }


}
