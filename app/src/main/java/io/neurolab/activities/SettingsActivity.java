package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import io.neurolab.R;
import io.neurolab.fragments.ConfigFragment;
import io.neurolab.fragments.NeuroSettingsFragment;
import io.neurolab.main.NeuroLab;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, NeuroLab.class));
        finish();
    }
}
