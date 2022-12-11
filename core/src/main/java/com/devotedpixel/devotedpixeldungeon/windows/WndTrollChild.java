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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrollChild;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Excalibur;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class WndTrollChild extends Window {
//im not proud of this code, rewrite in order at some point
	private static final int WIDTH      = 120;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP        = 2;
	private static String msgtype;

	public static boolean firsttime = true;


	public WndTrollChild( final TrollChild child,final int classnum ) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(ItemSpriteSheet.EXCALIBUR));
		titlebar.label((Messages.get(this, "title")));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);


		if (firsttime == true) {


			switch (classnum) {
				case 1:
				default:

					msgtype = "message";
					break;

				case 2:
					msgtype = "message_mage";
					break;

				case 3:
					msgtype = "message_rogue";
					break;
				case 4:
					msgtype = "message_huntress";
					break;
				case 5:
					msgtype = "message_zealot";
					break;
			}

		}
		else if (TrollChild.Quest.given == true){
			switch (classnum) {
				case 1:
				default:

					msgtype = "completedmessage";
					break;
				case 2:
					msgtype = "completedmessage_mage";
					break;
				case 3:
					msgtype = "completedmessage_rogue";
					break;
				case 4:
					msgtype = "completedmessage_huntress";
					break;
				case 5:
					msgtype = "completedmessage_zealot";
					break;
			}
		}





		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, msgtype), 6);
		add(message);
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		resize(WIDTH, (int) message.bottom());



		if (TrollChild.reward != null) {

			RedButton btnReward = new RedButton(Messages.get(this, "reward")) {
				@Override
				protected void onClick() {
					takeReward(child, TrollChild.reward);
				}

			};


			btnReward.setRect(0, btnReward.bottom() + message.bottom() + GAP, WIDTH, BTN_HEIGHT);
			add(btnReward);
			resize(WIDTH, (int) btnReward.bottom());
		}
		else {

			RedButton btnAccept = new RedButton(Messages.get(this, "accept")) {
				@Override
				protected void onClick() {
					hide();
					child.accept();
				}


			};

			RedButton btnDeny = new RedButton(Messages.get(this, "deny")) {
				@Override
				protected void onClick() {
					hide();
				}


			};

			btnAccept.setRect(0, btnAccept.bottom() + message.bottom() + GAP, WIDTH/2, BTN_HEIGHT);
			add(btnAccept);
			btnDeny.setRect(60, btnDeny.bottom() + message.bottom() + GAP, WIDTH/2, BTN_HEIGHT);
			add(btnDeny);
			resize(WIDTH, (int) btnDeny.bottom());
		}






	}


		private void takeReward( TrollChild child, Item reward ) {

			hide();

			if (reward == null) return;

			reward.identify();
			if (reward.doPickUp( Dungeon.hero )) {
				GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
			} else {
				Dungeon.level.drop( reward, child.pos ).sprite.drop();
			}


			GLog.i(Messages.get(TrollChild.class, "cya"));

			child.destroy();
			child.sprite.die();

			TrollChild.Quest.complete();
		}


}