package io.neurolab.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.io.UnsupportedEncodingException;

import io.neurolab.R;
import io.neurolab.activities.AboutUsActivity;
import io.neurolab.activities.DataLoggerActivity;
import io.neurolab.activities.DeviceInstructionsActivity;
import io.neurolab.activities.FocusParentActivity;
import io.neurolab.activities.MeditationHome;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.activities.OnBoardingActivity;
import io.neurolab.activities.PinLayoutActivity;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.activities.RelaxParentActivity;
import io.neurolab.activities.SettingsActivity;
import io.neurolab.activities.ShareDataActivity;
import io.neurolab.activities.TestModeActivity;
import io.neurolab.communication.USBCommunicationHandler;
import io.neurolab.communication.bluetooth.BluetoothTestActivity;
import io.neurolab.utilities.PermissionUtils;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class NeuroLab extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
public  int themetype=0;
    public static final String DEV_MODE_KEY = "developerMode";
    private static final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";
    private static final String[] READ_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int MY_REQUEST_CODE = 111;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    public static boolean developerMode = false;
    public static UsbSerialDevice serialPort;
    public static IntentFilter intentFilter;
    public static USBCommunicationHandler usbCommunicationHandler;
    private static UsbManager usbManager;
    private static int baudRate = 9600;
    private static boolean deviceConnected;
    private static String deviceData;
    private static AppUpdateManager appUpdateManager;
    private static Task<AppUpdateInfo> appUpdateInfoTask;
    private MenuItem navMeditate;
    private Menu menu;
    private CardView meditationCard;
    private int launcherSleepTime;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
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
        developerMode = sharedPreferences.getBoolean(DEV_MODE_KEY, false);
        if (developerMode)
            Toast.makeText(this, R.string.dev_mode_msg, Toast.LENGTH_SHORT).show();
        // Check if we need to display our OnBoardingActivity
        if (!sharedPreferences.getBoolean(
                OnBoardingActivity.getOnBoardingPrefKey(), false)) {
            // The user hasn't seen the OnBoardingActivity yet, so show it
            startActivity(new Intent(this, OnBoardingActivity.class));
        }
        if (!(PermissionUtils.checkRuntimePermissions(this, READ_WRITE_PERMISSIONS))) {
            getRuntimePermissions();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        usbCommunicationHandler = USBCommunicationHandler.getInstance(this, usbManager);
        intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menuNav = navigationView.getMenu();
        navMeditate = menuNav.findItem(R.id.nav_meditation);

        if (!developerMode)
            navMeditate.setVisible(false);

        // Setting Listeners of the settings checkboxes

        CardView focusButton = findViewById(R.id.focus_card);
        CardView relaxButton = findViewById(R.id.relax_card);
        CardView memGraphButton = findViewById(R.id.mem_graph_card);
        meditationCard = findViewById(R.id.meditation_card);

        focusButton.setOnClickListener(this);
        relaxButton.setOnClickListener(this);
        memGraphButton.setOnClickListener(this);
        meditationCard.setVisibility(View.GONE);

        if (developerMode) {
            meditationCard.setVisibility(View.VISIBLE);
            meditationCard.setOnClickListener(this);
        }

        checkForUpdates(appUpdateManager, appUpdateInfoTask);
    }

    private void checkForUpdates(AppUpdateManager appUpdateManager, Task<AppUpdateInfo> appUpdateInfoTask) {
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode != RESULT_OK) {
            checkForUpdates(appUpdateManager, appUpdateInfoTask);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, intentFilter);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        developerMode = sharedPreferences.getBoolean(DEV_MODE_KEY, false);
        if (developerMode) {
            meditationCard.setVisibility(View.VISIBLE);
            meditationCard.setOnClickListener(this);
            navMeditate.setVisible(true);
            invalidateOptionsMenu();
        }

        if (!developerMode) {
            meditationCard.setVisibility(View.GONE);
            navMeditate.setVisible(false);
            invalidateOptionsMenu();
        }

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), R.string.double_tap_back, Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
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
        if (id == R.id.device_icon) {
            changeDeviceIcon();
            startActivity(new Intent(this, DeviceInstructionsActivity.class));
            return true;
        } else if (id == R.id.test_mode) {
            startActivity(new Intent(this, TestModeActivity.class));
            return true;
        } else if (id == R.id.bluetooth_test) {
            startActivity(new Intent(this, BluetoothTestActivity.class));
            return true;
        } else if (id == R.id.pin_front_lay) {
            Intent intent = new Intent(this, PinLayoutActivity.class);
            intent.putExtra("layout", true);
            startActivity(intent);
        } else if (id == R.id.pin_back_lay) {
            Intent intent = new Intent(this, PinLayoutActivity.class);
            intent.putExtra("layout", false);
            startActivity(intent);
        } else if (id == R.id.theme_change) {
            //int nightModeFlags =
                    /*getApplicationContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                   setTheme(R.style.AppTheme_Dark);
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                   setTheme(R.style.AppTheme_Dark);
                    break;

                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    setTheme(R.style.AppTheme_Dark);
                    break;*/

            }

        //}
        if(themetype==0) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
            themetype=1;
        }
        else{
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
            themetype=0;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
        changeDeviceIcon();
        if (!developerMode) {
            MenuItem testMode = this.menu.findItem(R.id.test_mode);
            MenuItem bluetoothMode = this.menu.findItem(R.id.bluetooth_test);

            testMode.setVisible(false);
            bluetoothMode.setVisible(false);
        } else {
            MenuItem testMode = this.menu.findItem(R.id.test_mode);
            MenuItem bluetoothMode = this.menu.findItem(R.id.bluetooth_test);

            testMode.setVisible(true);
            bluetoothMode.setVisible(true);
        }
        return true;
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

        item.setCheckable(false);
        if (id == R.id.nav_focus) {
            startActivity(new Intent(this, FocusParentActivity.class));
        } else if (id == R.id.nav_relax) {
            startActivity(new Intent(this, RelaxParentActivity.class));
        } else if (id == R.id.nav_meditation) {
            startActivity(new Intent(this, MeditationHome.class));
        } else if (id == R.id.nav_memory_graph) {
            startProgramModeActivity(MemoryGraphParent.MEMORY_GRAPH_FLAG);
        } else if (id == R.id.nav_connect_device) {
            changeDeviceIcon();
            startActivity(new Intent(this, DeviceInstructionsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this, ShareDataActivity.class));
        } else if (id == R.id.nav_data_logger) {
            startActivity(new Intent(this, DataLoggerActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getRuntimePermissions() {
        PermissionUtils.requestRuntimePermissions(this,
                READ_WRITE_PERMISSIONS,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.focus_card:
                startActivity(new Intent(this, FocusParentActivity.class));
                break;
            case R.id.relax_card:
                startActivity(new Intent(this, RelaxParentActivity.class));
                break;
            case R.id.mem_graph_card:
                startProgramModeActivity(MemoryGraphParent.MEMORY_GRAPH_FLAG);
                break;
            case R.id.meditation_card:
                startActivity(new Intent(this, MeditationHome.class));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


}