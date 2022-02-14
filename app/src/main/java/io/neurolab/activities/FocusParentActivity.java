package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import io.neurolab.R;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.main.NeuroLab;

public class FocusParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_parent);
        setTitle(R.string.focus);

        FloatingActionButton gameButton = findViewById(R.id.play_focus_game);

        gameButton.setOnClickListener(v -> startProgramModeActivity(FocusVisualFragment.FOCUS_FLAG));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void startProgramModeActivity(String mode) {
        Intent intent = new Intent(this, ProgramModeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, mode);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, NeuroLab.class));
        finish();
    }
}
