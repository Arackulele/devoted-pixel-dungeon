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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class VoidSnipeParticle extends PixelParticle {

    public static final Factory FACTORY = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).reset(x, y);
        }
    };

    public static final Factory SMALL = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 0);
        }
    };

    public static final Factory BLUE = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 1);
        }
    };

    public static final Factory RED = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 2);
        }
    };

    public static final Factory YELLOW = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 3);
        }
    };

    public static final Factory PURPLE = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 4);
        }
    };


    public static final Factory MAIN = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((VoidSnipeParticle) emitter.recycle(VoidSnipeParticle.class)).resetSmall(x, y, 5);
        }
    };


    public VoidSnipeParticle() {
        super();


        angle = Random.Float(-30, 30);
    }

    public void reset(float x, float y) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan = 0.5f;
        size = 16;

        acc.y = 0;
        speed.y = 0;
        angularSpeed = 0;
    }

    int originalColor;

    public void resetSmall(float x, float y, int color) {
        reset(x, y);

        left = lifespan = 1f;
        size = 8;

        switch (color) {
            default:
            case 0:
                originalColor = ColorMath.random(0x00ff0d, 0x0cc415);
                color(originalColor);
                break;
            case 1:
                originalColor = ColorMath.random(0x428af5, 0x2565c4);
                color(originalColor);
                size = Random.Int(5, 15);
                break;
            case 2:
                originalColor = ColorMath.random(0xc0313, 0x9e1b23);
                color(originalColor);
                break;
            case 3:
                originalColor = ColorMath.random(0xfff53b, 0xe6bf32);
                color(originalColor);
                break;
            case 4:
                originalColor = ColorMath.random(0x6200ff, 0x3228e0);
                color(originalColor);
                break;
            case 5:
                originalColor = ColorMath.random(0x6200ff, 0x001e30);
                color(originalColor);
                break;
        }
    }

    public void resetFalling(float x, float y) {
        reset(x, y);

        left = lifespan = 1f;
        size = 8;

        acc.y = 30;
        speed.y = -5;
        angularSpeed = Random.Float(-90, 90);
    }

    @Override
    public void update() {
        super.update();

        float p = left / lifespan;
        color(ColorMath.interpolate(0x290338, originalColor, p));
        size((p < 0.5f ? p : 1 - p) * size);
    }
}