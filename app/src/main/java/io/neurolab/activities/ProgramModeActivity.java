package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import io.neurolab.R;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.fragments.RelaxVisualFragment;
import io.neurolab.main.NeuroLab;

import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

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
    private int mode;

//    Config parameters to be implemented in future
//    private boolean settingSimulation;
//    private boolean settingLoadResourcesFromPhn;
//    private boolean settingAudioFeedback;
//    private boolean setting24bit;
//    private boolean settingAdvanced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_program_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String modeFlag = bundle.getString(INTENT_KEY_PROGRAM_MODE);
        if (modeFlag.equals(FocusVisualFragment.FOCUS_FLAG))
            mode = FOCUS_PROGRAM_MODE;
        else if (modeFlag.equals(MemoryGraphParent.MEMORY_GRAPH_FLAG))
            mode = MEMORY_GRAPH_MODE;
        else if (modeFlag.equals(RelaxVisualFragment.RELAX_PROGRAM_FLAG))
            mode = RELAX_PROGRAM_MODE;

//        settingSimulation = bundle.getBoolean(SETTING_SIMULATION);
//        settingLoadResourcesFromPhn = bundle.getBoolean(SETTING_LOAD_RESOURCES_FROM_PHN);
//        settingAudioFeedback = bundle.getBoolean(SETTING_AUDIO_FEEDBACK);
//        setting24bit = bundle.getBoolean(SETTING_24BIT);
//        settingAdvanced = bundle.getBoolean(SETTING_ADVANCED);

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
                Intent memIntent = new Intent(this, MemoryGraphParent.class);
                memIntent.putExtra(LOG_FILE_KEY, bundle.getString(LOG_FILE_KEY));
                startActivity(memIntent);
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

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mode == FOCUS_PROGRAM_MODE)
            startActivity(new Intent(this, FocusParentActivity.class));
        else if (mode == RELAX_PROGRAM_MODE)
            startActivity(new Intent(this, RelaxParentActivity.class));
        else
            startActivity(new Intent(this, NeuroLab.class));
        finish();
    }
}
