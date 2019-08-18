package io.neurolab.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.neurolab.R;
import io.neurolab.utilities.FilePathUtil;

import static io.neurolab.fragments.FocusVisualFragment.locationTracker;

public class DataReceiver extends BroadcastReceiver {
    private Context context;
    private USBCommunicationHandler usbCommunicationHandler;

    private static String dataContent = "";
    private boolean collecting = true;
    private static boolean extStop = false; // For external stop case by user
    private StringBuilder updatedDataContent;
    private int count;

    public DataReceiver(Context context, USBCommunicationHandler usbCommunicationHandler) {
        this.context = context;
        this.usbCommunicationHandler = usbCommunicationHandler;
    }

    private UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data;
            try {
                data = new String(arg0, "UTF-8");
                for (int i = 0; i < data.length(); i++) {
                    // for 'Unix Time Stamp' header
                    final char START_DETECT_CHAR = 'U';
                    if (data.charAt(i) == START_DETECT_CHAR) {
                        collecting = false;
                        data = data.substring(i);
                    }
                    // for 'Recorded' message
                    final char END_DETECT_CHAR = 'R';
                    if (data.charAt(i) == END_DETECT_CHAR || extStop) { // To detect the end of file recording
                        count = 0;
                        FilePathUtil.recordData(updateIncomingData(dataContent));
                        usbCommunicationHandler.getSerialPort().close();
                        extStop = false;
                        return;
                    }
                }
                if (!collecting)
                    dataContent += data;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private String updateIncomingData(String dataContent) {
        boolean startUpdating = false;
        updatedDataContent = new StringBuilder();
        for (int j = 0; j < dataContent.length(); j++) {
            final char START_UPDATE_CHAR = 'N';
            if ((!startUpdating) && dataContent.charAt(j) != START_UPDATE_CHAR) {
                updatedDataContent.append(dataContent.charAt(j));
                continue;
            }
            startUpdating = true;
            parseIncomingData(dataContent, j);

            if (count == 10) {
                count = 0;
            }
            updatedDataContent.append(dataContent.charAt(j));
        }
        return updatedDataContent.toString();
    }

    private void parseIncomingData(String dataContent, int j) {
        if (dataContent.charAt(j) == ',') {
            if (count == 0)
                updatedDataContent.append(SystemClock.currentThreadTimeMillis());
            if (count == 1) {
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime = sdf.format(currentTime);
                updatedDataContent.append(dateTime);
            }
            if (count == 3 && locationTracker.getDeviceLocation() != null)
                updatedDataContent.append(locationTracker.getDeviceLocation().getLatitude());
            if (count == 4 && locationTracker.getDeviceLocation() != null)
                updatedDataContent.append(locationTracker.getDeviceLocation().getLatitude());
            count++;
        }
    }

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
                        Toast.makeText(context, R.string.connection_opened, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
                break;
            default:
                break;
        }
    }

    public void stopConnection() {
        extStop = true;
    }
}
