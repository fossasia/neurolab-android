package io.neurolab;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.neurolab.settings.FeedbackSettings;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int launcherSleepTime;

    private ConstraintLayout mainBackground;
    private CardView statusCardBackground;
    private CardView setupCardBackground;

    private CheckBox simulationCb;
    private CheckBox loadResourcesFromPhoneCb;
    private CheckBox audioFeedbackCb;
    private CheckBox bit24Cb;
    private CheckBox advancedModeCb;

    private TextView setupTextView;

    private Button focusButton;
    private Button relaxButton;
    private Button vjButton;
    private Button serialButton;

    private ImageView rocketimage;
    private int lastPos = 0;
    private int newPos = -300;
    private boolean moving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemClock.sleep(launcherSleepTime);
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        simulationCb = findViewById(R.id.cb_simulation);
        loadResourcesFromPhoneCb = findViewById(R.id.cb_load_resources_from_phone);
        audioFeedbackCb = findViewById(R.id.cb_audio_feedback);
        bit24Cb = findViewById(R.id.cb_24bit);
        advancedModeCb = findViewById(R.id.cb_advanced_mode);

        setupTextView = findViewById(R.id.tv_setup);

        focusButton = findViewById(R.id.btn_focus);
        relaxButton = findViewById(R.id.btn_relax);
        vjButton = findViewById(R.id.btn_vj);
        serialButton = findViewById(R.id.btn_serial);

        mainBackground = findViewById(R.id.background);
        statusCardBackground = findViewById(R.id.cardView);
        setupCardBackground = findViewById(R.id.setupCardView);

        // Setting Listeners of the various program buttons
        focusButton.setOnClickListener(v -> startProgramModeActivity(R.string.focus_toast, ProgramModeActivity.FOCUS_PROGRAM_MODE));

        relaxButton.setOnClickListener(v -> startProgramModeActivity(R.string.relax_toast, ProgramModeActivity.RELAX_PROGRAM_MODE));

        vjButton.setOnClickListener(v -> startProgramModeActivity(R.string.vj_toast, ProgramModeActivity.VJ_PROGRAM_MODE));

        serialButton.setOnClickListener(v -> startProgramModeActivity(R.string.serial_toast, ProgramModeActivity.SERIAL_PROGRAM_MODE));

        ConfigurationSettings configurationSettings = ConfigUtils.loadSettingsConfig(this);

        simulationCb.setChecked(configurationSettings.getServerSettings().isSimulation());
        loadResourcesFromPhoneCb.setChecked(configurationSettings.getServerSettings().isLoadFromPhone());
        audioFeedbackCb.setChecked(configurationSettings.getServerSettings().isAudioFeedback());
        bit24Cb.setChecked(configurationSettings.getServerSettings().isBit24());
        advancedModeCb.setChecked(configurationSettings.getServerSettings().isAdvancedMode());
    }

    private void startProgramModeActivity(int toastMessageID, int mode) {
        //Store Settings
        boolean settingSimulation = ((CheckBox) findViewById(R.id.cb_simulation)).isChecked();
        boolean settingLoadResourcesFromPhn = ((CheckBox) findViewById(R.id.cb_load_resources_from_phone)).isChecked();
        boolean settingAudioFeedback = ((CheckBox) findViewById(R.id.cb_audio_feedback)).isChecked();
        boolean setting24bit = ((CheckBox) findViewById(R.id.cb_24bit)).isChecked();
        boolean settingAdvanced = ((CheckBox) findViewById(R.id.cb_advanced_mode)).isChecked();

        Toast.makeText(MainActivity.this, toastMessageID, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ProgramModeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, mode);
        bundle.putBoolean(ProgramModeActivity.SETTING_SIMULATION, settingSimulation);
        bundle.putBoolean(ProgramModeActivity.SETTING_LOAD_RESOURCES_FROM_PHN, settingLoadResourcesFromPhn);
        bundle.putBoolean(ProgramModeActivity.SETTING_AUDIO_FEEDBACK, settingAudioFeedback);
        bundle.putBoolean(ProgramModeActivity.SETTING_24BIT, setting24bit);
        bundle.putBoolean(ProgramModeActivity.SETTING_ADVANCED, settingAdvanced);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_change_theme) {
            changeTheme();
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, NeuroSettingsActivity.class));
            return true;
        } else if (id == R.id.action_feedback_settings) {
            startActivity(new Intent(MainActivity.this, FeedbackSettings.class));
            return true;
        } else if (id == R.id.action_about_us) {
            startActivity(new Intent(MainActivity.this, About_Us.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_focus) {
            startProgramModeActivity(R.string.focus_toast, ProgramModeActivity.FOCUS_PROGRAM_MODE);
        } else if (id == R.id.nav_relax) {
            startProgramModeActivity(R.string.relax_toast, ProgramModeActivity.RELAX_PROGRAM_MODE);
        } else if (id == R.id.nav_vj) {
            startProgramModeActivity(R.string.vj_toast, ProgramModeActivity.VJ_PROGRAM_MODE);
        } else if (id == R.id.nav_serial) {
            startProgramModeActivity(R.string.serial_toast, ProgramModeActivity.SERIAL_PROGRAM_MODE);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeTheme() {
        int colorToBackground;
        int colorFromBackground;
        int colorFromStatusCardView;
        int colorToStatusCardView;
        int colorFromValues;
        int colorToValues;

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            colorFromBackground = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            colorToBackground = ContextCompat.getColor(this, R.color.color_white_background);
            colorFromStatusCardView = ContextCompat.getColor(this,R.color.colorCardViewNight);
            colorToStatusCardView = ContextCompat.getColor(this,R.color.colorCardViewDay);
            colorFromValues = ContextCompat.getColor(this,R.color.colorValuesNight);
            colorToValues = ContextCompat.getColor(this,R.color.colorValuesDay);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            colorFromBackground = ContextCompat.getColor(this, R.color.color_white_background);
            colorToBackground = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            colorFromStatusCardView = ContextCompat.getColor(this, R.color.colorCardViewDay);
            colorToStatusCardView = ContextCompat.getColor(this, R.color.colorCardViewNight);
            colorFromValues = ContextCompat.getColor(this,R.color.colorValuesDay);
            colorToValues = ContextCompat.getColor(this,R.color.colorValuesNight);
        }

        changeBackgroundColor(colorFromBackground,colorToBackground);
        changeStatusCardViewColor(colorFromStatusCardView,colorToStatusCardView);
        changeSetupCardViewColor(colorFromStatusCardView,colorToStatusCardView);
        changeTextColor(colorFromValues,colorToValues);

    }

    private void changeBackgroundColor(int colorFrom, int colorTo) {
        final ValueAnimator backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        backgroundAnimator.setDuration(200);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mainBackground.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        backgroundAnimator.start();
    }

    private void changeStatusCardViewColor(int colorFrom, int colorTo) {
        final ValueAnimator backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        backgroundAnimator.setDuration(200);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statusCardBackground.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        backgroundAnimator.start();
    }

    private void changeSetupCardViewColor(int colorFrom, int colorTo) {
        final ValueAnimator backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        backgroundAnimator.setDuration(200);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setupCardBackground.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        backgroundAnimator.start();
    }

    private void changeTextColor(int colorFrom, int colorTo) {
        final ValueAnimator backgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        backgroundAnimator.setDuration(200);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                simulationCb.setTextColor((Integer) animation.getAnimatedValue());
                loadResourcesFromPhoneCb.setTextColor((Integer) animation.getAnimatedValue());
                audioFeedbackCb.setTextColor((Integer) animation.getAnimatedValue());
                bit24Cb.setTextColor((Integer) animation.getAnimatedValue());
                advancedModeCb.setTextColor((Integer) animation.getAnimatedValue());
                setupTextView.setTextColor((Integer) animation.getAnimatedValue());
            }
        });
        backgroundAnimator.start();
    }
}