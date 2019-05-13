package io.neurolab.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.neurolab.R;

public class NeuroSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neurosettings);
        setTitle(getResources().getString(R.string.action_settings));
    }
}
