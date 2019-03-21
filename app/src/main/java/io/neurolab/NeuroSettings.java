package io.neurolab;


public class NeuroSettings {

    //Default place holder values
    private int samplesPerSecond = 3;
    private int bins = 4;
    private int numChannels = 2;

    // Constructors
    public NeuroSettings() {
    }

    public NeuroSettings(int samplesPerSecond, int numChannels, int bins) {
        this.samplesPerSecond = samplesPerSecond;
        this.numChannels = numChannels;
        this.bins = bins;
    }

    // Getters
    public int getSamplesPerSecond() {
        return samplesPerSecond;
    }

    public int getBins() {
        return bins;
    }

    public int getNumChannels() {
        return numChannels;
    }

    // Setters
    public void setSamplesPerSecond(int samplesPerSecond) {
        this.samplesPerSecond = samplesPerSecond;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }

}