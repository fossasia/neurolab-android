package io.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import io.neurolab.settings.FeedbackSettings;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int launcherSleepTime;

    private Button focusButton;
    private Button relaxButton;
    private Button vjButton;
    private Button serialButton;

    private boolean SETTING_SIMULATION;
    private boolean SETTING_LOAD_RESOURCES_FROM_PHN;
    private boolean SETTING_AUDIO_FEEDBACK;
    private boolean SETTING_24BIT;
    private boolean SETTING_ADVANCED;

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

        focusButton = findViewById(R.id.btn_focus);
        relaxButton = findViewById(R.id.btn_relax);
        vjButton = findViewById(R.id.btn_vj);
        serialButton = findViewById(R.id.btn_serial);

        // Setting Listeners of the various program buttons
        focusButton.setOnClickListener(v -> startProgramModeActivity(R.string.focus_toast, ProgramModeActivity.FOCUS_PROGRAM_MODE));

        relaxButton.setOnClickListener(v -> startProgramModeActivity(R.string.relax_toast, ProgramModeActivity.RELAX_PROGRAM_MODE));

        vjButton.setOnClickListener(v -> startProgramModeActivity(R.string.vj_toast, ProgramModeActivity.VJ_PROGRAM_MODE));

        serialButton.setOnClickListener(v -> startProgramModeActivity(R.string.serial_toast, ProgramModeActivity.SERIAL_PROGRAM_MODE));
    }

    private void startProgramModeActivity(int toastMessageID, int mode) {
        //Store Settings
        SETTING_SIMULATION = ((CheckBox) findViewById(R.id.cb_simulation)).isChecked();
        SETTING_LOAD_RESOURCES_FROM_PHN = ((CheckBox) findViewById(R.id.cb_load_resources_from_phone)).isChecked();
        SETTING_AUDIO_FEEDBACK = ((CheckBox) findViewById(R.id.cb_audio_feedback)).isChecked();
        SETTING_24BIT = ((CheckBox) findViewById(R.id.cb_24bit)).isChecked();
        SETTING_ADVANCED = ((CheckBox) findViewById(R.id.cb_advanced_mode)).isChecked();

        Toast.makeText(MainActivity.this, toastMessageID, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ProgramModeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, mode);
        boolean[] settings = {SETTING_SIMULATION, SETTING_LOAD_RESOURCES_FROM_PHN, SETTING_AUDIO_FEEDBACK, SETTING_24BIT, SETTING_ADVANCED};
        bundle.putBooleanArray(ProgramModeActivity.INTENT_KEY_SETTINGS, settings);
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
        if (id == R.id.action_settings) {
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
            // Handle the focus program mode
        } else if (id == R.id.nav_relax) {

        } else if (id == R.id.nav_vj) {

        } else if (id == R.id.nav_serial) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
