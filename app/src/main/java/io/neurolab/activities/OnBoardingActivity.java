package io.neurolab.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;

import io.neurolab.R;
import io.neurolab.main.SampleSlide;

public class OnBoardingActivity extends AppIntro2 {

    private static final String COMPLETED_ONBOARDING_PREF_NAME = "on_boarding_pref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // disabling the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // add On Boarding Fragments here
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro1));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro2));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro3));

        // color of the indicator dots (on/off)
        setIndicatorColor(ContextCompat.getColor(this, R.color.on_boarding_activity_indicator_on), ContextCompat.getColor(this, R.color.on_boarding_activity_indicator_off));

        // setting the transition animation
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        markVisited();
        finish();
    }

    // this method marks the visited state by populating the shared preferences
    private void markVisited(){
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING_PREF_NAME, true);
        sharedPreferencesEditor.apply();
    }

    public static String getOnBoardingPrefKey(){
        return COMPLETED_ONBOARDING_PREF_NAME;
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        markVisited();
        finish();
    }
}
