package io.neurolab.model;

public interface FFTData {
    public void updateFFTData(double[][] fftData);

    public void updateFFTData();

    public void setTrainingFactor(double trainingFactor);

    public double getTrainingFactor();
}
