package io.neurolab;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.concurrent.ConcurrentLinkedDeque;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BCISettings {

    protected ConcurrentLinkedDeque<double[]> currentData = new ConcurrentLinkedDeque<double[]>();
    public int samplesPerSecond = 3;
    public int numChannels = 2;
    public int bins = 4;

    //@TODO
    public BCISettings() {
    }

    public BCISettings(int samplesPerSecond, int numChannels, int bins ) {

        this.samplesPerSecond = samplesPerSecond;
        this.numChannels = numChannels;
        this.bins = bins;

    }

    public int getSamplesPerSecond() {
        return samplesPerSecond;
    }

    public int getBins() {
        return bins;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public ConcurrentLinkedDeque<double[]> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(ConcurrentLinkedDeque<double[]> currentData) {
        this.currentData = currentData;
    }



}