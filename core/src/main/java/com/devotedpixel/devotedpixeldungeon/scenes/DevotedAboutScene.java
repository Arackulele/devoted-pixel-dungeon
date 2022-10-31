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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class DevotedAboutScene extends PixelScene {

	private static final int BTN_HEIGHT = 22;

	@Override
	public void create() {
		super.create();

		final float colWidth = 120;
		final float fullWidth = colWidth * (landscape() ? 2 : 1);
		int elementWidth = PixelScene.landscape() ? 202 : 120;


		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		//darkens the arches
		add(new ColorBlock(w, h, 0x88000000));

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();





		//*** Shattered Pixel Dungeon Credits ***

		String shpxLink = "https://shatteredpixel.com/";
		String devotedLink = "https://github.com/Arackulele";
		//tracking codes, so that the website knows where this pageview came from
		shpxLink += "?utm_source=shatteredpd";
		shpxLink += "&utm_medium=about_page";
		shpxLink += "&utm_campaign=ingame_link";

		CreditsBlock devoted = new CreditsBlock(true, Window.DEVOTED_COLOR,
				"Devoted Pixel Dungeon",
				Icons.ARA.get(),
				"Mod Developed by: Arackulele",
				"github",
				devotedLink);
		devoted.setRect((Camera.main.width - colWidth)/2f, 4, colWidth, 0);
		content.add(devoted);

		CreditsBlock main = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"Hey im me Ara arackulele and this is an expansion mod for shattered pixel dungeon, thanks for playing! Follow me on my socials if you want to",
				null,
				null);

		main.setRect((Camera.main.width - colWidth)/2f, devoted.bottom() + 12, colWidth, 0);
		content.add(main);



		CreditsBlock twitch = new CreditsBlock(false,
				Window.TITLE_COLOR,
				null,
				null,
				null,
				"My Twitch",
				"https://www.twitch.tv/arackulele_");

		twitch.setRect((Camera.main.width - colWidth)/2f, main.bottom() + 12, colWidth, 0);
		content.add(twitch);

		addLine(twitch.top() - 4, content);

		addLine(twitch.bottom() + 4, content);

		CreditsBlock youtube = new CreditsBlock(false,
				Window.TITLE_COLOR,
				null,
				null,
				null,
				"My Youtube",
				"https://www.youtube.com/channel/UCbF-SBnZgiCWzsjy0eg-eNw");

		youtube.setRect((Camera.main.width - colWidth)/2f, twitch.bottom() + 12, colWidth, 0);
		content.add(youtube);

		addLine(youtube.top() - 4, content);

		addLine(youtube.bottom() + 4, content);

		CreditsBlock twitter = new CreditsBlock(false,
				Window.TITLE_COLOR,
				null,
				null,
				null,
				"My Twitter",
				"https://twitter.com/Arackulele1");

		twitter.setRect((Camera.main.width - colWidth)/2f, youtube.bottom() + 12, colWidth, 0);
		content.add(twitter);

		addLine(twitter.top() - 4, content);

		addLine(twitter.bottom() + 4, content);

		CreditsBlock artist = new CreditsBlock(false,
				Window.TITLE_COLOR,
				"Artist Credit",
				null,
				"TarzHel#2821(:soiled:) on discord for the Envoy Sprite",
				null,
				null);

		artist.setRect((Camera.main.width - colWidth)/2f, twitter.bottom() + 12, colWidth, 0);
		content.add(artist);

		addLine(artist.top() - 4, content);

		addLine(artist.bottom() + 4, content);





		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;
		StyledButton shatteredabout = new StyledButton(GREY_TR,"Shattered PD About"){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene( AboutScene.class );
			}
		};
		shatteredabout.icon(Icons.get(Icons.SHPX));
		shatteredabout.textColor(Window.TITLE_COLOR);
		shatteredabout.setSize(elementWidth, BTN_HEIGHT);
		shatteredabout.setPos( (w-elementWidth)/2f, artist.bottom() + 12);
		add(shatteredabout);

		//fadeIn();
	}





	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content ){
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		//many elements can be null, but body is assumed to have content.
		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						ShatteredPixelDungeon.platform.openURI( linkUrl );
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (title != null){
				title.maxWidth((int)width());
				title.setPos( x + (width() - title.width())/2f, topY);
				topY += title.height() + (large ? 2 : 1);
			}

			if (large){

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 2;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

			} else {

				if (avatar != null){
					avatar.x = x;
					body.maxWidth((int)(width() - avatar.width - 1));

					float fullAvHeight = Math.max(avatar.height(), 16);
					if (fullAvHeight > body.height()){
						avatar.y = topY + (fullAvHeight - avatar.height())/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY + (fullAvHeight - body.height())/2f);
						topY += fullAvHeight + 1;
					} else {
						avatar.y = topY + (body.height() - fullAvHeight)/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY);
						topY += body.height() + 2;
					}

				} else {
					topY += 1;
					body.maxWidth((int)width());
					body.setPos( x, topY);
					topY += body.height()+2;
				}

			}

			if (link != null){
				if (large) topY += 1;
				link.maxWidth((int)width());
				link.setPos( x + (width() - link.width())/2f, topY);
				topY += link.height() + 2;

				linkButton.x = link.left()-1;
				linkButton.y = link.top()-1;
				linkButton.width = link.width()+2;
				linkButton.height = link.height()+2;

				linkUnderline.size(link.width(), PixelScene.align(0.49f));
				linkUnderline.x = link.left();
				linkUnderline.y = link.bottom()+1;

			}

			topY -= 2;

			height = Math.max(height, topY - top());
		}
	}
}
