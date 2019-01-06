package io.neurolab;

public class NeuroSettings {

    public int samplesPerSecond = 3;
    public int numChannels = 2;
    public int bins = 4;

    public NeuroSettings() {
    }

    public NeuroSettings(int samplesPerSecond, int numChannels, int bins) {
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

}