package io.neurolab.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.neurolab.R;
import io.neurolab.fragments.ConfigFragment;
import io.neurolab.fragments.NeuroSettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.action_settings));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_edit_text_preferences, new NeuroSettingsFragment())
                .add(R.id.container_config_params, new ConfigFragment())
                .commit();
    }

}
