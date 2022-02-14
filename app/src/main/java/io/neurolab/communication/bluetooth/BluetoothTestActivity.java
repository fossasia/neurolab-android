package io.neurolab.communication.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.neurolab.R;

public class BluetoothTestActivity extends AppCompatActivity {

    private static final String TAG = BluetoothTestActivity.class.getCanonicalName();

    private BluetoothConnectionManager bluetoothConnectionManager;

    private TextView isPairedWithTheHeadsetStatusTextView;
    private TextView bluetoothAdapterStateTextView;
    private TextView receivedDataDisplayTextView;
    private Button enableBluetoothBtn;
    private Button sendBtn;
    private EditText editText;

    private boolean stopBackgroundThread;

    // Broadcast Receiver to deal with the state changes of the adapter.
    private final BroadcastReceiver stateChangeBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "stateChangeBroadcastReceiver onReceive: STATE OFF");
                        disabledStateUI();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "stateChangeBroadcastReceiver onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "stateChangeBroadcastReceiver onReceive: STATE ON");
                        enabledStateUI();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "stateChangeBroadcastReceiver onReceive: STATE TURNING ON");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        bluetoothConnectionManager = BluetoothConnectionManager.getInstance();

        bluetoothAdapterStateTextView = findViewById(R.id.tv_bluetooth_adapter_state_viewer);
        enableBluetoothBtn = findViewById(R.id.enable_bluetooth_btn);
        isPairedWithTheHeadsetStatusTextView = findViewById(R.id.tv_is_paired_with_headset_status);
        receivedDataDisplayTextView = findViewById(R.id.received_data_display_tv);
        sendBtn = findViewById(R.id.send_btn);
        editText = findViewById(R.id.edit_text);

        enableBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAdapter();
            }
        });

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED); // Broadcast Action: The state of the local Bluetooth adapter has been changed.
        registerReceiver(stateChangeBroadcastReceiver, intentFilter);

        switch (bluetoothConnectionManager.checkBluetoothState()) {
            case BluetoothConnectionManager.NO_BLUETOOTH_SUPPORT:
                finish();
                break;
            case BluetoothConnectionManager.BLUETOOTH_DISABLED:
                disabledStateUI();
                break;
            case BluetoothConnectionManager.BLUETOOTH_ENABLED:
                enabledStateUI();
                break;
            default:
                break;
        }
    }

    private void disabledStateUI() {
        bluetoothAdapterStateTextView.setText("Bluetooth Disabled");
        enableBluetoothBtn.setVisibility(View.VISIBLE);
        isPairedWithTheHeadsetStatusTextView.setVisibility(View.GONE);
        sendBtn.setVisibility(View.GONE);
        receivedDataDisplayTextView.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        bluetoothConnectionManager.cleanUp();
    }

    private void enabledStateUI() {
        bluetoothAdapterStateTextView.setText("Bluetooth Enabled");
        enableBluetoothBtn.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        sendBtn.setVisibility(View.GONE);
        receivedDataDisplayTextView.setVisibility(View.GONE);

        isPairedWithTheHeadsetStatusTextView.setVisibility(View.VISIBLE);

        BluetoothDevice bluetoothDevice = bluetoothConnectionManager.returnPairedHeadset();

        if (bluetoothDevice == null) {
            isPairedWithTheHeadsetStatusTextView.setText("Please pair your phone with the headset.");
        } else {
            isPairedWithTheHeadsetStatusTextView.setText("Phone-Headset paired");

            BluetoothSocket bluetoothSocket = bluetoothConnectionManager.createBluetoothConnection(bluetoothDevice);
            if (bluetoothSocket == null) {
                Toast.makeText(this, "failed to establish the bluetooth connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "connection established", Toast.LENGTH_SHORT).show();
                editText.setVisibility(View.VISIBLE);
                sendBtn.setVisibility(View.VISIBLE);
                receivedDataDisplayTextView.setVisibility(View.VISIBLE);

                beginListenForData(bluetoothSocket);

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = editText.getText().toString();
                        bluetoothConnectionManager.sendData(string);
                        editText.setText("");
                    }
                });
            }
        }
    }

    private void beginListenForData(BluetoothSocket bluetoothSocket) {
        stopBackgroundThread = false;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopBackgroundThread) {
                    try {
                        int byteCount = bluetoothSocket.getInputStream().available();
                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            bluetoothSocket.getInputStream().read(rawBytes);
                            final String string = new String(rawBytes, "UTF-8");
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    receivedDataDisplayTextView.append(string);
                                }
                            });
                        }
                    } catch (IOException ex) {
                        stopBackgroundThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    private void enableAdapter() {
        Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // Show a system activity that allows the user to turn on Bluetooth.
        startActivity(btEnableIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateChangeBroadcastReceiver);
        bluetoothConnectionManager.cleanUp();
    }
}
