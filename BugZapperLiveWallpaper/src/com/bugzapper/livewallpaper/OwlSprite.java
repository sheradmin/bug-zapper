package com.bugzapper.livewallpaper;

import android.util.Log;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sher
 */
class OwlSprite extends AnimatedSprite {

    public boolean stopped = true;
    private int count = 0;
    private int tile = 0;
    private boolean out = true;

    private float x = 0;
    private float y = 0;
    private static final float VELOCITY_X = -13.0f;
    private static final float VELOCITY_Y = 20.0f;

    public OwlSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion) {
        super(pX, pY, pTiledTextureRegion);

        this.x = pX;
        this.y = pY;
        long[] frameDurations = new long[6];
        Arrays.fill(frameDurations, 600);
        this.animate(frameDurations, 0, 5, true);
        this.stopAnimation(11);
    }

    public void onManagedUpdateOwl() {
        Log.d("onManagedUpdateOwl", String.valueOf(stopped));
        Log.d("onManagedUpdateOwl", String.valueOf(out));
        if (!this.stopped) {
            if (out) {
                out();
            } else {
                come();
            }
        }
    }

    public void resetOwl() {
        this.out = true;
        this.stopped = true;
        this.count = 0;
        this.tile = 0;
        this.x = this.getInitialX();
        this.y = this.getInitialY();

        this.reset();
        long[] frameDurations = new long[6];
        Arrays.fill(frameDurations, 600);
        this.animate(frameDurations, 0, 5, true);
        this.stopAnimation(11);
    }

    public void come() {
        this.setVisible(true);
        this.x = this.x + (-VELOCITY_X);
        this.y = this.y + VELOCITY_Y;

        count++;
        if (count < 4) {
            long[] frameDurations = new long[2];
            Arrays.fill(frameDurations, 500);
            this.animate(frameDurations, tile, tile + 1, true);
        } else {
            tile = tile + 1;
            count = 0;
        }

        if (this.x > this.getInitialX()) {
            this.x = this.getInitialX();
        }

        if (this.y > this.getInitialY()) {
            this.y = this.getInitialY();
        }

        this.setPosition(this.x, this.y);

        if (this.getCurrentTileIndex() == 11 && count > 1) {
            tile = 0;
            count = 0;
            this.out = true;
            this.stopAnimation();
            this.stopped = true;
            this.x = this.mX;
            this.y = this.mY;
        }
    }

    public void out() {
        this.setVisible(true);
        this.x = this.x + VELOCITY_X;
        this.y = this.y + VELOCITY_Y;

        count++;
        if (count < 4) {
            long[] frameDurations = new long[2];
            Arrays.fill(frameDurations, 500);
            this.animate(frameDurations, tile, tile + 1, true);

        } else {
            tile = tile + 1;
            count = 0;
        }

        this.setPosition(this.x, this.y);

        if (this.getCurrentTileIndex() == 5) {
            this.out = false;
            this.mY = mY - 2 * (mY - this.getInitialY());
            tile = 6;
            count = 0;
            this.x = this.mX + 8;
            this.y = this.mY + 10;
            this.stopAnimation();
            this.setVisible(false);
            this.stopped = true;
            timer();
        }
    }

    private void timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                OwlSprite.this.stopped = false;
                OwlSprite.this.animate(1000);
            }
        }, 6000);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isStopped() {
        return stopped;
    }
}