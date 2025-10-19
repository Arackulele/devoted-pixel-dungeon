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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wisp;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.TorchHalo;
import com.watabou.noosa.TextureFilm;

public class WispSprite extends MobSprite {

	public WispSprite() {
		super();
		
		texture( Assets.Sprites.WISP );
		
		TextureFilm frames = new TextureFilm( texture, 13, 16 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 1 );

		run = new Animation( 7, true );
		run.frames( frames, 6, 7, 8, 9 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 2, 3, 4, 5, 0 );

		zap = new Animation( 10, false );
		zap.frames( frames, 2, 3, 4, 5, 0 );

		die = new Animation( 10, false );
		die.frames( frames, 10, 11, 12, 13, 14, 15, 16 );
		
		play( idle );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		light = new TorchHalo( this );
		light.hardlight(blood() & 0x00FFFFFF);
		light.alpha(0.3f);
		light.radius(10);

		GameScene.effect(light);
	}

	public void zap( int cell ) {

		super.zap( cell );

		parent.add(new com.watabou.noosa.tweeners.AlphaTweener(light, 1f, 0.2f) {
			@Override
			public void onComplete() {
				light.alpha(0.3f);
				((Wisp)ch).onZapComplete();
				Beam ray = new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(cell));
				ray.hardlight(blood() & 0x00FFFFFF);
				parent.add( ray );
			}
		});

	}

	private float baseY = Float.NaN;

	@Override
	public void update() {
		super.update();

		if (!paused && curAnim != die){
			if (Float.isNaN(baseY)) baseY = y;
			y = baseY + Math.abs((float)Math.sin(com.watabou.noosa.Game.timeTotal));
			shadowOffset = 0.25f - 0.8f*Math.abs((float)Math.sin(com.watabou.noosa.Game.timeTotal));
		}

		if (light != null){
			light.visible = visible;
			light.point(center());

		}
	}

	@Override
	public void place(int cell) {
		super.place(cell);
		baseY = y;
	}

	@Override
	public com.watabou.utils.PointF point(com.watabou.utils.PointF p) {
		super.point(p);
		baseY = y;
		return p;
	}

	@Override
	public void die() {
		super.die();
		if (light != null){
			light.putOut();
		}
	}

	@Override
	public void kill() {
		super.kill();
		if (light != null){
			light.killAndErase();
		}
	}

	@Override
	public void turnTo(int from, int to) {
		//do nothing
	}

	@Override
	public void move(int from, int to) {
		super.move(from, to);
	}

	@Override
	public int blood() {
		return 0xFF66B3FF;
	}
}
