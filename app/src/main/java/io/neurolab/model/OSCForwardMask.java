package io.neurolab.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.neurolab.main.network.OSCForwarder;
import io.neurolab.main.task.OscForwardTask;

public class OSCForwardMask {
    private OSCForwarder oscForwarder;
    private DefaultFFTData fftData;
    DecimalFormat df = new DecimalFormat("#.##");

    private OscForwardTask fwdTask;
    private Config config;

    public OSCForwardMask(OscForwardTask fwdTask, DefaultFFTData fftData, Config config) {
        this.fwdTask = fwdTask;
        this.config = config;
        df.setRoundingMode(RoundingMode.CEILING);

        this.fftData = fftData;
        oscForwarder = new OSCForwarder();

    }

    public void init(DefaultFFTData fftData) {
        this.fftData = fftData;
    }

    public void save(String ipText) {
        if (config != null) {
            config.setPref(Config.osc_settings, String.valueOf(Config.osc_settings_params.ip), ipText);
            config.store();
        }
    }
}
