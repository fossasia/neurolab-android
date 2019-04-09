package io.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import io.neurolab.tools.Animations;
import io.neurolab.visuals.SpaceAnimationVisuals;

public class ProgramModeActivity extends AppCompatActivity {

    public static final int FOCUS_PROGRAM_MODE = 1;
    public static final int RELAX_PROGRAM_MODE = 2;
    public static final int VJ_PROGRAM_MODE = 3;
    public static final int SERIAL_PROGRAM_MODE = 4;

    public static final String INTENT_KEY_PROGRAM_MODE = "MODE";
    public static final String SETTING_SIMULATION = "SETTING_SIMULATION";
    public static final String SETTING_LOAD_RESOURCES_FROM_PHN = "SETTING_LOAD_RESOURCES_FROM_PHN";
    public static final String SETTING_AUDIO_FEEDBACK = "SETTING_AUDIO_FEEDBACK";
    public static final String SETTING_24BIT = "SETTING_24BIT";
    public static final String SETTING_ADVANCED = "SETTING_ADVANCED";

    private ImageView rocketimage;
    private int lastPos = 0;
    private int newPos = -300;
    private boolean moving = false;

    private boolean settingSimulation;
    private boolean settingLoadResourcesFromPhn;
    private boolean settingAudioFeedback;
    private boolean setting24bit;
    private boolean settingAdvanced;

    private View layoutMainScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_program_mode);

        rocketimage = findViewById(R.id.rocketimage);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int mode = bundle.getInt(INTENT_KEY_PROGRAM_MODE);
        settingSimulation = bundle.getBoolean(SETTING_SIMULATION);
        settingLoadResourcesFromPhn = bundle.getBoolean(SETTING_LOAD_RESOURCES_FROM_PHN);
        settingAudioFeedback = bundle.getBoolean(SETTING_AUDIO_FEEDBACK);
        setting24bit = bundle.getBoolean(SETTING_24BIT);
        settingAdvanced = bundle.getBoolean(SETTING_ADVANCED);

        switch (mode) {
            case FOCUS_PROGRAM_MODE:
                setTitle(R.string.focus);
                setupFocusScreen();
                break;
            case RELAX_PROGRAM_MODE:
                setTitle(R.string.relax);
                setupRelaxScreen();
                break;
            case VJ_PROGRAM_MODE:
                setTitle(R.string.vj);
                break;
            case SERIAL_PROGRAM_MODE:
                setTitle(R.string.serial);
                break;
        }
    }

    private void setupFocusScreen() {
        layoutMainScreen = findViewById(R.id.layoutFocusScreen);
        layoutMainScreen.setVisibility(View.VISIBLE);
      
        SpaceAnimationVisuals.moveRocket(rocketimage, lastPos, newPos, moving);
    }

    private void setupRelaxScreen() {
        layoutMainScreen = findViewById(R.id.layoutRelaxScreen);
        layoutMainScreen.setVisibility(View.VISIBLE);

        Animations.rotateView(findViewById(R.id.yantraOneImageView), 360f, 0);
        Animations.rotateView(findViewById(R.id.yantraTwoImageView), 0, 360f);
    }
  
}
