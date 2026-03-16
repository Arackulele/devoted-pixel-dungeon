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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.noosa.ui.Component;

public class ArtbookScene extends PixelScene {

    private static final float GAP	= 4;

    private IconButton goLeft;
    private IconButton goRight;

    Image pic;

    Image bg;

    public static Object[] imgs = new Object[] {
            Assets.Artbook.PAGE1,
            Assets.Artbook.PAGE2
    };

    public static int index = 0;

    public static ArtbookScene instance;

    RenderedTextBlock label;

    @Override
	public void create() {
		super.create();

        instance = this;

		final float colWidth = 120;
		final float fullWidth = colWidth * (landscape() ? 2 : 1);

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		//darkens the arches
		add(new ColorBlock(w, h, 0x88000000));


        IconTitle title = new IconTitle( Icons.DEVO.get(), Messages.get(this, "title"));
        title.setSize(200, 0);
        title.setPos(
                (w - title.reqWidth()) / 2f,
                (20 - title.height()) / 2f
        );
        align(title);
        add(title);

        SetImages();

        label = renderTextBlock( 8 );
        label.hardlight( 0xCCCCCC );
        label.setHightlighting(true, Window.SHPX_COLOR);
        label.text( Messages.get(this, "text" + index));
        add( label );

        label.setPos(
                (w - label.width()) / 2,
                h - label.height() - 2*GAP
        );
        align(label);




        goRight = new IconButton(Icons.CHEVRON.get()){
            @Override
            protected void onClick() {
            if (index < imgs.length - 1) index++;
            SetImages();
            }

        };
        goRight.icon().originToCenter();
        goRight.icon().angle = 90f;
        goRight.setRect(w-40, h/2, 20, 21);
        add(goRight);

        goLeft = new IconButton(Icons.CHEVRON.get()){
            @Override
            protected void onClick() {
                if (index > 0 ) index--;
                SetImages();
            }
        };
        goLeft.icon().originToCenter();
        goLeft.icon().angle = 270f;
        goLeft.setRect(40, h/2, 20, 21);
        add(goLeft);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
        add( btnExit );


    }

    public void SetImages()
    {
        int w = Camera.main.width;
        int h = Camera.main.height;


        pic = new Image(imgs[index]);
        float topRegion = Math.max(pic.height - 6, h*0.45f);
        pic.scale.scale(Math.max(0.001f, w/ 1150f));
        pic.x = (w - pic.width()) / 2f;
        pic.y =  (- 70 )+ (topRegion - pic.height()) / 2f;
        align(pic);

        bg = new Image(Assets.Artbook.BG);
        add( bg );
        bg.scale = pic.scale;
        bg.x = (w - bg.width()) / 2f;
        bg.y = (- 70 )+ (topRegion - bg.height()) / 2f;
        align(bg);
        add( pic );

        if (label != null)label.text( Messages.get(this, "text" + index));
    }
}
