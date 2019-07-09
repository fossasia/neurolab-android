package io.neurolab.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;

import io.neurolab.R;
import io.neurolab.activities.AboutUsActivity;
import io.neurolab.activities.MeditationActivity;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.activities.OnBoardingActivity;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.activities.SettingsActivity;
import io.neurolab.activities.TestModeActivity;
import io.neurolab.communication.USBCommunicationHandler;
import io.neurolab.communication.bluetooth.BluetoothTestActivity;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.fragments.RelaxVisualFragment;

public class NeuroLab extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";
    public static UsbSerialDevice serialPort;
    public static IntentFilter intentFilter;
    private static UsbManager usbManager;
    private static int baudRate = 9600;
    private static boolean deviceConnected;
    private static String deviceData;
    private Menu menu;
    private int launcherSleepTime;
    public static USBCommunicationHandler usbCommunicationHandler;
    private CardView focusButton;
    private CardView relaxButton;
    private CardView memGraphButton;
    private UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                deviceData = new String(arg0, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USB_PERMISSION:
                    boolean granted =
                            intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        if (usbCommunicationHandler.initializeSerialConnection(baudRate)) {
                            serialPort = usbCommunicationHandler.getSerialPort();
                            Toast.makeText(context, getResources().getString(R.string.connection_opened), Toast.LENGTH_SHORT).show();
                            deviceConnected = true;
                            serialPort.read(readCallback);
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static UsbManager getUsbManager() {
        return usbManager;
    }

    public static String getDeviceData() {
        return deviceData;
    }

    public static UsbSerialDevice getSerialPort() {
        return serialPort;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemClock.sleep(launcherSleepTime);
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // Check if we need to display our OnBoardingActivity
        if (!sharedPreferences.getBoolean(
                OnBoardingActivity.getOnBoardingPrefKey(), false)) {
            // The user hasn't seen the OnBoardingActivity yet, so show it
            startActivity(new Intent(this, OnBoardingActivity.class));
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        usbCommunicationHandler = USBCommunicationHandler.getInstance(this, usbManager);
        intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, intentFilter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setting Listeners of the settings checkboxes

        focusButton = findViewById(R.id.focus_card);
        relaxButton = findViewById(R.id.relax_card);
        memGraphButton = findViewById(R.id.mem_graph_card);

        focusButton.setOnClickListener(this);
        relaxButton.setOnClickListener(this);
        memGraphButton.setOnClickListener(this);
    }

    private void startProgramModeActivity(String mode) {
        //Store Settings
        Intent intent = new Intent(NeuroLab.this, ProgramModeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, mode);
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
        changeDeviceIcon();
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
            startActivity(new Intent(NeuroLab.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about_us) {
            startActivity(new Intent(NeuroLab.this, AboutUsActivity.class));
            return true;
        } else if (id == R.id.device_icon) {
            changeDeviceIcon();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeDeviceIcon() {
        if (deviceConnected) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_device_connected));
            menu.getItem(0).setTitle(getResources().getString(R.string.device_connected));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_focus) {
            startProgramModeActivity(FocusVisualFragment.FOCUS_FLAG);
        } else if (id == R.id.nav_relax) {
            startProgramModeActivity(RelaxVisualFragment.RELAX_PROGRAM_FLAG);
        } else if (id == R.id.nav_memory_graph) {
            startProgramModeActivity(MemoryGraphParent.MEMORY_GRAPH_FLAG);
        } else if (id == R.id.nav_meditation) {
            startActivity(new Intent(this, MeditationActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_test) {
            startActivity(new Intent(this, TestModeActivity.class));
        } else if (id == R.id.bluetooth_test) {
            startActivity(new Intent(this, BluetoothTestActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.focus_card:
                startProgramModeActivity(FocusVisualFragment.FOCUS_FLAG);
                break;
            case R.id.relax_card:
                startProgramModeActivity(RelaxVisualFragment.RELAX_PROGRAM_FLAG);
                break;
            case R.id.mem_graph_card:
                startProgramModeActivity(MemoryGraphParent.MEMORY_GRAPH_FLAG);
                break;
        }
    }

}
