package io.neurolab.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import io.neurolab.R;
import io.neurolab.main.NeuroLab;

import static io.neurolab.main.NeuroLab.DEV_MODE_KEY;

public class ConfigFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private SwitchPreference developerModeCheck;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_config_settings, s);

        developerModeCheck = (SwitchPreference) getPreferenceScreen().findPreference(DEV_MODE_KEY);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        developerModeCheck.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case DEV_MODE_KEY:
                if (!developerModeCheck.isChecked())
                    NeuroLab.developerMode = true;
                else {
                    NeuroLab.developerMode = false;
                    Toast.makeText(getActivity(), R.string.dev_mode_msg, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }
}
