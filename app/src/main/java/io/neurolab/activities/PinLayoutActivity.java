package io.neurolab.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import io.neurolab.R;

public class PinLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_layout);

        boolean front = getIntent().getBooleanExtra("layout", true);
        ImageView pinImageView = findViewById(R.id.pin_lay_img_view);
        if(front)
            pinImageView.setImageDrawable(getDrawable(R.drawable.front_pin_layout));
        else
            pinImageView.setImageDrawable(getDrawable(R.drawable.back_pin_layout));
    }
}
