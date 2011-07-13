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
        int pathCount = 16 + ((int) (Math.random() * 50)) * 2;
        float[] pathX = new float[pathCount];
        float[] pathY = new float[pathCount];

        float currentAngle1 = (float) (Math.random() * Math.PI * 2);
        float currentAngle2 = (float) (Math.random() * Math.PI * 2);
        if (angleX != 0f) {
            currentAngle1 = angleX;
            currentAngle2 = angleY;
        } else {
            angleX = currentAngle1;
            angleY = currentAngle2;
        }
        int k = 0;
        for (int currentRad = maxRad; currentRad >= 0; currentRad = (k == pathCount / 2) ? (currentRad - (maxRad / (pathCount / 2))) : 0) {
            float changeAngle1 = (float) ((Math.random() - 0.5) * (Math.PI));
            float changeAngle2 = (float) ((Math.random() - 0.5) * (Math.PI));
            currentAngle1 += changeAngle1;
            currentAngle2 += changeAngle2;
            pathX[k] = (float) (zapCenterX + Math.cos(currentAngle1) * currentRad);
            pathY[k] = (float) (zapCenterY + Math.sin(currentAngle1) * currentRad);

            pathX[(pathCount - 1) - k] = (float) (zapCenterX + Math.cos(currentAngle2) * currentRad);
            pathY[(pathCount - 1) - k] = (float) (zapCenterY + Math.sin(currentAngle2) * currentRad);
            k++;
        }

        final PathModifier.Path path = new PathModifier.Path(pathCount * 2 - 1);

//        Log.i("zapx:zapy", zapCenterX + ":" + zapCenterY);
        for (int i = 0; i < pathCount; i++) {
//            Log.i("x:y", pathX[i] + ":" + pathY[i]);
            path.to(pathX[i], pathY[i]);
        }

        for (int i = pathCount - 2; i >= 0; i--) {
//            Log.i("x:y", pathX[i] + ":" + pathY[i]);
            path.to(pathX[i], pathY[i]);
        }

        PathModifier pathModifier = new PathModifier(MathUtils.random(20f, 50f), path, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
                mothAnimate();
            }

            @Override
            public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
//                mothAnimate();
                /*switch(pWaypointIndex) {
					case 0:
						MothSprite.this.animate(new long[]{100, 100}, 4, 5, true);
						break;
					case 1:
						MothSprite.this.animate(new long[]{100, 100}, 2, 3, true);
						break;
					case 2:
						MothSprite.this.animate(new long[]{100, 100}, 0, 1, true);
						break;
					case 3:
						MothSprite.this.animate(new long[]{100, 100}, 6, 7, true);
						break;
				}*/
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