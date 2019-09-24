package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.neurolab.R;
import io.neurolab.adapters.MeditationListAdapter;

public class MeditationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_list);
        setTitle("Meditations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView meditationsRecyclerView = findViewById(R.id.meditation_recycler_view);

        MeditationListAdapter meditationListAdapter = new MeditationListAdapter(this, R.raw.class.getFields());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        meditationsRecyclerView.setLayoutManager(linearLayoutManager);
        meditationsRecyclerView.setAdapter(meditationListAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

     public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MeditationHome.class));
        finish();
    }
}
