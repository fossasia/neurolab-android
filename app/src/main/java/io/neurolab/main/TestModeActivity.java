package io.neurolab.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import io.neurolab.R;

public class TestModeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DeviceConnector deviceConnector;
    private Button readBtn;
    private Button writeBtn;
    private EditText editText;
    private TextView displayView;
    private Spinner baudRateSpinner;

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    private int baudRate = 9600;

    private final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";

    private UsbSerialInterface.UsbReadCallback callBack = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                setReadText(displayView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    deviceConnector.checkConnection(context);
                    break;
                case ACTION_USB_PERMISSION:
                    boolean granted =
                            intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                writeBtn.setEnabled(true);
                                serialPort.setBaudRate(baudRate);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(callBack);
                                setReadText(displayView, getResources()
                                        .getString(R.string.connection_opened) + "\n");
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
                    deviceConnector.checkConnection(context);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);
        // setting up the UsbManager instance with the desired USB service.
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        // initializing the DeviceConnector instance for checking and connecting to the device.
        deviceConnector = new DeviceConnector(usbManager);
        // to start reading from the Arduino.
        readBtn = findViewById(R.id.buttonRead);
        // to send and write data to the Arduino.
        writeBtn = findViewById(R.id.buttonWrite);
        // to input the data to be sent.
        editText = findViewById(R.id.editText);
        // to display the data sent and received through the serial port.
        displayView = findViewById(R.id.displayData);
        // to choose a baud rate.
        baudRateSpinner = findViewById(R.id.baud_rates_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.baud_rates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // setting the adapter with the spinner.
        baudRateSpinner.setAdapter(adapter);
        baudRateSpinner.setOnItemSelectedListener(this);
        writeBtn.setEnabled(false);
        IntentFilter intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * To start the connection and reading from the Arduino.
     *
     * @param view
     */
    public void beginRead(View view) {
        deviceConnector.checkConnection(this);
        device = deviceConnector.getDevice();
    }

    /**
     * To send and write data to the Arduino once connection is set up.
     *
     * @param view
     */
    public void beginWrite(View view) {
        String string = editText.getText().toString();
        serialPort.write(string.getBytes());
        setReadText(displayView, "\n" + getResources().getString(R.string.data_sent) +
                string + "\n");
    }

    /**
     * To set the display text with the data being received from the Arduino through the serial
     * port.
     *
     * @param tv
     * @param text
     */
    private void setReadText(TextView tv, String text) {
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
