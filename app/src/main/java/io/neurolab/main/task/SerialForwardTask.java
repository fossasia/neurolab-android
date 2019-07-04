package io.neurolab.main.task;

import android.util.Log;

import java.net.DatagramSocket;

import io.neurolab.main.NFBServer;
import io.neurolab.main.network.SerialForwarder;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.interfaces.Task;

public class SerialForwardTask implements Task {

    long currentTimestamp = -1;
    long nextRenderTimestamp = -1;
    private float defaultMin = -2f;
    private float defaultMax = 3f;
    private int messageInterval = 75;
    private DatagramSocket socket = null;
    private String address;
    private String baudRate;
    private NFBServer nfbServer;
    private float[] minValues;
    private float[] maxValues;
    private float[] rangeValues;
    private DefaultFFTData fftData;
    private int mode = 0;     // 0 = per channel per bin, 1 = per channel per bin
    private int forwardMode = 0;
    private SerialForwarder serialForwarder;
    private boolean serialForwardMaskInterfaceVisible;
    private boolean connected = false;
    private Config config;
    private int[] messages;
    private float[] vals;

    private static final String TAG = SerialForwardTask.class.getCanonicalName();
    
    public SerialForwardTask(NFBServer nfbServer) {
        this.nfbServer = nfbServer;
        this.config = nfbServer.getConfig();
        this.serialForwarder = new SerialForwarder();
        // TODO: Instantiate a new SerialForwardMaskInterface object.
    }

    public void setSerialForwardMaskInterfaceVisible(boolean serialForwardMaskInterfaceVisible) {
        this.serialForwardMaskInterfaceVisible = serialForwardMaskInterfaceVisible;
    }

    public synchronized void setMinVal(int index, float value) {
        minValues[index] = value;
        rangeValues[index] = maxValues[index] - minValues[index];
    }

    public synchronized void setMaxVal(int index, float value) {
        maxValues[index] = value;
        rangeValues[index] = maxValues[index] - minValues[index];
    }

    public synchronized void setCurrentFeedbackValues() {
        // TODO: Needs to be implemented for setting the current feedback values.
    }

    public boolean connect(String address, String baudrate) {
        this.address = address;
        this.baudRate = baudrate;
        return connected = serialForwarder.connect(address, Integer.valueOf(baudrate));
    }

    @Override
    public void init() {
        Log.e(TAG, "init osc forward");
    }

    public boolean disconnect() {
        return serialForwarder.disconnect();
    }

    @Override
    public void run() {
        currentTimestamp = System.currentTimeMillis();
        if (!serialForwardMaskInterfaceVisible) {
            this.fftData = nfbServer.getFftData();
            // TODO: Initialize the Serial Forward Mask Interface here.
            int size = fftData.getNumChannels() * fftData.getBins();
            System.out.println("size:" + size);
            minValues = new float[size];
            maxValues = new float[size];
            rangeValues = new float[size];
            messages = new int[fftData.getBins() + 1];
            vals = new float[fftData.getBins() + 1];

            for (int b = 0; b < fftData.getBins(); b++) {
                    int cb = b;
                    float minVal = Float.valueOf(config.getPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.message) + cb + "min"));
                    float maxVal = Float.valueOf(config.getPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.message) + cb + "max"));
                    minValues[cb] = minVal;
                    maxValues[cb] = maxVal;
                    rangeValues[cb] = maxVal - minVal;
            }
        }

        if (connected) {
            if (currentTimestamp < nextRenderTimestamp)
                return;

            try {
                for (int b = 0; b < fftData.getBins(); b++) {
                    vals[b] = (float) (fftData.getRewardFFTBins()[b][0] + fftData.getRewardFFTBins()[b][1]) / 2f;
                    messages[b] = (int) (((vals[b] - minValues[b]) / rangeValues[b]) * 255f);
                }
                vals[fftData.getBins()] = nfbServer.getCurrentFeedbackSettings().getCurrentFeedback();
                messages[fftData.getBins()] = 127 + (int) (vals[fftData.getBins()] * 40f);
                serialForwarder.forwardMessage(messages);

                for (int b = 0; b < fftData.getBins() + 1; b++) {
                    // TODO: Update the interface passing in index as b, val as vals[b] and msgVal as messages[b]
                }
            } catch (java.lang.IllegalArgumentException e) {
                e.printStackTrace();
            }

            nextRenderTimestamp = currentTimestamp + messageInterval;
        }
    }

    public NFBServer getNfbServer() {
        return nfbServer;
    }

    @Override
    public void stop() {
        this.serialForwarder.disconnect();
    }

}
