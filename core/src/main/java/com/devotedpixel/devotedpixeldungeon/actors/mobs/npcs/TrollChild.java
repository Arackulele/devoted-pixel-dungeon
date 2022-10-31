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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Envoy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Paladin;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EvilMage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollChildSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTrollChild;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TrollChild extends NPC {

	{
		spriteClass = TrollChildSprite.class;

		state = WANDERING;
		target = Dungeon.hero.pos;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	private static boolean spawned;

	private static final String SPAWNED		= "spawned";

	private static final String PROCESSED	= "processed";

	public static Item reward;

	private static int classnum;

	@Override
	public boolean reset() {
		return true;
	}



	@Override
	public boolean interact(Char c) {






		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;

		}


		if (Dungeon.hero.heroClass == HeroClass.WARRIOR) classnum = 1;
		else if (Dungeon.hero.heroClass == HeroClass.MAGE) classnum = 2;
		else if (Dungeon.hero.heroClass == HeroClass.ROGUE) classnum = 3;
		else if (Dungeon.hero.heroClass == HeroClass.HUNTRESS) classnum = 4;
		else if (Dungeon.hero.heroClass == HeroClass.ZEALOT) classnum = 5;
		else classnum = 1;

		if (Quest.given && !Quest.completed) {


			GLog.i(Messages.get(this, "quest_midmessage"));



		} else {
			if (Dungeon.hero.heroClass != null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndTrollChild( TrollChild.this, classnum) );
					}

				});


				Mob questBoss;

				switch (classnum) {
					case 1:
					default:
						questBoss = new Paladin();
						break;
					case 2:
						questBoss = new EvilMage();
						break;
					case 3:
						questBoss = new Envoy();
						break;

				}

				questBoss.pos = Dungeon.level.randomRespawnCell(this);

				if (questBoss.pos != -1) {
					GameScene.add(questBoss);

					WndTrollChild.hasaccepted = true;
					Quest.given = true;
					Quest.completed = false;
					Notes.add(Notes.Landmark.TROLL_CHILD);
				}

			}

			};




		return true;
	}






	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		if (Quest.processed()) {
			target = Dungeon.hero.pos;
		}
		if (Quest.completed()) {
			target = Dungeon.hero.pos;
		}
		if (Dungeon.level.heroFOV[pos] && !Quest.completed()){
			Notes.add( Notes.Landmark.TROLL_CHILD );
		}
		return super.act();
	}






	public static class Quest {


		private static boolean completed;
		private static boolean spawned;

		private static int type;

		private static boolean given;
		private static boolean processed;


		private static int depth;



		public static void reset() {
			spawned = false;


		}

		private static final String NODE = "TrollChild";

		private static final String SPAWNED = "spawned";
		private static final String TYPE = "type";
		private static final String GIVEN = "given";
		private static final String PROCESSED = "processed";
		private static final String DEPTH = "depth";


		public static void spawn(SewerLevel level) {
			int pos = -1;
			if (!spawned && Dungeon.depth == 2) {

				TrollChild child = new TrollChild();

				do {
					child.pos = level.randomRespawnCell(child);
				} while (child.pos == -1);
				level.mobs.add(child);

				spawned = true;

			}
		}

		public static void complete() {

			Notes.remove(Notes.Landmark.TROLL_CHILD);
			completed = true;
		}

		public static boolean processed(){
			return spawned && processed;
		}

		public static boolean completed(){
			return processed();
		}

	}




}
