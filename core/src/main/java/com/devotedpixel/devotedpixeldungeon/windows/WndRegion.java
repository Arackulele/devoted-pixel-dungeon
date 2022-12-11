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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class WndRegion extends Window {

	private static final int WIDTH		= 120;
	private static final int TTL_HEIGHT = 16;
	private static final int BTN_HEIGHT = 16;
	private static final int GAP        = 1;

	private boolean Gardens;

	private boolean Coldhouse;

	private CheckBox cb;

	private CheckBox cb2;

	private CheckBox cb3;

	private CheckBox cb4;
	private boolean camefromrandom;

	private boolean editable;
	private ArrayList<CheckBox> boxes;

	public WndRegion( int checked, boolean editable ) {

		super();

		this.editable = editable;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );

		float pos = TTL_HEIGHT;


			cb = new CheckBox( Messages.titleCase(Messages.get(WndRegion.class, "region1")) );
			cb.checked( SPDSettings.gardens() != false );
			cb.active = editable;
				pos += GAP;
			cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

			add( cb );
			
			IconButton info = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.scene().add(
							new WndMessage(Messages.get(WndRegion.class, "region1desc"))
					);
				}
			};
			info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
			add(info);
			
			pos = cb.bottom();


		cb2 = new CheckBox( Messages.titleCase(Messages.get(WndRegion.class, "region2")) );
		cb2.checked( SPDSettings.coldhouse() != false );
		cb2.active = editable;
		pos += GAP;
		cb2.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

		add( cb2 );

		IconButton info2 = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				ShatteredPixelDungeon.scene().add(
						new WndMessage(Messages.get(WndRegion.class, "region2desc"))
				);
			}
		};
		info2.setRect(cb2.right(), pos, 16, BTN_HEIGHT);
		add(info2);

		pos = cb2.bottom();


		cb3 = new CheckBox( Messages.titleCase(Messages.get(WndRegion.class, "region3")) );
		cb3.checked( SPDSettings.forge() != false );
		cb3.active = editable;
		pos += GAP;
		cb3.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

		add( cb3 );

		IconButton info3 = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				ShatteredPixelDungeon.scene().add(
						new WndMessage(Messages.get(WndRegion.class, "region3desc"))
				);
			}
		};
		info3.setRect(cb3.right(), pos, 16, BTN_HEIGHT);
		add(info3);

		pos = cb3.bottom();



		cb4 = new CheckBox( Messages.titleCase(Messages.get(WndRegion.class, "region4")) );
		cb4.checked( SPDSettings.citadel() != false );
		cb4.active = editable;
		pos += GAP;
		cb4.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

		add( cb4 );

		IconButton info4 = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				ShatteredPixelDungeon.scene().add(
						new WndMessage(Messages.get(WndRegion.class, "region4desc"))
				);
			}
		};
		info4.setRect(cb4.right(), pos, 16, BTN_HEIGHT);
		add(info4);

		pos = cb4.bottom();




		RedButton Random = new RedButton(Messages.get(WndRegion.class, "random")) {
			@Override
			protected void onClick() {
				SPDSettings.putregiontamper(false);
				camefromrandom=true;
				onBackPressed();
			}


		};

		Random.setRect(0, Random.bottom() + info4.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(Random);
		resize(WIDTH, (int) Random.bottom());

	}

	@Override
	public void onBackPressed() {
		if (camefromrandom==false) SPDSettings.putregiontamper(true);
		else SPDSettings.putregiontamper(false);
		if (cb.checked()) {
			SPDSettings.putgardens(true);
		}
		else SPDSettings.putgardens(false);

		if (cb2.checked()) {
			SPDSettings.putcoldhouse(true);
		}
		else SPDSettings.putcoldhouse(false);

		if (cb3.checked()) {
			SPDSettings.putforge(true);
		}
		else SPDSettings.putforge(false);

		if (cb4.checked()) {
			SPDSettings.putcitadel(true);
		}
		else SPDSettings.putcitadel(false);

		camefromrandom=false;
		super.onBackPressed();
	}
}