package io.neurolab.utilities;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FrequencyProcessor {

    private int inputLength;
    private int fftLength;
    private int nbFFTPoints;
    private boolean even;
    private boolean zeroPad = false;
    private double[] real;
    private double[] imag;
    private double[] logpower;
    private double[] Y;
    private double[] f;
    private double[] hammingWin;
    private DoubleFFT_1D fft_1D;

    public FrequencyProcessor(int inputLength, int fftLength, double samplingFrequency) {

        this.inputLength = inputLength;
        this.fftLength = fftLength;

        // Find out if zero-padding or truncating is necessary
        if (this.fftLength > this.inputLength) { // zero-padding
            zeroPad = true;
        }

        // Compute the number of points in the FrequencyProcessor
        if (this.fftLength % 2 == 0) {
            nbFFTPoints = this.fftLength /2;
            even = true;
        } else {
            nbFFTPoints = (int)(this.fftLength /2) + 1;
            even = false;
        }

        // Initialize arrays to hold internal values
        Y = new double[this.fftLength];
        real = new double[nbFFTPoints];
        imag = new double[nbFFTPoints];
        logpower = new double[nbFFTPoints];


        // Initialize FrequencyProcessor transform
        fft_1D = new DoubleFFT_1D(this.fftLength);

        // Define frequency bins
        f = new double[nbFFTPoints];
        for (int i = 0; i < nbFFTPoints; i++) {
            f[i] = samplingFrequency * i / this.fftLength;
        }

        // Initialize Hamming window
        hammingWin = hamming(this.inputLength);
    }

    public double[] processFFTData(double[] x) {
        if (x.length != inputLength) {
            throw new IllegalArgumentException("Input has " + x.length + " elements instead of " + inputLength + ".");
        }

        if (zeroPad) {
            Y = new double[fftLength]; // Re-initialize to have zeros at the end
        }

        // Compute mean of the window
        double winMean = 0;
        for (int i = 0; i < inputLength; i++) {
            winMean += x[i];
        }
        winMean /= inputLength;

        // De-mean and apply Hamming window
        for (int i = 0; i < Math.min(inputLength, fftLength); i++) {
            Y[i] = hammingWin[i]*(x[i] - winMean);
        }

        // Compute DFT
        fft_1D.realForward(Y);

        // Get real and imaginary parts
        for (int i = 0; i < nbFFTPoints -1; i++) {
            real[i] = Y[2*i];
            imag[i] = Y[2*i + 1];
        }
        imag[0] = 0;

        // Get first and/or last points depending on length of FrequencyProcessor (Specific to JTransforms library)
        if (even) {
            real[nbFFTPoints -1] = Y[1];
        } else {
            imag[nbFFTPoints -1] = Y[1];
            real[nbFFTPoints -1] = Y[fftLength -1];
        }

        for (int i = 0; i < nbFFTPoints; i++) {
            logpower[i] = Math.log10(real[i]*real[i] + imag[i]*imag[i]); // log squared
            // complex magnitude
        }
        return logpower;
    }

    // Compute Hamming window coefficients.
    private double[] hamming(int L) {
        double[] w = new double[L];
        for (int n = 0; n < L; n++) {
            w[n] = 0.54 - 0.46*Math.cos(2*Math.PI*n/(L-1));
        }
        return w;
    }

    public double[] getFreqBins() {
        return f;
    }
}
