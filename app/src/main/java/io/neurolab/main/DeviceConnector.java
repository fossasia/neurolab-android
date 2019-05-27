package io.neurolab.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DeviceConnector {

    Context context;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    private int baudRate = 9600;
    private int arduinoVid = 0x2341;

    private final String ACTION_USB_PERMISSION = "io.neurolab.main.USB_PERMISSION";

    UsbSerialInterface.UsbReadCallback callBack = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (intent.getAction()) {
                        case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                            checkConnection(context);
                            break;
                        case ACTION_USB_PERMISSION:
                            boolean granted =
                                    intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                            if (granted) {
                                connection = usbManager.openDevice(device);
                                serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                                if (serialPort != null) {
                                    if (serialPort.open()) { //Set Serial Connection Parameters.
                                        serialPort.setBaudRate(baudRate);
                                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                        serialPort.read(callBack);
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
                            checkConnection(context);
                            break;
                    }
                }
            };
        }
    };

    public void checkConnection(Context context) {
        this.context = context;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == arduinoVid) { //Arduino Vendor ID
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0,
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

    public UsbDevice getDevice() {
        return device;
    }

    public void setDevice(UsbDevice device) {
        this.device = device;
    }

    public UsbSerialDevice getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(UsbSerialDevice serialPort) {
        this.serialPort = serialPort;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getArduinoVid() {
        return arduinoVid;
    }
}
