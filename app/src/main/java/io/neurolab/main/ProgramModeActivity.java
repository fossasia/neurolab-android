package io.neurolab.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import io.neurolab.R;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.fragments.RelaxVisualFragment;

public class ProgramModeActivity extends AppCompatActivity {

    public static final int FOCUS_PROGRAM_MODE = 1;
    public static final int RELAX_PROGRAM_MODE = 2;
    public static final int MEMORY_GRAPH_MODE = 3;
    public static final int SERIAL_PROGRAM_MODE = 4;

    public static final String INTENT_KEY_PROGRAM_MODE = "MODE";
    public static final String SETTING_SIMULATION = "SETTING_SIMULATION";
    public static final String SETTING_LOAD_RESOURCES_FROM_PHN = "SETTING_LOAD_RESOURCES_FROM_PHN";
    public static final String SETTING_AUDIO_FEEDBACK = "SETTING_AUDIO_FEEDBACK";
    public static final String SETTING_24BIT = "SETTING_24BIT";
    public static final String SETTING_ADVANCED = "SETTING_ADVANCED";

    private boolean settingSimulation;
    private boolean settingLoadResourcesFromPhn;
    private boolean settingAudioFeedback;
    private boolean setting24bit;
    private boolean settingAdvanced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_program_mode);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int mode = bundle.getInt(INTENT_KEY_PROGRAM_MODE);
        settingSimulation = bundle.getBoolean(SETTING_SIMULATION);
        settingLoadResourcesFromPhn = bundle.getBoolean(SETTING_LOAD_RESOURCES_FROM_PHN);
        settingAudioFeedback = bundle.getBoolean(SETTING_AUDIO_FEEDBACK);
        setting24bit = bundle.getBoolean(SETTING_24BIT);
        settingAdvanced = bundle.getBoolean(SETTING_ADVANCED);

        Fragment fragment;

        switch (mode) {
            case FOCUS_PROGRAM_MODE:
                setTitle(R.string.focus);
                fragment = new FocusVisualFragment();
                moveToFragment(fragment);
                break;
            case RELAX_PROGRAM_MODE:
                setTitle(R.string.relax);
                fragment = new RelaxVisualFragment();
                moveToFragment(fragment);
                break;
            case MEMORY_GRAPH_MODE:
                setTitle(R.string.mem_graph);
                break;
            case SERIAL_PROGRAM_MODE:
                setTitle(R.string.serial);
                break;
        }
    }

    private void moveToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
    }

}
