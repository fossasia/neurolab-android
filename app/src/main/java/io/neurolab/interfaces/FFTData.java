package io.neurolab.interfaces;

public interface FFTData {
    void updateFFTData(double[][] fftData);

    void updateFFTData();

    void setTrainingFactor(double trainingFactor);

    double getTrainingFactor();
}
