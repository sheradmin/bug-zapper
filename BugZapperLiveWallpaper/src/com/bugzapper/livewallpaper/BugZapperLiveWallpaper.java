package com.bugzapper.livewallpaper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.modifier.IModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sher
 */
public class BugZapperLiveWallpaper extends TouchLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener {
    // ===========================================================
    // Constants
    // ===========================================================

    public static final String SHARED_PREFS_NAME = "bugzapperlivewallpapersettings";

    // Camera Constants
    private static int CAMERA_WIDTH = 480;
    private static int CAMERA_HEIGHT = 720;

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * The Scene class is the root container for all objects to be drawn on the screen. A Scene has a specific amount
     * of Layers, which themselves can contain a (fixed or dynamic) amount of Entities. There are subclasses, like the
     * CameraScene/HUD/MenuScene that are drawing themselves to the same position of the Scene no matter where the
     * camera is positioned to.
     */
    private Scene scene = null;
    private AutoParallaxBackground autoParallaxBackground;
    private Texture mTextureCloud;

    private Texture mTextureZapper;
    private Texture mTextureTree;
    private Texture mTextureMoon;
    private Texture mTextureOwl;
    private Texture mTextureBackground;

    private TextureRegion mParallaxLayerCloudOne;
    private TiledTextureRegion mParallaxLayerZapper;
    private TextureRegion mParallaxLayerTree;
    private TiledTextureRegion mParallaxLayerMoon;
    private TiledTextureRegion mParallaxLayerOwl;
    private TextureRegion mParallaxLayerBackground;
    private TextureRegion mParallaxLayerGround;

    /**
     * An OwlSprite is an object that can be drawn, like Sprites, Rectangles, Text or Lines.
     * An OwlSprite has a animation position/rotation/scale/color/etc...
     */
    private OwlSprite owlSprite;

    private Sound owlSound;
    private Sound zapperSound;

    int zapperX = 0;
    int zapperY = 0;
    float zapCenterX = 0f;
    float zapCenterY = 0f;

    /**
     * A Camera defines the rectangle of the scene that is drawn on the screen, as not the whole scene is visible all
     * the time. Usually there is one Camera per Scene, except for the SplitScreenEngines. There are subclasses that
     * allow zooming and smooth position changes of the Camera.
     */
    private Camera mCamera = null;
    private TiledSprite zapperSprite;
    private ScreenOrientation screenOrientation;

    // Shared Preferences
    private SharedPreferences mSharedPreferences;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    /**
     * The Engine make the game proceed in small discrete steps of time. The Engine manages to synchronize a periodic
     * drawing and updating of the Scene, which contains all the content that your game is currently handling actively.
     * There usually is one Scene per Engine, except for the SplitScreenEngines.
     *
     * @return org.anddev.andengine.engine.Engine
     */
    @Override
    public org.anddev.andengine.engine.Engine onLoadEngine() {
        screenOrientation = ScreenOrientation.PORTRAIT;
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, screenOrientation,
                new FillResolutionPolicy(), mCamera).setNeedsSound(true);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);

        return new org.anddev.andengine.engine.Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {
        // Set the Base Texture Path
        TextureRegionFactory.setAssetBasePath("gfx/");
        /**
         * A Texture is a 'image' in the memory of the graphics chip. On Android the width and height of a Texture has
         * to be a power of 2. Therefore AndEngine assembles a Texture from a couple of ITextureSources, so the space
         * can be used better.         *
         */
        this.mTextureCloud = new Texture(1024, 1024, TextureOptions.DEFAULT);

        this.mTextureTree = new Texture(1024, 1024, TextureOptions.BILINEAR);

        this.mTextureMoon = new Texture(1024, 256, TextureOptions.DEFAULT);

        this.mTextureOwl = new Texture(2048, 512, TextureOptions.DEFAULT);

        this.mTextureZapper = new Texture(256, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mTextureBackground = new Texture(512, 1024, TextureOptions.BILINEAR);

        this.mParallaxLayerCloudOne = TextureRegionFactory.createFromAsset(this.mTextureCloud, this, "cloud.png", 0, 180);

        this.mParallaxLayerTree = TextureRegionFactory.createFromAsset(this.mTextureTree, this, "house-tree.png", 0, 180);

        this.mParallaxLayerMoon = TextureRegionFactory.createTiledFromAsset(this.mTextureMoon, this, "moon.png", 0, 0, 4, 1);

        this.mParallaxLayerZapper = TextureRegionFactory.createTiledFromAsset(this.mTextureZapper, this, "zapper.png", 0, 0, 5, 1);

        this.mParallaxLayerOwl = TextureRegionFactory.createTiledFromAsset(this.mTextureOwl, this, "owls.png", 0, 0, 6, 2);

        this.mParallaxLayerBackground = TextureRegionFactory.createFromAsset(this.mTextureBackground, this, "bg.png", 0, 180);

        this.mParallaxLayerGround = TextureRegionFactory.createFromAsset(this.mTextureBackground, this, "ground-trimmed.png", 0, 0);

        SoundFactory.setAssetBasePath("mfx/");
        try {
            this.owlSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "owl.mp3");
            this.zapperSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "bug.ogg");
        } catch (final IOException e) {
            Debug.e(e);
        }

        this.mEngine.getTextureManager().loadTextures(this.mTextureCloud, this.mTextureMoon, this.mTextureTree, this.mTextureBackground, mTextureOwl, mTextureZapper);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, 0);
        prefs.registerOnSharedPreferenceChangeListener(this);
        // initialize the starting preferences
        onSharedPreferenceChanged(prefs, null);

    }

    /**
     * The Scene class is the root container for all objects to be drawn on the screen. A Scene has a specific amount
     * of Layers, which themselves can contain a (fixed or dynamic) amount of Entities. There are subclasses, like the
     * CameraScene/HUD/MenuScene that are drawing themselves to the same position of the Scene no matter where the
     * camera is positioned to.
     *
     * @return Scene
     */
    @Override
    public Scene onLoadScene() {
        scene = new Scene(6);

        final int centerX = (CAMERA_WIDTH - this.mParallaxLayerBackground.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - this.mParallaxLayerBackground.getHeight()) / 2;

        //Sky BEGIN
        detachAndAttachSkySprite();
        //Sky END

        //Zapper Begin
        //A lighted bug zapper hanging on the porch
        zapperX = centerX + 375;
        zapperY = 461;
        zapperSprite = new TiledSprite(zapperX, zapperY, this.mParallaxLayerZapper) {
            /**
             * I was wondering if you could do a small snippet to show how to properly update a sprite. I have added a
             * listener to the zapper and i want it to on/off.
             *
             * @param pSceneTouchEvent
             * @param pTouchAreaLocalX
             * @param pTouchAreaLocalY
             * @return
             */
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
                    if (BugZapperConfig.getInstance().isZapperOn()) {
                        this.setCurrentTileIndex(4);
                        BugZapperConfig.getInstance().setZapperOn(false);
                    } else {
                        this.setCurrentTileIndex(BugZapperConfig.getInstance().getZapperIndex());
                        BugZapperConfig.getInstance().setZapperOn(true);
                    }
                }
                return true;
            }
        };
        zapperSprite.setScale(0.8f);
        //Zapper End

        //Moth Begin
        float zapHeight = zapperSprite.getHeight();
        float zapWidth = zapperSprite.getWidth();
        zapCenterX = zapperX + zapWidth / 2;
        zapCenterY = zapperY + zapHeight / 2;

        detachAndAttachMothSprite();
        //Moth End

        /**
         * Create Owl animated sprite object
         */
        owlSprite = new OwlSprite(105, 280, this.mParallaxLayerOwl) {
            /**
             * I was wondering if you could do a small snippet to show how to properly update a sprite. I have added a
             * listener to the owl and i want it to move my sprite to whatever position is being touched
             *
             * @param pSceneTouchEvent
             * @param pTouchAreaLocalX
             * @param pTouchAreaLocalY
             * @return
             */
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
                    BugZapperLiveWallpaper.this.owlSound.play();
                    this.setStopped(false);
                }
                return true;
            }
        };
        scene.registerUpdateHandler(new TimerHandler(0.2f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                BugZapperLiveWallpaper.this.owlSprite.onManagedUpdateOwl();
            }
        }));

        //Moon Begin
        //Create Moon sprite object.
        //Lighted moon that changes upon touch (full moon, half moon, quarter moon, etc.)
        final TiledSprite moonSprite = new TiledSprite(centerX + 330, centerY, this.mParallaxLayerMoon) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
                    this.nextTile();
                }
                return true;
            }
        };
        moonSprite.setCurrentTileIndex(3);
        //Moon End

        //-	A large tree and a grassy lawn
        Sprite bgSprite = new Sprite(centerX, 0, this.mParallaxLayerBackground);
        Sprite groundSprite = new Sprite(centerX, CAMERA_HEIGHT - this.mParallaxLayerGround.getHeight(), this.mParallaxLayerGround);

        autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.setColor(0, 78, 255);
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, bgSprite));
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, groundSprite));

        scene.getChild(4).attachChild(zapperSprite);
        scene.getChild(3).attachChild(owlSprite);
        scene.getChild(2).attachChild(new Sprite(centerX - 200, CAMERA_HEIGHT - this.mParallaxLayerTree.getHeight(), this.mParallaxLayerTree));
        scene.getChild(0).attachChild(moonSprite);
        scene.setBackground(autoParallaxBackground);

        scene.registerTouchArea(zapperSprite);

        scene.registerTouchArea(moonSprite);

        scene.registerTouchArea(owlSprite);

        scene.setTouchAreaBindingEnabled(true);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, 0);
        prefs.registerOnSharedPreferenceChangeListener(this);
        // initialize the starting preferences
        onSharedPreferenceChanged(prefs, BugZapperSettings.BG_COLOR_KEY);
        onSharedPreferenceChanged(prefs, BugZapperSettings.ZAPPER_COLOR_KEY);

        return scene;
    }

    /**
     * Detach oll bugs and Add new bugs.
     */
    private void detachAndAttachMothSprite() {
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                /* Now it is save to remove the entity! */
                scene.getChild(5).detachChildren();
                createMothSprite();
            }
        });
    }

    private void createMothSprite() {
        int k = BugZapperConfig.getInstance().getMothCount();
        int j = 0;
        for (int i = 0; i < k; i++) {
            if (j > 16) {
                j = 0;
            }
            createMothSprite(j);
            j += 4;
        }
    }

    /**
     * Create new moth.
     *
     * @param j
     */
    public void createMothSprite(int j) {
        Texture mTextureMoth = new Texture(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final TiledTextureRegion mParallaxLayerMoth = TextureRegionFactory.createTiledFromAsset(mTextureMoth, this, "moth_flash9.png", 0, 0, 4, 5);

        this.mEngine.getTextureManager().loadTexture(mTextureMoth);

        final MothSprite mothSprite = new MothSprite(zapCenterX, zapCenterY, zapperX, zapperY, mParallaxLayerMoth, zapperSound, j);
        scene.getChild(5).attachChild(mothSprite);
        mothSprite.setScale(1.2f);
    }

    private void detachAndAttachSkySprite() {
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                /* Now it is save to remove the entity! */
                scene.getChild(1).detachChildren();
                createSkySprite();
            }
        });
    }

    /**
     * Create Cloud
     * Clouds that move in the sky and over the moon
     */
    private void createSkySprite() {
        final Sprite skyOne = new Sprite(0, 100, this.mParallaxLayerCloudOne);

        skyOne.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(BugZapperConfig.getInstance().getSkySpeed(), -900, 900, new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

            }
        })));

        /*final Sprite skyTwo = new Sprite(600, 50, this.mParallaxLayerCloudOne);

        skyTwo.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(BugZapperConfig.getInstance().getSkySpeed(), -800, 900, new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

            }
        })));*/

        scene.getChild(1).attachChild(skyOne);
//        scene.getChild(1).attachChild(skyTwo);
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public Engine onCreateEngine() {
        return super.onCreateEngine();
    }

    @Override
    public void onPauseGame() {

    }

    @Override
    public void onResumeGame() {

    }

    /**
     * Interface definition for a callback to be invoked when a shared preference is changed.
     *
     * @param sharedPrefs
     * @param pKey
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String pKey) {
        if (pKey == null || BugZapperSettings.SOUND_MOTH_KEY.equals(pKey)) {
            BugZapperConfig.getInstance().setMothSoundOn(sharedPrefs.getBoolean(BugZapperSettings.SOUND_MOTH_KEY, true));
        }
        if (pKey == null || BugZapperSettings.BG_COLOR_KEY.equals(pKey)) {
            int i = sharedPrefs.getInt(BugZapperSettings.BG_COLOR_KEY, 1);
            if (autoParallaxBackground != null) {
                autoParallaxBackground.setColor(Color.red(i), Color.green(i), Color.blue(i));
            }
        }
        if (pKey == null || BugZapperSettings.MOTH_COUNT_KEY.equals(pKey)) {
            BugZapperConfig.getInstance().setMothCount(Integer.parseInt(sharedPrefs.getString(BugZapperSettings.MOTH_COUNT_KEY, "5")));
            if (pKey != null) {
                detachAndAttachMothSprite();
            }
        }
        if ((pKey == null) || BugZapperSettings.SKY_SPEED_KEY.equals(pKey)) {
            BugZapperConfig.getInstance().setSkySpeed(Integer.parseInt(sharedPrefs.getString(BugZapperSettings.SKY_SPEED_KEY, "60")));
            if (pKey != null) {
                detachAndAttachSkySprite();
            }
        }
        if ((pKey == null) || BugZapperSettings.ZAPPER_COLOR_KEY.equals(pKey)) {
            BugZapperConfig.getInstance().setZapperIndex(Integer.parseInt(sharedPrefs.getString(BugZapperSettings.ZAPPER_COLOR_KEY, "0")));
            if (pKey != null) {
                if (BugZapperConfig.getInstance().isZapperOn()) {
                    zapperSprite.setCurrentTileIndex(Integer.parseInt(sharedPrefs.getString(BugZapperSettings.ZAPPER_COLOR_KEY, "0")));
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (owlSprite != null) {
            owlSprite.resetOwl();
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * Scroll with home screen
     *
     * @param xOffset
     * @param yOffset
     * @param xOffsetStep
     * @param yOffsetStep
     * @param xPixelOffset
     * @param yPixelOffset
     * @param preview
     */
    @Override
    public void offsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                               float yOffsetStep, int xPixelOffset, int yPixelOffset, boolean preview) {
        if (mCamera != null) {
            // Emulator has 3 screens
            if (!preview) {
                mCamera.setCenter(((960 * xOffset) - 240), mCamera.getCenterY());
            } else {
                mCamera.setCenter(CAMERA_WIDTH / 2, mCamera.getCenterY());
            }
        }

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}