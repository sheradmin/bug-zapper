package com.bugzapper.livewallpaper;

/**
 * Created by IntelliJ IDEA.
 * User: JAX
 * Date: 6/27/11
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BugZapperConfig {
    private static BugZapperConfig ourInstance = new BugZapperConfig();

    public static BugZapperConfig getInstance() {
        return ourInstance;
    }

    private BugZapperConfig() {
    }

    private boolean zapperOn = true;
    private int zapperIndex = 0;
    private boolean mothSoundOn = true;
    private int mothCount = 5;
    private int skySpeed = 60;

    public boolean isZapperOn() {
        return zapperOn;
    }

    public void setZapperOn(boolean zapperOn) {
        this.zapperOn = zapperOn;
    }

    public boolean isMothSoundOn() {
        return mothSoundOn;
    }

    public void setMothSoundOn(boolean mothSoundOn) {
        this.mothSoundOn = mothSoundOn;
    }

    public int getMothCount() {
        return mothCount;
    }

    public void setMothCount(int mothCount) {
        this.mothCount = mothCount;
    }

    public int getSkySpeed() {
        return skySpeed;
    }

    public void setSkySpeed(int skySpeed) {
        this.skySpeed = skySpeed;
    }

    public int getZapperIndex() {
        return zapperIndex;
    }

    public void setZapperIndex(int zapperIndex) {
        this.zapperIndex = zapperIndex;
    }
}
