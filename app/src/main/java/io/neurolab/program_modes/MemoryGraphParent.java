package io.neurolab.program_modes;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.neurolab.R;
import io.neurolab.fragments.MemoryGraphFragment;
import io.neurolab.fragments.SpectrumFragment;
import io.neurolab.fragments.StatisticsFragment;

public class MemoryGraphParent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_graph_parent);
        setTitle(R.string.mem_graph);
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
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, MemoryGraphFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StatisticsFragment.parsedData = null;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
