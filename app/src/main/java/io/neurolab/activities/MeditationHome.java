package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import io.neurolab.R;
import io.neurolab.main.NeuroLab;

public class MeditationHome extends AppCompatActivity {

    public static final String MEDITATION_DIR_KEY = "MEDITATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_home);
        setTitle(R.string.meditation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView happinessView = findViewById(R.id.happy_card);
        CardView depressionView = findViewById(R.id.depression_card);
        CardView sleepView = findViewById(R.id.sleep_card);
        CardView travelView = findViewById(R.id.travel_card);
        CardView shBreakView = findViewById(R.id.sh_break_card);
        CardView lgBreakView = findViewById(R.id.lg_break_card);

        setMeditationCategoryIntent(happinessView);
        setMeditationCategoryIntent(depressionView);
        setMeditationCategoryIntent(sleepView);
        setMeditationCategoryIntent(travelView);
        setMeditationCategoryIntent(shBreakView);
        setMeditationCategoryIntent(lgBreakView);
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

    private void setMeditationCategoryIntent(CardView view) {
        view.setOnClickListener(v -> {
                    startActivity(new Intent(MeditationHome.this, MeditationListActivity.class));
                    finish();
                }
        );
    }
}
