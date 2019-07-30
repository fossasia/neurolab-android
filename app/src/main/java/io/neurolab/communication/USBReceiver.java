package io.neurolab.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;

import io.neurolab.utilities.FilePathUtil;

public class USBReceiver extends BroadcastReceiver {
    private USBCommunicationHandler usbCommunicationHandler;

    private static String dataContent = "";
    private boolean collecting = false;
    private static final char END_DETECT_CHAR = 'R';

    public USBReceiver(USBCommunicationHandler usbCommunicationHandler) {
        this.usbCommunicationHandler = usbCommunicationHandler;
    }

    private UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data;
            try {
                data = new String(arg0, "UTF-8");
                for (int i = 0; i < data.length(); i++) {
                    if (data.charAt(i) == END_DETECT_CHAR) { // To detect the end of file recording
                        FilePathUtil.recordData(dataContent);
                        collecting = true;
                    }
                }
                if (!collecting)
                    dataContent += data;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";

        switch (intent.getAction()) {
            case ACTION_USB_PERMISSION:
                boolean granted =
                        intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    int baudRate = 9600;
                    if (usbCommunicationHandler.initializeSerialConnection(baudRate)) {
                        usbCommunicationHandler.getSerialPort().read(readCallback);
                        Toast.makeText(context, "Serial Connection Opened!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
                break;
            default:
                break;
        }
    }
}
