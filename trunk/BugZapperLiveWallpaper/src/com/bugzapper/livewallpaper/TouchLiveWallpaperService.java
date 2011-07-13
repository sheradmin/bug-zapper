package com.bugzapper.livewallpaper;

import net.rbgrn.opengl.GLWallpaperService;

import org.anddev.andengine.audio.music.MusicManager;
import org.anddev.andengine.audio.sound.SoundManager;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.view.GLSurfaceView.Renderer;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.sensor.accelerometer.AccelerometerSensorOptions;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.sensor.location.ILocationListener;
import org.anddev.andengine.sensor.location.LocationSensorOptions;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationSensorOptions;
import org.anddev.andengine.ui.IGameInterface;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * @author Sher
 */
public abstract class TouchLiveWallpaperService extends GLWallpaperService
		implements IGameInterface, IOffsetsChanged {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected org.anddev.andengine.engine.Engine mEngine;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate() {
		super.onCreate();

		this.mEngine = this.onLoadEngine();
		this.applyEngineOptions(this.mEngine.getEngineOptions());

		this.onLoadResources();
		final Scene scene = this.onLoadScene();
		this.mEngine.onLoadComplete(scene);
		this.onLoadComplete();
		this.mEngine.start();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public org.anddev.andengine.engine.Engine getEngine() {
		return this.mEngine;
	}

	public SoundManager getSoundManager() {
		return this.mEngine.getSoundManager();
	}

	public MusicManager getMusicManager() {
		return this.mEngine.getMusicManager();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected void onPause() {
		this.mEngine.stop();
//		this.onGamePaused();
	}

	protected void onResume() {
		// Log.d("Echo", "TouchLiveWallpaperService: resuming");
		this.mEngine.start();
//		this.onGameResumed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.mEngine.interruptUpdateThread();

		this.onUnloadResources();
	}

	@Override
	public Engine onCreateEngine() {
		return new BaseWallpaperGLEngine(this);
	}

	/*@Override
	public void onGamePaused() {

	}

	@Override
	public void onGameResumed() {

	}*/

	@Override
	public void onUnloadResources() {
		if (this.mEngine.getEngineOptions().needsMusic()) {
			this.getMusicManager().releaseAll();
		}
		if (this.mEngine.getEngineOptions().needsSound()) {
			this.getSoundManager().releaseAll();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void runOnUpdateThread(final Runnable pRunnable) {
		this.mEngine.runOnUpdateThread(pRunnable);
	}

	protected void onTap(final int pX, final int pY) {
		this.mEngine.onTouch(null, MotionEvent.obtain(SystemClock
				.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_DOWN, pX, pY, 0));
	}

	protected void onDrop(final int pX, final int pY) {

	}

	protected void applyEngineOptions(final EngineOptions pEngineOptions) {

	}

	protected boolean enableVibrator() {
		return this.mEngine.enableVibrator(this);
	}

	protected boolean enableAccelerometerSensor(
			final IAccelerometerListener pAccelerometerListener) {
		return this.mEngine.enableAccelerometerSensor(this,
				pAccelerometerListener);
	}

	protected boolean enableAccelerometerSensor(
			final IAccelerometerListener pAccelerometerListener,
			final AccelerometerSensorOptions pAccelerometerSensorOptions) {
		return this.mEngine.enableAccelerometerSensor(this,
				pAccelerometerListener, pAccelerometerSensorOptions);
	}

	protected boolean enableOrientationSensor(
			final IOrientationListener pOrientationListener) {
		return this.mEngine.enableOrientationSensor(this, pOrientationListener);
	}

	protected boolean enableOrientationSensor(
			final IOrientationListener pOrientationListener,
			final OrientationSensorOptions pOrientationSensorOptions) {
		return this.mEngine.enableOrientationSensor(this, pOrientationListener,
				pOrientationSensorOptions);
	}

	/**
	 * @return <code>true</code> when the sensor was successfully disabled,
	 *         <code>false</code> otherwise.
	 */
	protected boolean disableOrientationSensor() {
		return this.mEngine.disableOrientationSensor(this);
	}

	protected void enableLocationSensor(
			final ILocationListener pLocationListener,
			final LocationSensorOptions pLocationSensorOptions) {
		this.mEngine.enableLocationSensor(this, pLocationListener,
				pLocationSensorOptions);
	}

	protected void disableLocationSensor() {
		this.mEngine.disableLocationSensor(this);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	protected class BaseWallpaperGLEngine extends GLEngine {
		// ===========================================================
		// Fields
		// ===========================================================

		private Renderer mRenderer;
		private IOffsetsChanged mOffsetsChangedListener = null;

		// ===========================================================
		// Constructors
		// ===========================================================

		public BaseWallpaperGLEngine(IOffsetsChanged pOffsetsChangedListener) {
			this.setEGLConfigChooser(false);
			this.mRenderer = new RenderSurfaceView.Renderer(
					TouchLiveWallpaperService.this.mEngine);
			this.setRenderer(this.mRenderer);
			this.setRenderMode(RENDERMODE_CONTINUOUSLY);
			this.setTouchEventsEnabled(true);
			this.mOffsetsChangedListener = pOffsetsChangedListener;
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public Bundle onCommand(final String pAction, final int pX,
				final int pY, final int pZ, final Bundle pExtras,
				final boolean pResultRequested) {
			if (pAction.equals(WallpaperManager.COMMAND_TAP)) {
				TouchLiveWallpaperService.this.onTap(pX, pY);
			} else if (pAction.equals(WallpaperManager.COMMAND_DROP)) {
				TouchLiveWallpaperService.this.onDrop(pX, pY);
			}

			return super.onCommand(pAction, pX, pY, pZ, pExtras,
					pResultRequested);
		}

        public boolean isWallpaperPreview(){
            return this.isPreview();
        }

		@Override
		public void onTouchEvent(MotionEvent pEvent) {
			if (this.isPreview()
					&& pEvent.getAction() == MotionEvent.ACTION_DOWN)
				TouchLiveWallpaperService.this.mEngine.onTouch(null, pEvent);
		}

		@Override
		public void onResume() {
			super.onResume();
			TouchLiveWallpaperService.this.getEngine().onResume();
			TouchLiveWallpaperService.this.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
			TouchLiveWallpaperService.this.getEngine().onPause();
			TouchLiveWallpaperService.this.onPause();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			if (this.mRenderer != null) {
				// mRenderer.release();
			}
			this.mRenderer = null;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			// TODO Auto-generated method stub
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);

			if (this.mOffsetsChangedListener != null)
				this.mOffsetsChangedListener.offsetsChanged(xOffset, yOffset,
						xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset,isPreview());
		}
	}
}