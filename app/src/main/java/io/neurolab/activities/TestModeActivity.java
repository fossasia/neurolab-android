package io.neurolab.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;

import io.neurolab.R;
import io.neurolab.communication.USBCommunicationHandler;
import io.neurolab.fragments.NeuroSettingsFragment;
import io.neurolab.main.DeviceConnector;
import io.neurolab.main.NeuroLab;

public class TestModeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DeviceConnector deviceConnector;

    private Button readBtn;
    private Button writeBtn;
    private Button stopBtn;

    private EditText editText;
    private TextView displayView;

    private USBCommunicationHandler usbCommunicationHandler;
    private int baudRate = 9600;

    private static final int NUM_CHANNELS_DEFAULT = 2;
    private static final int BIN_NUMBER_DEFAULT = 4;
    private static final int SAMPLES_PER_SECOND_DEFAULT = 1007;

    private final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";

    private UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                updateView(displayView, data);
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
                            setUiEnabled(true);
                            usbCommunicationHandler.getSerialPort().read(readCallback);
                            feedConfigSetToArduino();
                            updateView(displayView, "Serial Connection Opened!\\n");
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

    private void feedConfigSetToArduino() {
        // Grabbing the application shared preference and the values. Have added the default values for the edge case when user
        // lets say starts up the application for the first time and directly goes to the Test Mode Activity. Shared preferences would
        // not be initialized in that case.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String channels = sharedPreferences.getString(NeuroSettingsFragment.KEY_CHANNELS, Integer.toString(NUM_CHANNELS_DEFAULT));
        String samplesPerSecond = sharedPreferences.getString(NeuroSettingsFragment.KEY_SAMPLES, Integer.toString(SAMPLES_PER_SECOND_DEFAULT));
        String bins = sharedPreferences.getString(NeuroSettingsFragment.KEY_BINS, Integer.toString(BIN_NUMBER_DEFAULT));

        // Adding space as a delimeter for arduino to parse them individually.
        String configString = channels + " " + samplesPerSecond + " " + bins;

        // Writing the config string to arduino
        usbCommunicationHandler.getSerialPort().write(configString.getBytes());

        // Snackbar for validation
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout_coordinator_test_mode),
                "Feeding configuration to Arduino: " +
                        "Channels = " + channels + ", " + "Samples Per Second = " + samplesPerSecond +
                        ", " + "Bin Number = " + bins, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);

        // Setting listeners of all the three buttons
        Button beginBtn = findViewById(R.id.buttonBegin);
        readBtn = findViewById(R.id.buttonRead);
        writeBtn = findViewById(R.id.buttonWrite);
        stopBtn = findViewById(R.id.buttonStop);

        // to start reading from the Arduino.
        editText = findViewById(R.id.editText);
        // to display the data received through the serial port.
        displayView = findViewById(R.id.displayData);
        // to choose a baud rate.

        beginBtn.setOnClickListener(v -> usbCommunicationHandler.searchForArduinoDevice(this));
        writeBtn.setOnClickListener(v -> write(v));
        stopBtn.setOnClickListener(v -> closeConnection());

        Spinner baudRateSpinner = findViewById(R.id.baud_rates_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.baud_rates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        baudRateSpinner.setAdapter(adapter);
        baudRateSpinner.setOnItemSelectedListener(this);

        // setting up the UsbManager instance with the desired USB service.
        usbCommunicationHandler = USBCommunicationHandler.getInstance(this, NeuroLab.getUsbManager());
        // initializing the DeviceConnector instance for checking and connecting to the device.
        deviceConnector = new DeviceConnector(NeuroLab.getUsbManager());

        IntentFilter intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    // For closing the serial connection
    private void closeConnection() {
        usbCommunicationHandler.getSerialPort().close();
        setUiEnabled(false);
    }

    // Sets UI enabled or disabled as per the state passed in
    private void setUiEnabled(boolean state) {
        readBtn.setEnabled(state);
        writeBtn.setEnabled(state);
        stopBtn.setEnabled(state);
    }

    /**
     * To send and write data to the Arduino once connection is set up.
     *
     * @param view
     */
    public void write(View view) {
        String string = editText.getText().toString();
        usbCommunicationHandler.getSerialPort().write(string.getBytes());
        editText.setText("");
    }

    /**
     * To append the textview tv with the string text.
     * port.
     *
     * @param tv
     * @param text
     */
    private void updateView(TextView tv, String text) {
        final TextView displayView = tv;
        final String ftext = text;
        runOnUiThread(() -> displayView.append(ftext));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        baudRate = Integer.parseInt((String) parent.getItemAtPosition(position));
        deviceConnector.setBaudRate(baudRate);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        baudRate = 9600;
        deviceConnector.setBaudRate(baudRate);
    }
}