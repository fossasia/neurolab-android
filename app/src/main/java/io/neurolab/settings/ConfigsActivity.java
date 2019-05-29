package io.neurolab.settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import io.neurolab.R;
import io.neurolab.model.ConfigUtils;

public class ConfigsActivity extends AppCompatActivity {
    private CheckBox audioFeedbackCb;
    private CheckBox bit24Cb;
    private CheckBox advancedModeCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configs);

        audioFeedbackCb = findViewById(R.id.cb_audio_feedback);
        bit24Cb = findViewById(R.id.cb_24bit);
        advancedModeCb = findViewById(R.id.cb_advanced_mode);

        audioFeedbackCb.setOnCheckedChangeListener((v, isChecked) ->
                ConfigUtils.saveSettingsConfig(this, modifyConfigurationSettings(v, isChecked)));
        bit24Cb.setOnCheckedChangeListener((v, isChecked) ->
                ConfigUtils.saveSettingsConfig(this, modifyConfigurationSettings(v, isChecked)));
        advancedModeCb.setOnCheckedChangeListener((v, isChecked) ->
                ConfigUtils.saveSettingsConfig(this, modifyConfigurationSettings(v, isChecked)));

        ConfigurationSettings configurationSettings = ConfigUtils.loadSettingsConfig(this);

        audioFeedbackCb.setChecked(configurationSettings.getServerSettings().isAudioFeedback());
        bit24Cb.setChecked(configurationSettings.getServerSettings().isBit24());
        advancedModeCb.setChecked(configurationSettings.getServerSettings().isAdvancedMode());
    }

    private ConfigurationSettings modifyConfigurationSettings(View v, boolean isChecked) {
        ConfigurationSettings configurationSettings = ConfigUtils.loadSettingsConfig(this);
        switch (v.getId()) {
            case R.id.cb_audio_feedback:
                configurationSettings.getServerSettings().setAudioFeedback(isChecked);
                break;
            case R.id.cb_24bit:
                configurationSettings.getServerSettings().setBit24(isChecked);
                break;
            case R.id.cb_advanced_mode:
                configurationSettings.getServerSettings().setAdvancedMode(isChecked);
                break;
            default:
        }
        return configurationSettings;
    }
}
