package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.neurolab.R;
import io.neurolab.adapters.MeditationCardAdapter;
import io.neurolab.main.NeuroLab;
import io.neurolab.model.MeditationCardData;

public class MeditationHome extends AppCompatActivity {

    public static final String MEDITATION_DIR_KEY = "MEDITATION";
    private List<MeditationCardData> mtest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_home);
        setTitle(R.string.meditation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        CardView happinessView = findViewById(R.id.happy_card);
//        CardView depressionView = findViewById(R.id.depression_card);
//        CardView sleepView = findViewById(R.id.sleep_card);
//        CardView travelView = findViewById(R.id.travel_card);
//        CardView shBreakView = findViewById(R.id.sh_break_card);
//        CardView lgBreakView = findViewById(R.id.lg_break_card);
//
//        setMeditationCategoryIntent(happinessView);
//        setMeditationCategoryIntent(depressionView);
//        setMeditationCategoryIntent(sleepView);
//        setMeditationCategoryIntent(travelView);
//        setMeditationCategoryIntent(shBreakView);
//        setMeditationCategoryIntent(lgBreakView);

        mtest = new ArrayList<>();

        mtest.add(new MeditationCardData(R.drawable.ic_happiness, "Happiness" ,  "Meditations while you are in a happy mood"));
        mtest.add(new MeditationCardData(R.drawable.ic_depressed,"Depression" ,  "Meditations when you feel depressed"));
        mtest.add(new MeditationCardData(R.drawable.ic_sleep,"Sleep" ,  "Meditations while having trouble in sleeping"));
        mtest.add(new MeditationCardData(R.drawable.ic_travel,"Travel" ,  "Meditations when you are travelling"));
        mtest.add(new MeditationCardData(R.drawable.ic_sh_break,"Short-Breaks" ,  "Meditations when you are on short breaks"));
        mtest.add(new MeditationCardData(R.drawable.ic_lg_break,"Long-breaks" ,  "Meditations when you are on long breaks"));

        RecyclerView recycle = (RecyclerView) findViewById(R.id.recycle);
        MeditationCardAdapter gridAdapter = new MeditationCardAdapter(this, mtest);
        GridLayoutManager manager = new GridLayoutManager(this,2);
        recycle.setLayoutManager(manager);
        recycle.setAdapter(gridAdapter);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return gridAdapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
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

//    private void setMeditationCategoryIntent(CardView view) {
//        view.setOnClickListener(v -> {
//                    startActivity(new Intent(MeditationHome.this, MeditationListActivity.class));
//                    finish();
//                }
//        );
//    }
}

