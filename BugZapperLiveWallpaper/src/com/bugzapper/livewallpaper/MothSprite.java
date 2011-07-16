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
    private float zapCenterX; //X coordinate of the center of the zapper
    private float zapCenterY; //Y coordinate of the center of the zapper
    private static int maxRadius = 200; //the bugs will fly within the maximum radius from the center of the zapper
    private int index = 0; //index of the frame of the animation
    private float scale = 1f;

    /** Creating a new bug on the screen
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
        LoopEntityModifier loopEntityModifier = getLoopEntityModifier(zapCenterX, zapCenterY);
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

            //Checking if the bug is zapped
            if (deadPointX < this.mX && deadPointX + 32 > this.mX && deadPointY < this.mY && deadPointY + 34 > this.mY && isZapperOn()) {
                this.setScale(1.2f);
                int zI = (BugZapperConfig.getInstance().getZapperIndex() * 4) + 2;
                this.animate(new long[]{50, 50}, zI, zI + 1, true); //show flash image
                this.playSound();
                this.stopped = true; //stop and hide the animation of this bug
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
        this.animate(new long[]{30, 30}, index, index + 1, true); //animate the flying bug to its next state(frame)
    }


    public boolean isZapperOn() {
        return BugZapperConfig.getInstance().isZapperOn();
    }

    /**
     * Builds the path along which this bug flies. The path is a sequence of points on the screen.
     * When the bug flies, it moves from one point to the next in sequence.
     * @param zapCenterX
     * @param zapCenterY
     * @return
     */
    private LoopEntityModifier getLoopEntityModifier(float zapCenterX, float zapCenterY) {
        List<Integer> radiusList = new ArrayList<Integer>(); //list of radii for approaching the zapper and then flying away from the zapper
        int radius = maxRadius;
        radiusList.add(radius); //the starting point for the bug is at a distance of maxRadius from the center of the zapper
        while(radius != 0 ) { //continue until it reaches the center of the zapper
            int radiusChange = MathUtils.random(1, 10) - 6; //each time the radius is changed by a random value

            if (radiusChange < 0) {
                radiusChange = 0; //if radiusChange is negative, the radius doesn't change and will be the same as previous. This is to avoid quick approaching of the bug towards the zapper.
            }

            radius -= radiusChange;
            if (radius < 0) {
                radius = 0;
            }

            radiusList.add(radius);
        }

        for(int i = radiusList.size() - 2; i >= 0; i--) { //appending the radius list for flying away from the zapper
            radiusList.add(radiusList.get(i));
        }

        int pathCount = radiusList.size(); //number of points on the path of the bug

        float[] pathX = new float[pathCount];
        float[] pathY = new float[pathCount];

        float currentAngle = (float) (Math.random() * Math.PI * 2); //the starting point should be at a random angle relative to the center of the zapper

        for (int k = 0; k < pathCount; k++) {
            currentAngle += MathUtils.random(-0.2f, 0.2f); //every time the bug moves from point to point, its angle changes by a random value.
            pathX[k] = (float) (zapCenterX + Math.cos(currentAngle) * radiusList.get(k)); //Each point on the path is build using the corresponding radius value
            pathY[k] = (float) (zapCenterY + Math.sin(currentAngle) * radiusList.get(k)); //and the corresponding angle
        }

        //if the bug reaches the last point on the path, it goes through the path in reverse order, thefore
        //there will be 2*pathCount points on the PathModifier.Path

        final PathModifier.Path path = new PathModifier.Path(pathCount * 2 - 1);

        for (int i = 0; i < pathCount; i++) { //from initial point to the last point
            path.to(pathX[i], pathY[i]);
        }

        for (int i = pathCount - 2; i >= 0; i--) { //from the last point back to the initial point
            path.to(pathX[i], pathY[i]);
        }

        PathModifier pathModifier = new PathModifier(
                MathUtils.random(50f, 80f), //speed (random) of the bug for moving from one point to another
                path, null, new PathModifier.IPathModifierListener() {

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