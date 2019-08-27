package io.neurolab.main.task;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;

import java.net.DatagramSocket;

import io.neurolab.main.NFBServer;
import io.neurolab.main.network.OSCForwarder;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.model.OSCForwardMask;
import io.neurolab.interfaces.Task;

public class OscForwardTask implements Task {

    private float defaultMin = -2f;
    private float defaultMax = 3f;

    private DatagramSocket socket = null;
    private String address;
    private String port;
    private String oscAddress;
    private NFBServer nfbServer;
    private String[] outputs;

    private float[] minValues;
    private float[] maxValues;
    private float[] rangeValues;

    private DefaultFFTData fftData;

    private int mode = 0;
    private int forwardMode = 0;

    private OSCForwarder oscForwarder;
    private OSCForwardMask oscForwardMask;
    private boolean connected = false;
    private Config config;

    public synchronized void setMinVal(int index, float value) {
        minValues[index] = value;
        rangeValues[index] = maxValues[index] - minValues[index];
    }

    public synchronized void setMaxVal(int index, float value) {
        maxValues[index] = value;
        rangeValues[index] = maxValues[index] - minValues[index];
    }

    public synchronized void setOutputString(int index, String string) {
        outputs[index] = string;
    }

    public OscForwardTask(NFBServer nfbServer) {
        this.nfbServer = nfbServer;
        this.config = nfbServer.getConfig();
        this.oscForwarder = new OSCForwarder();
        this.oscForwardMask = new OSCForwardMask(this, nfbServer.getFftData(), nfbServer.getConfig());
    }

    public boolean connect(String address, String port) {
        this.address = address;
        this.port = port;
        return connected = oscForwarder.connect(address, port);
    }

    @Override
    public void init() {
        System.err.println("init osc forward");
    }

    public boolean disconnect() {
        return oscForwarder.disconnect();
    }

    @Override
    public void run() {
        if (oscForwardMask != null) {
            this.fftData = nfbServer.getFftData();
            this.oscForwardMask.init(nfbServer.getFftData());
            int size = fftData.getNumChannels() * fftData.getBins();
            System.out.println("size:" + size);
            minValues = new float[size];
            maxValues = new float[size];
            rangeValues = new float[size];
            outputs = new String[size];

            for (int b = 0; b < fftData.getBins(); b++) {
                for (int c = 0; c < fftData.getNumChannels(); c++) {
                    int cb = c * fftData.getBins() + b;
                    float minVal = Float.valueOf(config.getPref(Config.osc_settings, String.valueOf(Config.osc_settings_params.address) + cb + "min"));
                    float maxVal = Float.valueOf(config.getPref(Config.osc_settings, String.valueOf(Config.osc_settings_params.address) + cb + "max"));

                    minValues[cb] = minVal;
                    maxValues[cb] = maxVal;
                    rangeValues[cb] = maxVal - minVal;
                }
            }
        }

        if (connected) {
            OSCBundle bundle = new OSCBundle();
            try {
                for (int b = 0; b < fftData.getBins(); b++) {
                    for (int c = 0; c < fftData.getNumChannels(); c++) {
                        int cb = c * fftData.getBins() + b;
                        OSCMessage msg = new OSCMessage(outputs[cb]);
                        Object argument;
                        float val = ((float) fftData.getRelativeFFTBins()[b][c] - minValues[cb]) / rangeValues[cb];
                        argument = val;

                        msg.addArgument(argument);
                        bundle.addPacket(msg);
                    }
                }
                oscForwarder.forwardBundle(bundle);
            } catch (java.lang.IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public NFBServer getNfbServer() {
        return nfbServer;
    }

    @Override
    public void stop() {
        this.oscForwarder.disconnect();
    }
}
