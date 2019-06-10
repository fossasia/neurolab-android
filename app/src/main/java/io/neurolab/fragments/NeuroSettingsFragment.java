package io.neurolab.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.EditTextPreference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import io.neurolab.R;

public class NeuroSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_SAMPLES = "samples";
    public static final String KEY_BINS = "bins";
    public static final String KEY_CHANNELS = "numChannels";

    private EditTextPreference samplesPref;
    private EditTextPreference binsPref;
    private EditTextPreference channelsPref;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_neuro_settings, rootKey);
        // Assign preferences to use in this class
        samplesPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_SAMPLES);
        binsPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_BINS);
        channelsPref = (EditTextPreference) getPreferenceScreen().findPreference(KEY_CHANNELS);
        // Fetch related shared preferences
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            // TODO: Set limits to following preferences
            case KEY_SAMPLES:
                samplesPref.setSummary(samplesPref.getText() + " sample" +
                        pluralize(Integer.valueOf(samplesPref.getText())));
                break;
            case KEY_BINS:
                binsPref.setSummary(binsPref.getText() + " bin" +
                        pluralize(Integer.valueOf(binsPref.getText())));
                break;
            case KEY_CHANNELS:
                channelsPref.setSummary(channelsPref.getText() + " channel" +
                        pluralize(Integer.valueOf(channelsPref.getText())));
                break;
            default:
                break;
        }
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
    public void onResume() {
        super.onResume();
        samplesPref.setSummary(samplesPref.getText() + " sample" +
                pluralize(Integer.valueOf(samplesPref.getText())));
        binsPref.setSummary(binsPref.getText() + " bin" +
                pluralize(Integer.valueOf(binsPref.getText())));
        channelsPref.setSummary(channelsPref.getText() + " channel" +
                pluralize(Integer.valueOf(channelsPref.getText())));
    }

    private String pluralize(int count) {
        return count > 1 ? "s" : "";
    }

}
