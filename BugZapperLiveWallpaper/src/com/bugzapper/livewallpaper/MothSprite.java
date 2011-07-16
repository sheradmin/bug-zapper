package com.bugzapper.livewallpaper;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;

import java.util.*;

/**
 * @author Sher
 *
 * Animated bugs (different types and sizes) that fly into the bug zapper giving off bright flash and making "zap"
 * sound. Color flash should match color of bug zapper light selected
 */
class MothSprite extends AnimatedSprite {

    private boolean stopped = false;
    private float deadPointX, deadPointY;
    private Sound zapperSound;
    private float zapCenterX;
    private float zapCenterY;
    private int maxRad = 200;
    private int index = 0;
    private float scale = 1f;
    private float angleX = 0f;
    private float angleY = 0f;

    /**
     * @param zapCenterX
     * @param zapCenterY
     * @param deadPointX
     * @param deadPointY
     * @param pTiledTextureRegion
     * @param zapperSound
     * @param i
     */
    public MothSprite(float zapCenterX, float zapCenterY, float deadPointX, float deadPointY, TiledTextureRegion pTiledTextureRegion, Sound zapperSound, int i) {
        super(deadPointX, deadPointY + 160, 32, 32, pTiledTextureRegion);
        this.zapCenterX = zapCenterX;
        this.zapCenterY = zapCenterY;
        this.deadPointX = deadPointX;
        this.deadPointY = deadPointY;
        this.zapperSound = zapperSound;
        this.index = i;

        registerEntityModifier();
    }

    /**
     * Register Entity Modifier
     */
    private void registerEntityModifier() {
        this.clearEntityModifiers();
        LoopEntityModifier loopEntityModifier = getLoopEntityModifier(zapCenterX, zapCenterY, maxRad);
        this.registerEntityModifier(loopEntityModifier);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        if (this.isAnimationRunning()) {
            if (this.stopped) {
                this.stopAnimation();
                this.setVisible(false);
                this.timer();
                this.stopped = false;
            }
            if (deadPointX < this.mX && deadPointX + 32 > this.mX && deadPointY < this.mY && deadPointY + 34 > this.mY && isZapperOn()) {
                this.setScale(1.2f);
                int zI = (BugZapperConfig.getInstance().getZapperIndex() * 4) + 2;
                this.animate(new long[]{50, 50}, zI, zI + 1, true);
                this.playSound();
                this.stopped = true;
            }
        }
        super.onManagedUpdate(pSecondsElapsed);
    }

    private void playSound() {
        if (BugZapperConfig.getInstance().isMothSoundOn()) {
            this.zapperSound.play();
        }
    }

    private void timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        resetMoth();
                    }
                }, (long) (Math.random() * 10));
    }

    /**
     * Reset Bug to default
     */
    private void resetMoth() {
        if (scale < 0.8f) {
            scale = 1f;
        }
        this.setScale(scale);
        scale = scale - 0.1f;

        registerEntityModifier();
        this.stopped = false;
        this.setVisible(true);
        mothAnimate();
    }

    private void mothAnimate() {
        this.animate(new long[]{30, 30}, index, index + 1, true);
    }


    public boolean isZapperOn() {
        return BugZapperConfig.getInstance().isZapperOn();
    }

    private LoopEntityModifier getLoopEntityModifier(float zapCenterX, float zapCenterY, int maxRad) {
        List<Integer> radiusList = new ArrayList<Integer>();
        int rad = maxRad;
        radiusList.add(rad);
        while(rad !=0 ) {
            int radChange = MathUtils.random(1, 10) - 6;

            if (radChange < 0) {
                radChange = 0;
            }

            rad -= radChange;
            if (rad < 0) {
                rad = 0;
            }

            radiusList.add(rad);
        }

        for(int i = radiusList.size() - 2; i >= 0; i--) {
            radiusList.add(radiusList.get(i));
        }

        int pathCount = radiusList.size();

        float[] pathX = new float[pathCount];
        float[] pathY = new float[pathCount];

        float currentAngle = (float) (Math.random() * Math.PI * 2);

        for (int k = 0; k < pathCount; k++) {
            currentAngle += MathUtils.random(-0.2f, 0.2f);
            pathX[k] = (float) (zapCenterX + Math.cos(currentAngle) * radiusList.get(k));
            pathY[k] = (float) (zapCenterY + Math.sin(currentAngle) * radiusList.get(k));
        }

        final PathModifier.Path path = new PathModifier.Path(pathCount * 2 - 1);

        for (int i = 0; i < pathCount; i++) {
            path.to(pathX[i], pathY[i]);
        }

        for (int i = pathCount - 2; i >= 0; i--) {
            path.to(pathX[i], pathY[i]);
        }

        PathModifier pathModifier = new PathModifier(MathUtils.random(50f, 80f), path, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
                mothAnimate();
            }

            @Override
            public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
            }

            @Override
            public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
            }

            @Override
            public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
                if (MothSprite.this.isVisible()) {
                    resetMoth();
                }
            }
        });

        return new LoopEntityModifier(pathModifier);
    }

}