package io.neurolab.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.neurolab.R;

public class ConfigFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_config_settings, s);
    }

}
