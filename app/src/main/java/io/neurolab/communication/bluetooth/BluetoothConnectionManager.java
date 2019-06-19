package io.neurolab.communication.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnectionManager {

    // The device can have three possible states (mutually exclusive and exhaustive) at any time related to the local bluetooth adapter.
    public static final int NO_BLUETOOTH_SUPPORT = 1;  // no bluetooth support
    public static final int BLUETOOTH_ENABLED = 2;     // bluetooth is supported and enabled
    public static final int BLUETOOTH_DISABLED = 3;    // bluetooth is supported but disabled
    private static final String DEVICE_NAME = "HC-05";      // TODO: Will be changed later for the headset
    private static final UUID port_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");       // TODO: Will be changed for the headset
    // Class Variables
    private static BluetoothConnectionManager bluetoothConnectionManager;
    // Instance Fields
    private BluetoothAdapter bluetoothAdapter;    // The local device bluetooth adapter

    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private BluetoothConnectionManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothConnectionManager getInstance() {
        if (bluetoothConnectionManager == null) {
            bluetoothConnectionManager = new BluetoothConnectionManager();
        }
        return bluetoothConnectionManager;
    }

    /*
    This method checks the current bluetooth state and returns any one of the three states
    NO_BLUETOOTH_SUPPORT, BLUETOOTH_ENABLED, BLUETOOTH_DISABLED
     */
    public int checkBluetoothState() {
        if (bluetoothAdapter == null)
            return NO_BLUETOOTH_SUPPORT;
        else if (!bluetoothAdapter.isEnabled())
            return BLUETOOTH_DISABLED;
        else
            return BLUETOOTH_ENABLED;
    }

    /*
    This method checks if the bluetooth adapter is paired with the headset device and updates the
    bluetoothDevice instance field.
     */
    public BluetoothDevice returnPairedHeadset() {
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices)
            if (device.getName().equals(DEVICE_NAME))
                return device;
        return null;
    }

    /*
    This method creates a bluetooth connection to the headset device and returns the corresponding socket.
    Null is returned for all other cases (non-working).
    */
    public BluetoothSocket createBluetoothConnection(BluetoothDevice headsetBTDevice) {
        if (headsetBTDevice != null) {    // Validating once to prevent exceptions
            bluetoothSocket = null;
            try {
                bluetoothSocket = headsetBTDevice.createRfcommSocketToServiceRecord(port_uuid);
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return bluetoothSocket;     // returning the bluetooth socket only after ensuring the connection has established
        }
        return null;
    }

    /*
    This method writes the string data using the bluetoothsocket's outputstream.
    Returns true only if it is able to successfully write the data; returns false for all the other cases.
    */
    public boolean sendData(String string) {
        if (bluetoothSocket != null) {    // Validating once to prevent exceptions
            OutputStream outputStream = null;
            try {
                outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void cleanUp() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
