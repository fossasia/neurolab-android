package io.neurolab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import io.neurolab.visuals.SpaceAnimationVisuals;

public class ProgramModeActivity extends AppCompatActivity {

    public static final int FOCUS_PROGRAM_MODE = 1;
    public static final int RELAX_PROGRAM_MODE = 2;
    public static final int VJ_PROGRAM_MODE = 3;
    public static final int SERIAL_PROGRAM_MODE = 4;

    public static final String INTENT_KEY_PROGRAM_MODE = "MODE";

    private ImageView rocketimage;
    private int lastPos = 0;
    private int newPos = -300;
    private boolean moving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_mode);

        rocketimage = findViewById(R.id.rocketimage);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int mode = bundle.getInt(INTENT_KEY_PROGRAM_MODE);

        switch (mode){
            case FOCUS_PROGRAM_MODE:
                setTitle(R.string.focus);
                break;
            case RELAX_PROGRAM_MODE:
                setTitle(R.string.relax);
                break;
            case VJ_PROGRAM_MODE:
                setTitle(R.string.vj);
                break;
            case SERIAL_PROGRAM_MODE:
                setTitle(R.string.serial);
                break;
        }
        SpaceAnimationVisuals.moveRocket(rocketimage, lastPos, newPos, moving);
    }
}
