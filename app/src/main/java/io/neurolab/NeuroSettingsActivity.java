package io.neurolab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NeuroSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neurosettings);
        setTitle(getResources().getString(R.string.action_settings));
    }
}
