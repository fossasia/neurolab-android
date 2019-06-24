package io.neurolab.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.neurolab.R;
import io.neurolab.fragments.NeuroSettingsFragment;

public class TestModeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DeviceConnector deviceConnector;

    private Button beginBtn;
    private Button readBtn;
    private Button writeBtn;
    private Button stopBtn;

    private EditText editText;
    private TextView displayView;
    private Spinner baudRateSpinner;

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    private int baudRate = 9600;
    private static final int ARDUINO_DEVICE_ID = 0x2341;

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
            switch (intent.getAction()){
                case ACTION_USB_PERMISSION:
                    boolean granted =
                            intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { // Set Serial Connection Parameters.
                                setUiEnabled(true); // Enable Buttons in UI
                                serialPort.setBaudRate(baudRate);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(readCallback);
                                feedConfigSetToArduino(); // Once the connection is all set up and socket connected
                                updateView(displayView, "Serial Connection Opened!\n");

                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                    break;
                default:
                    break;
            }
        };
    };

    private void feedConfigSetToArduino(){
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
        serialPort.write(configString.getBytes());

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
        beginBtn = findViewById(R.id.buttonBegin);
        readBtn = findViewById(R.id.buttonRead);
        writeBtn = findViewById(R.id.buttonWrite);
        stopBtn = findViewById(R.id.buttonStop);

        // to start reading from the Arduino.
        editText = findViewById(R.id.editText);
        // to display the data received through the serial port.
        displayView = findViewById(R.id.displayData);
        // to choose a baud rate.

        beginBtn.setOnClickListener(v -> searchForArduinoDevice());
        writeBtn.setOnClickListener(v -> write(v));
        stopBtn.setOnClickListener(v -> closeConnection());

        baudRateSpinner = findViewById(R.id.baud_rates_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.baud_rates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        baudRateSpinner.setAdapter(adapter);
        baudRateSpinner.setOnItemSelectedListener(this);

        // setting up the UsbManager instance with the desired USB service.
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        // initializing the DeviceConnector instance for checking and connecting to the device.
        deviceConnector = new DeviceConnector(usbManager);

        IntentFilter intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /*
        Searches for all connected devices and then check if the vendor ID of the Arduino
        matches that of a connected device.
        If found, permission must be requested from the user.
     */
    private void searchForArduinoDevice(){
        HashMap usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Object object : usbDevices.entrySet()) {
                Map.Entry<String, UsbDevice> entry = (Map.Entry<String, UsbDevice>) object;
                device = entry.getValue();

                int deviceVID = device.getVendorId();
                if (deviceVID == ARDUINO_DEVICE_ID){                   //Arduino Vendor ID = 0x2341
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                            new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    // For closing the serial connection
    private void closeConnection(){
        serialPort.close();
        setUiEnabled(false);
    }

    // Sets UI enabled or disabled as per the state passed in
    private void setUiEnabled(boolean state){
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
        serialPort.write(string.getBytes());
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
