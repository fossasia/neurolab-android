package io.neurolab.model;

import java.util.Iterator;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import io.neurolab.interfaces.Task;
import io.neurolab.settings.NeuroSettings;
import io.neurolab.tools.WindowFunction;

public class FFTPreprocessor implements Task {
    private NeuroSettings bciSettings;
    private DoubleFFT_1D fft;
    protected DefaultFFTData fftData;
    private double[] filterWindow;

    private int numFIRFilterSamples = 10;
    private double avgValCount = 5;

    public FFTPreprocessor(DefaultFFTData fftData, NeuroSettings bciSettings) {
        this.bciSettings = bciSettings;
        this.fftData = fftData;
        init();
    }

    public void setNumFIRFilterSamples(int numFIRFilterSamples) {
        this.numFIRFilterSamples = numFIRFilterSamples;
    }

    public int getNumFIRFilterSamples() {
        return numFIRFilterSamples;
    }

    public void init() {
        this.fft = new DoubleFFT_1D(bciSettings.getSamplesPerSecond());
        this.fftData.init(bciSettings.getSamplesPerSecond(), bciSettings.getBins(), bciSettings.getNumChannels());
        System.out.println("window size: " + this.fftData.getWindowSize() + " / samples per second: " + bciSettings.getSamplesPerSecond());
        this.filterWindow = WindowFunction.generate(this.fftData.getWindowSize(), WindowFunction.FunctionType.BLACKMAN);
    }

    public DefaultFFTData getFFTData() {
        return fftData;
    }

    private void nullifyFft() {
        fft = null;
    }

    @Override
    public void run() {

        Iterator<double[]> currentDataIterator;

        int s = 0;
        double diff = 0;
        int fftDataLocal = fftData.getNumChannels();
        fftDataLocal = bciSettings.getNumChannels();

        boolean[] notBrainWaves;
        notBrainWaves = new boolean[bciSettings.getNumChannels()];
        currentDataIterator = bciSettings.getCurrentData().iterator();
        while (currentDataIterator.hasNext()) {
            double[] currentSamples = currentDataIterator.next();
            for (int c = 0; c < fftData.getNumChannels(); c++) {

                fftData.getWindows()[c][s] = currentSamples[c];
                if (numFIRFilterSamples < 0) {
                    if ((s > 10) && !fftData.getNotBrainwaves()[c]) {
                        diff = Math.abs((fftData.getWindows()[c][s - 9] + fftData.getWindows()[c][s - 8] + fftData.getWindows()[c][s - 7] + fftData.getWindows()[c][s - 6] + fftData.getWindows()[c][s - 5]) / 5
                                - (fftData.getWindows()[c][s - 4] + fftData.getWindows()[c][s - 3] + fftData.getWindows()[c][s - 2] + fftData.getWindows()[c][s - 1] + fftData.getWindows()[c][s]) / 5);

                        if (diff > fftData.getPeakToPeakLimit()) {
                            fftData.getNotBrainwaves()[c] = true;
                            fftData.getPackagePenalty()[c] = 16;
                        }
                    }
                }
            }
            s++;
        }

        for (int c = 0; c < fftData.getNumChannels(); c++) {
            double[] convData = null;
            if (numFIRFilterSamples > 0) {
                double[] firFiltered = new double[fftData.getWindows()[c].length];
                for (int i = 0; i < fftData.getWindows()[c].length; i++) {
                    for (int n = 0; n < Math.min(i, numFIRFilterSamples); n++)
                        firFiltered[i] += fftData.getWindows()[c][i - n];
                    firFiltered[i] /= (double) Math.min(i + 1, numFIRFilterSamples);

                    if ((i > numFIRFilterSamples * 2) && !fftData.getNotBrainwaves()[c]) {
                        for (int n = 0; n < avgValCount; n++)
                            diff += firFiltered[i - numFIRFilterSamples * 2 + n] - firFiltered[i - n];
                        diff /= avgValCount * 2;

                        if (diff > fftData.getPeakToPeakLimit()) {
                            fftData.getNotBrainwaves()[c] = true;
                            fftData.getPackagePenalty()[c] = 16;
                        }
                    }

                }
                convData = WindowFunction.convolve(firFiltered, filterWindow);
            } else
                convData = WindowFunction.convolve(fftData.getWindows()[c], filterWindow);

            fft.realForward(convData);
            fftData.getCurrentFFTs()[c] = convData.clone();

            int h = fftData.getCurrentFFTs()[c].length / 2;

            fftData.getCurrentFFTValue()[c] = 0.1d;
            for (int v = 0; v < h - 2; v++) {
                // calculate magnitude
                fftData.getCurrentFFTs()[c][v] = Math.sqrt(fftData.getCurrentFFTs()[c][v * 2] * fftData.getCurrentFFTs()[c][v * 2] + fftData.getCurrentFFTs()[c][(v * 2) + 1] * fftData.getCurrentFFTs()[c][(v * 2) + 1]);
                fftData.getCurrentFFTPhases()[c][v] = Math.atan(fftData.getCurrentFFTs()[c][v + 1] / fftData.getCurrentFFTs()[c][v]);
                if ((v >= fftData.getValueMin()) && (v <= fftData.getValueMax()))
                    fftData.getCurrentFFTValue()[c] += fftData.getCurrentFFTs()[c][v];
            }
        }
        fftData.updateFFTData();
    }

    @Override
    public void stop() {
        nullifyFft();
    }

    public void enableFIRFilter(boolean enable) {
        numFIRFilterSamples = Math.abs(numFIRFilterSamples);
        if (!enable)
            numFIRFilterSamples *= -1;
    }

}
