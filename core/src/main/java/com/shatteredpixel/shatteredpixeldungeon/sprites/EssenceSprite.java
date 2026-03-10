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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.NeuronSentry;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.altregion.VelEssence;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public abstract class EssenceSprite extends MobSprite {

    protected abstract int texOffset();

    public EssenceSprite() {
        super();

        int c = texOffset();

        texture(Assets.Sprites.ESSENCE);

        TextureFilm frames = new TextureFilm(texture, 19, 27);

        idle = new Animation(4, true);
        idle.frames(frames, c + 0, c + 0, c + 1, c + 1, c + 0, c + 0, c + 1, c + 1, c + 0, c + 0, c + 1, c + 1, c + 0, c + 2, c + 0, c + 2);

        run = new Animation(5, true);
        run.frames(frames, c + 3, c + 4);

        attack = new Animation(12, false);
        attack.frames(frames, c + 5, c + 6, c + 7, c + 7, c + 5);

        zap = attack.clone();

        die = new Animation(8, false);
        die.frames(frames, c + 8, c + 9, c + 10, c + 11, c + 12, c + 13, c + 14);

        play(idle);
    }

    public static class Unearth extends EssenceSprite {

        @Override
        protected int texOffset() {
            return 0;
        }
    }

    public static class Snipe extends EssenceSprite {

        public void zap(int cell) {

            super.zap(cell);

            MagicMissile.boltFromChar(parent,
                    MagicMissile.VOID_SNIPE,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((VelEssence.Snipe) ch).onZapComplete();
                        }
                    });
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
        }

        @Override
        public void onComplete(Animation anim) {
            if (anim == zap) {
                idle();
            }
            super.onComplete(anim);
        }

        @Override
        protected int texOffset() {
            return 15;
        }
    }

    public static class Bombard extends EssenceSprite {

        @Override
        protected int texOffset() {
            return 30;
        }

        public void zap(int cell) {

            super.zap(cell);

            MagicMissile.boltFromPos(parent,
                    MagicMissile.VOID_BLUE,
                    Math.max(cell - (Dungeon.level.width() * 6) + Random.Int(-5, 5), 0),
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((VelEssence.Bombard) ch).onZapComplete(cell);
                        }
                    });
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
        }

        @Override
        public void onComplete(Animation anim) {
            if (anim == zap) {
                idle();
            }
            super.onComplete(anim);
        }
    }

    public static class Ram extends EssenceSprite {

        @Override
        protected int texOffset() {
            return 45;
        }
    }

    public static class Restrain extends EssenceSprite {

        @Override
        protected int texOffset() {
            return 60;
        }
    }
}
