package io.neurolab;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;

public class NeuroLauncherFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {
    private SharedPreferences sharedPreferences;

    private boolean simulation;
    private boolean loadResources;
    private boolean audioFeedback;
    private boolean mode24bit;
    private boolean modeAdvanced;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.launcher_preference_screen);
        Preference preference = findPreference(getResources().getString(R.string.simulation));
        preference.setOnPreferenceChangeListener(this);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       if(key.equals(getResources().getString(R.string.simulation))){
           simulation = sharedPreferences.getBoolean(key, false);
       }else if(key.equals(getResources().getString(R.string.load_from_phone))){
           loadResources = sharedPreferences.getBoolean(key, false);
       }else if(key.equals(getResources().getString(R.string.audio_feedback))){
           audioFeedback = sharedPreferences.getBoolean(key, false);
       }else if(key.equals(getResources().getString(R.string.mode_24bit))){
           mode24bit = sharedPreferences.getBoolean(key, false);
       }else {
           modeAdvanced = sharedPreferences.getBoolean(key, false);
       }
    }

    public boolean isSimulation() {
        return simulation;
    }

    public boolean isLoadResources() {
        return loadResources;
    }

    public boolean isAudioFeedback() {
        return audioFeedback;
    }

    public boolean isMode24bit() {
        return mode24bit;
    }

    public boolean isModeAdvanced() {
        return modeAdvanced;
    }
}
