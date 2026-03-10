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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.EndothermicRing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMalaise;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SageCorpseSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSageCorpse;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SageCorpse extends NPC {

	{
		spriteClass = SageCorpseSprite.class;

		properties.add(Char.Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.SAGECORPSE;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return Char.INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
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

        if (Quest.given) {
			
			boolean Done;

            if (
                    (Quest.type == 1 && Dungeon.hero.belongings.getItem(WandOfMalaise.class) != null)
                            || (Quest.type == 2 && Dungeon.hero.belongings.getItem(EndothermicRing.class) != null)
                            || (Quest.type == 3 && Dungeon.hero.belongings.getItem(CloakOfThorns.class) != null)
            ) {
                switch (Quest.type) {
                    case 1:
                    default:
                        Done = Dungeon.hero.belongings.getItem(WandOfMalaise.class).QuestDone;
                        break;
                    case 2:
                        Done = Dungeon.hero.belongings.getItem(EndothermicRing.class).QuestDone;
                        break;
                    case 3:
                        Done = Dungeon.hero.belongings.getItem(CloakOfThorns.class).QuestDone;
                        break;
                }
            } else Done = false;

			if (Done) {

				Item Icon;

				switch (Quest.type)
				{
					default:
					case 1:
						Icon = Dungeon.hero.belongings.getItem(WandOfMalaise.class);
						break;

					case 2:
						Icon = Dungeon.hero.belongings.getItem(EndothermicRing.class);
						break;

					case 3:
						Icon = Dungeon.hero.belongings.getItem(CloakOfThorns.class);
						break;

				}
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndSageCorpse( SageCorpse.this, Icon ) );
					}
				});
			} else {
				String msg;
				switch(Quest.type){
					case 1: default:
						msg = Messages.get(this, "reminder_wand", Messages.titleCase(Dungeon.hero.name()));
						break;
					case 2:
						msg = Messages.get(this, "reminder_ring", Messages.titleCase(Dungeon.hero.name()));
						break;
					case 3:
						msg = Messages.get(this, "reminder_artifact", Messages.titleCase(Dungeon.hero.name()));
						break;
				}
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(SageCorpse.this, msg));
					}
				});
			}
			
		} else {

			String msg1 = "";
			String msg2 = "";
			switch(Dungeon.hero.heroClass){
				case WARRIOR:
					msg1 += Messages.get(this, "intro_warrior");
					break;
				case ROGUE:
					msg1 += Messages.get(this, "intro_rogue");
					break;
				case MAGE:
					msg1 += Messages.get(this, "intro_mage", Messages.titleCase(Dungeon.hero.name()));
					break;
				case HUNTRESS:
					msg1 += Messages.get(this, "intro_huntress");
					break;
				case DUELIST:
					msg1 += Messages.get(this, "intro_duelist");
					break;
				case CLERIC:
					msg1 += Messages.get(this, "intro_cleric");
					break;
				case ZEALOT:
					msg1 += Messages.get(this, "intro_zealot");
					break;
			}

			msg1 += Messages.get(this, "intro_1");

			switch (Quest.type){
				case 1:
					msg2 += Messages.get(this, "intro_wand");
					break;
				case 2:
					msg2 += Messages.get(this, "intro_ring");
					break;
				case 3:
					msg2 += Messages.get(this, "intro_artifact");
					break;
			}

			msg2 += Messages.get(this, "intro_2");
			final String msg1Final = msg1;
			final String msg2Final = msg2;
			
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(SageCorpse.this, msg1Final){
						@Override
						public void hide() {
							super.hide();

							GameScene.show(new WndQuest(SageCorpse.this, msg2Final));

							Item m;

                            if (!Quest.givenitems) {
                                switch (Quest.type) {
                                    default:
                                    case 1:
                                        m = new WandOfMalaise();
                                        break;

                                    case 2:
                                        m = new EndothermicRing();
                                        break;

                                    case 3:
                                        m = new CloakOfThorns();
                                        break;

                                }

                                m.identify();

                                if (m.doPickUp(Dungeon.hero)) {
                                    GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", m.name())));
                                } else {
                                    Dungeon.level.drop(m, Dungeon.hero.pos).sprite.drop();
                                }
                                Quest.givenitems = true;
                            }

							Quest.given = true;
						}
					});
				}
			});


		}

		return true;
	}
	
	public static class Quest {

		private static int type;
		// 1 = wand of malaise quest
		// 2 = endothermic ring quest
		// 3 = cloak of thorns quest
		
		private static boolean spawned;
		
		private static boolean given;

        private static boolean givenitems = false;


        public static Item item1;
		public static Item item2;
		
		public static void reset() {
			spawned = false;
			type = 0;

			item1 = null;
			item2 = null;
		}
		
		private static final String NODE		= "wandmaker";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE		= "type";
		private static final String GIVEN		= "given";
        private static final String GIVENITEMS = "givenitems";
        private static final String ITEM1 = "wand1";
		private static final String ITEM2		= "wand2";

		private static final String RITUALPOS	= "ritualpos";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
                node.put(GIVENITEMS, givenitems);

				node.put( TYPE, type );
				
				node.put( GIVEN, given );

				if (Dungeon.prisonalt) {
					node.put(ITEM1, item1);
					node.put(ITEM2, item2);
				}


			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				type = node.getInt(TYPE);
				
				given = node.getBoolean( GIVEN );
                givenitems = node.getBoolean(GIVENITEMS);

				if (Dungeon.prisonalt) {
					item1 = (Item) node.get(ITEM1);
					item2 = (Item) node.get(ITEM2);
				}


			} else {
				reset();
			}
		}
		
		private static boolean spawnonthisroom;
		
		public static void spawnSageCorpse(Level level, Room room ) {

			// decide between 1,2, or 3 for quest type.
			if (type == 0) type = Random.Int(3) + 1;

			if (!spawned && Dungeon.depth > 6 && Random.Int(5 - (Dungeon.depth-5)) == 0) {

                SageCorpse npc = new SageCorpse();
                boolean validPos;
                //Do not spawn wandmaker on the entrance, in front of a door, or on bad terrain.
                do {
                    validPos = true;
                    npc.pos = level.pointToCell(room.random((room.width() > 6 && room.height() > 6) ? 2 : 1));
                    if (npc.pos == level.entrance() || level.solid[npc.pos]) {
                        validPos = false;
                    }
                    for (int i : PathFinder.NEIGHBOURS4) {
                        if (level.map[npc.pos + i] == Terrain.DOOR) {
                            validPos = false;
                        }
                    }
                    if (level.traps.get(npc.pos) != null
                            || !level.passable[npc.pos]
                            || level.map[npc.pos] == Terrain.EMPTY_SP) {
                        validPos = false;
                    }
                } while (!validPos);
                level.mobs.add(npc);

                spawned = true;

            }

            GenerateItems();

        }

        public static void GenerateItems() {
            Generator.Category gentype;

            switch (Quest.type) {
                default:
                case 1:
                    gentype = Generator.Category.WAND;
                    break;

                case 2:
                    gentype = Generator.Category.RING;
                    break;

                case 3:
                    gentype = Generator.Category.ARTIFACT;
                    break;

            }

            given = false;
            item1 = Generator.random(gentype);
            item1.cursed = false;
            item1.upgrade();

            item2 = Generator.random(gentype);
            ArrayList<Item> toUndo = new ArrayList<>();
            while (item2.getClass() == item1.getClass()) {
                toUndo.add(item2);
                item2 = Generator.random(gentype);
            }
            for (Item i : toUndo) {
                Generator.undoDrop(i);
            }
            item2.cursed = false;
            item2.upgrade();
        }

		//quest is active if:
		public static boolean active(){
			//it is not completed
			if (item1 == null || item2 == null
					|| !(Dungeon.level instanceof RegularLevel) || Dungeon.hero == null){
				return false;
			}

			return true;
		}
		
		public static void complete() {
			item1 = null;
			item2 = null;
			
			Notes.remove( Notes.Landmark.SAGECORPSE );
			//other quests award score when their boss is defeated
			if (Quest.type == 1) {
				Statistics.questScores[1] += 2000;
			}
		}
	}
}
