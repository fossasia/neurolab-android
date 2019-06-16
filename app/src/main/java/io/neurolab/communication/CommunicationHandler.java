package io.neurolab.communication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;

public class CommunicationHandler {

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    private int baudRate = 9600;

    private static final int ARDUINO_DEVICE_ID = 0x2341;
    private final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";

    public CommunicationHandler(Context context, UsbManager usbManager) {
        this.usbManager = usbManager;
        searchForArduinoDevice(context);
    }

    private void searchForArduinoDevice(Context context) {
        HashMap usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Object object : usbDevices.entrySet()) {
                Map.Entry<String, UsbDevice> entry = (Map.Entry<String, UsbDevice>) object;
                device = entry.getValue();

                int deviceVID = device.getVendorId();
                if (deviceVID == ARDUINO_DEVICE_ID) { //Arduino Vendor ID = 0x2341
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

    public boolean initializeSerialConnection() {
        connection = usbManager.openDevice(device);
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
                serialPort.setBaudRate(baudRate);
                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            } else {
                Log.d("SERIAL", "PORT NOT OPEN");
                return false;
            }
        } else {
            Log.d("SERIAL", "PORT IS NULL");
            return false;
        }
        return true;
    }

    public UsbSerialDevice getSerialPort() {
        return serialPort;
    }
}
