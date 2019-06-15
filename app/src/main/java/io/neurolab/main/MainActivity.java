package io.neurolab.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import io.neurolab.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private int launcherSleepTime;

    private CardView focusButton;
    private CardView relaxButton;
    private CardView memGraphButton;
    private Menu menu;

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

        // Setting Listeners of the settings checkboxes

        focusButton = findViewById(R.id.btn_focus);
        relaxButton = findViewById(R.id.btn_relax);
        memGraphButton = findViewById(R.id.btn_mem_graph);

        focusButton.setOnClickListener(this);
        relaxButton.setOnClickListener(this);
        memGraphButton.setOnClickListener(this);
    }

    private void startProgramModeActivity(int toastMessageID, int mode) {
        //Store Settings
        Intent intent = new Intent(MainActivity.this, ProgramModeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, mode);
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
        this.menu = menu;
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about_us) {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
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
            startProgramModeActivity(R.string.mem_graph_toast, ProgramModeActivity.MEMORY_GRAPH_MODE);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_test) {
            startActivity(new Intent(this, TestModeActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_focus:
                startProgramModeActivity(R.string.focus_toast, ProgramModeActivity.FOCUS_PROGRAM_MODE);
                break;
            case R.id.btn_relax:
                startProgramModeActivity(R.string.relax_toast, ProgramModeActivity.RELAX_PROGRAM_MODE);
                break;
            case R.id.btn_mem_graph:
                startProgramModeActivity(R.string.mem_graph_toast, ProgramModeActivity.MEMORY_GRAPH_MODE);
                break;
        }
    }
}