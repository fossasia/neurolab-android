package io.neurolab.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import io.neurolab.R;

public class FeedbackSettings extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private int smpls_per_sec;
    private int bins;
    private int num_channels;
    private TextView smpls_txt_view;
    private TextView bins_txt_view;
    private TextView numChannels_txt_view;

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }
    public FeedbackSettings() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedback_settings);
        Toolbar toolbar = findViewById(R.id.feedback_toolbar);
        FeedbackSettings activity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setActionBar(toolbar);
        }
        activity.getActionBar().setTitle(getResources().getString(R.string.app_name));
        activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getActionBar().setDisplayShowHomeEnabled(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        smpls_per_sec = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.samples_pref_key), "4"));
        bins = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.bins_pref_key), "3"));
        num_channels = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.num_channels_pref_key), "2"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        smpls_txt_view = findViewById(R.id.feedback_smpls_value);
        bins_txt_view = findViewById(R.id.feedback_bins_value);
        numChannels_txt_view = findViewById(R.id.feedback_numChannels_value);

        smpls_txt_view.setText(String.valueOf(smpls_per_sec));
        bins_txt_view.setText(String.valueOf(bins));
        numChannels_txt_view.setText(String.valueOf(num_channels));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == getResources().getString(R.string.samples_pref_key))
            smpls_per_sec = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.samples_pref_key), "4"));
        else if (key == getResources().getString(R.string.bins_pref_key))
            bins = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.bins_pref_key), "3"));
        else if (key == getResources().getString(R.string.num_channels_pref_key))
            num_channels = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.num_channels_pref_key), "2"));
    }
}
