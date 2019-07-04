package io.neurolab.gui;

import android.content.Context;

import org.w3c.dom.Document;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.neurolab.main.network.SerialForwarder;
import io.neurolab.main.task.SerialForwardTask;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;

public class SerialForwardMask {

    private static final long serialVersionUID = 1L;
    private static int serialWidth = 580;
    private static int serialHeight = 700;
    private DecimalFormat df = new DecimalFormat("#.##");
    private ThresholdRenderer[] thresholdRenderers;
    private SerialForwarder serialForwarder;
    private DefaultFFTData fftData;
    private SerialForwardTask fwdTask;
    private Config config;
    private Context context;
    private String addressText;
    private String baudText;
    private String[] currentValLabels;
    private float minValue;
    private float maxValue;
    private String label;

    public SerialForwardMask(Context context, SerialForwardTask fwdTask, DefaultFFTData fftData, Config config) {
        this.context = context;
        this.fwdTask = fwdTask;
        this.config = config;
        df.setRoundingMode(RoundingMode.CEILING);

        this.fftData = fftData;
        serialForwarder = new SerialForwarder();
    }

    public boolean connect() {
        return fwdTask.connect(addressText, baudText);
    }

    public void setVal(int index, float val, int msgVal) {
        currentValLabels[index] = (df.format(val) + " [" + msgVal + "]");
        thresholdRenderers[index].setCur(val);
    }

    public void init(DefaultFFTData fftData) {
        this.fftData = fftData;
        thresholdRenderers = new ThresholdRenderer[fftData.getBins() + 1];
        currentValLabels[fftData.getBins() + 1] = "";

        if (serialForwarder.isConnected()) {
            serialForwarder.disconnect();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        boolean connectionSuccessful = connect();

        for (int b = 0; b < fftData.getBins() + 1; b++) {
            int bi = b;
            String msgAsString = config.getPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.message) + bi);

            minValue = Float.valueOf(config.getPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.message) + bi + "min"));
            maxValue = Float.valueOf(config.getPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.message) + bi + "max"));
            currentValLabels[bi] = "0";

            if (b < fftData.getBins())
                label = fwdTask.getNfbServer().getCurrentFeedbackSettings().binLabels[bi];
            else
                label = "feedback";
        }
    }

    public void save() {
        if (config != null) {
            config.setPref(Config.serial_settings, String.valueOf(Config.serial_settings_params.address), addressText);
            config.store();
        }
    }

    public void changedUpdate(Document arg0) {
        save();
    }

    public void insertUpdate(Document arg0) {
        save();
    }

    public void removeUpdate(Document arg0) {
        save();
    }

    public String getAddressText() {
        return addressText;
    }

    public String getBaudText() {
        return baudText;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public String getLabel() {
        return label;
    }

}
