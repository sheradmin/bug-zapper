package com.bugzapper.livewallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by IntelliJ IDEA.
 * User: JAX
 * Date: 7/12/11
 * Time: 12:41 AM
 * To change this template use File | Settings | File Templates.
 */

public class LiveWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new SampleEngine();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class SampleEngine extends Engine {


        SampleEngine() {

        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
        }
    }
}