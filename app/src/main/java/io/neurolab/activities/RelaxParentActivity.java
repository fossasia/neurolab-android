package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import io.neurolab.R;
import io.neurolab.fragments.RelaxVisualFragment;
import io.neurolab.main.NeuroLab;

public class RelaxParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relax_parent);
        setTitle(R.string.relax);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton gameButton = findViewById(R.id.play_relax_illusion);

        gameButton.setOnClickListener(v -> startProgramModeActivity(RelaxVisualFragment.RELAX_PROGRAM_FLAG));
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
