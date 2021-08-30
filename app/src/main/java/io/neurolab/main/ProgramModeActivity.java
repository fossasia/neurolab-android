package io.neurolab.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import io.neurolab.R;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.fragments.RelaxVisualFragment;

public class ProgramModeActivity extends AppCompatActivity {

    public static final int FOCUS_PROGRAM_MODE = 1;
    public static final int RELAX_PROGRAM_MODE = 2;
    public static final int MEMORY_GRAPH_MODE = 3;

    public static final String INTENT_KEY_PROGRAM_MODE = "MODE";
    public static final String SETTING_SIMULATION = "SETTING_SIMULATION";
    public static final String SETTING_LOAD_RESOURCES_FROM_PHN = "SETTING_LOAD_RESOURCES_FROM_PHN";
    public static final String SETTING_AUDIO_FEEDBACK = "SETTING_AUDIO_FEEDBACK";
    public static final String SETTING_24BIT = "SETTING_24BIT";
    public static final String SETTING_ADVANCED = "SETTING_ADVANCED";
    public static final String PROGRAM_FLAG_KEY = "FLAG";

    private boolean settingSimulation;
    private boolean settingLoadResourcesFromPhn;
    private boolean settingAudioFeedback;
    private boolean setting24bit;
    private boolean settingAdvanced;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_program_mode);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String modeFlag = bundle.getString(INTENT_KEY_PROGRAM_MODE);
        if (modeFlag.equals(FocusVisualFragment.FOCUS_FLAG))
            mode = FOCUS_PROGRAM_MODE;
        else if (modeFlag.equals(MemoryGraphParent.MEMORY_GRAPH_FLAG))
            mode = MEMORY_GRAPH_MODE;
        else if (modeFlag.equals(RelaxVisualFragment.RELAX_PROGRAM_FLAG))
            mode = RELAX_PROGRAM_MODE;
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
                if (bundle.getString(FocusVisualFragment.FOCUS_FLAG) != null)
                    fragment.setArguments(bundle);
                moveToFragment(fragment);
                break;
            case RELAX_PROGRAM_MODE:
                setTitle(R.string.relax);
                fragment = new RelaxVisualFragment();
                if (bundle.getString(RelaxVisualFragment.RELAX_PROGRAM_FLAG) != null)
                    fragment.setArguments(bundle);
                moveToFragment(fragment);
                break;
            case MEMORY_GRAPH_MODE:
                setTitle(R.string.mem_graph);
                startActivity(new Intent(this, MemoryGraphParent.class));
                finish();
                break;
            default:
                break;
        }
    }

    private void moveToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commit();
    }
}
