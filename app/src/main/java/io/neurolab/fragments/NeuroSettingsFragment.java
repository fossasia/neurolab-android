package io.neurolab.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import io.neurolab.R;

public class NeuroSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, TextView.OnEditorActionListener {

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

        if (samplesPref.getText().isEmpty()) {
            samplesPref.setText("3");
            Toast.makeText(getActivity(), "Enter valid input !", Toast.LENGTH_SHORT).show();
        }
        if (binsPref.getText().isEmpty()) {
            binsPref.setText("4");
            Toast.makeText(getActivity(), "Enter valid input !", Toast.LENGTH_SHORT).show();
        }
        if (channelsPref.getText().isEmpty()) {
            channelsPref.setText("2");
            Toast.makeText(getActivity(), "Enter valid input !", Toast.LENGTH_SHORT).show();
        }
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
        samplesPref.getEditText().setOnEditorActionListener(this);
        binsPref.getEditText().setOnEditorActionListener(this);
        channelsPref.getEditText().setOnEditorActionListener(this);
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }

}
