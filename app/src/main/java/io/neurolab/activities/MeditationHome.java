package io.neurolab.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.neurolab.R;

public class MeditationHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_home);
        setTitle(R.string.meditation);
    }
}
