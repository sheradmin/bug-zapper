package com.bugzapper.livewallpaper;

/**
 * @author Sher
 */
public interface IOffsetsChanged {

    public void offsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
                               int yPixelOffset, boolean preview);
}
