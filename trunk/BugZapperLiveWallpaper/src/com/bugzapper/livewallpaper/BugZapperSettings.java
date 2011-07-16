package com.bugzapper.livewallpaper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * For working with application settings
 */
public class BugZapperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String SOUND_MOTH_KEY = "sound_moth";
    public static final String BG_COLOR_KEY = "custom_bg_color";
    public static final String MOTH_COUNT_KEY = "moth_count";
    public static final String SKY_SPEED_KEY = "sky_speed";
    public static final String ZAPPER_COLOR_KEY = "zapper_color";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(BugZapperLiveWallpaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.wallpaper_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        final Preference customBgColorPref = (Preference) findPreference(BG_COLOR_KEY);
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                SharedPreferences customSharedPreference = getSharedPreferences(BugZapperLiveWallpaper.SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = customSharedPreference.edit();
                editor.putInt(BugZapperSettings.BG_COLOR_KEY, color);
                editor.commit();
                customBgColorPref.setDefaultValue(color);
            }
        }, 2);


        customBgColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                colorPickerDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

}
