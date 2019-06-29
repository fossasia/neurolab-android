package io.neurolab.program_modes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import io.neurolab.R;
import io.neurolab.fragments.MemoryGraphFragment;
import io.neurolab.fragments.SpectrumFragment;
import io.neurolab.fragments.StatisticsFragment;
import io.neurolab.main.NeuroLab;

import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class MemoryGraphParent extends AppCompatActivity {

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_graph_parent);
        setTitle(R.string.mem_graph);
        if (getIntent().getExtras() != null) {
            filePath = getIntent().getStringExtra(LOG_FILE_KEY);
        }
        Bundle bundle = new Bundle();
        bundle.putString(LOG_FILE_KEY, filePath);
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.bottom_nav_graph:
                    selectedFragment = MemoryGraphFragment.newInstance();
                    break;
                case R.id.bottom_nav_stats:
                    selectedFragment = StatisticsFragment.newInstance();
                    break;
                case R.id.bottom_nav_spectrum:
                    selectedFragment = SpectrumFragment.newInstance();
                    break;
                default:
                    break;
            }
            selectedFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment defaultProgram = MemoryGraphFragment.newInstance();
        defaultProgram.setArguments(bundle);
        transaction.replace(R.id.frame_layout, defaultProgram);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsFragment.parsedData = null;
        startActivity(new Intent(this, NeuroLab.class));
        finish();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
