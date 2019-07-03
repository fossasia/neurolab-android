package io.neurolab.model;

import io.neurolab.interfaces.FFTData;
import io.neurolab.settings.NeuroSettings;
import io.neurolab.utilities.MathBasics;

public class DefaultFFTData implements FFTData {
    private boolean peakToPeakCheck = true;
    private boolean[] notBrainwaves;
    private int[] packagePenalty;

    private double[][] currentFFTs;
    private double[][] meanFFTs;
    private double[][] currentFFTPhases;
    private double[][] windows;

    private double[][] currentFFTBins;

    // long term
    private int[][] meanFFTValueCount;
    private double[][] meanFFTBins;
    private double[][] varFFTBins;
    private double[][] relativeFFTBins;

    // fixed fft values by operator
    private double[][] baselineFFTValues;
    private double[][] shortMeanFFTBins;
    private double[][] shortVarFFTBins;
    private double[][] rewardFFTBins;
    private double[] currentFFTValue;

    private int[] binRanges = {4, 7, 8, 10, 11, 13, 17, 30};
    private int[] binRangesAmount = {4, 3, 3, 14};

    private int numChannels;
    private int bins;
    private int windowSize;

    public double[] maxFFTValue;
    public double[] meanFFTValue;
    public double[] varFFTValue;

    public double trainingFactor = .10d;

    private String[] binLabels = {"theta", "lowalpha", "highalpha", "beta"};
    private int maxSampleCount = 10000;
    private double peakToPeakLimit = 20d;

    private int valueMin = 4;
    private int valueMax = 34;


    public boolean isPeakToPeakCheck() {
        return peakToPeakCheck;
    }

    public boolean[] getNotBrainwaves() {
        return notBrainwaves;
    }

    public int[] getPackagePenalty() {
        return packagePenalty;
    }

    public double[][] getCurrentFFTs() {
        return currentFFTs;
    }

    public double[][] getMeanFFTs() {
        return meanFFTs;
    }

    public double[][] getCurrentFFTPhases() {
        return currentFFTPhases;
    }

    public double[][] getWindows() {
        return windows;
    }

    public double[][] getCurrentFFTBins() {
        return currentFFTBins;
    }

    public int[][] getMeanFFTValueCount() {
        return meanFFTValueCount;
    }

    public double[][] getMeanFFTBins() {
        return meanFFTBins;
    }

    public double[][] getVarFFTBins() {
        return varFFTBins;
    }

    public double[][] getRelativeFFTBins() {
        return relativeFFTBins;
    }

    public double[][] getBaselineFFTValues() {
        return baselineFFTValues;
    }

    public double[][] getShortMeanFFTBins() {
        return shortMeanFFTBins;
    }

    public double[][] getShortVarFFTBins() {
        return shortVarFFTBins;
    }

    public double[][] getRewardFFTBins() {
        return rewardFFTBins;
    }

    public double[] getCurrentFFTValue() {
        return currentFFTValue;
    }

    public int[] getBinRanges() {
        return binRanges;
    }

    public int[] getBinRangesAmount() {
        return binRangesAmount;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getBins() {
        return bins;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public double[] getMaxFFTValue() {
        return maxFFTValue;
    }

    public double[] getMeanFFTValue() {
        return meanFFTValue;
    }

    public double[] getVarFFTValue() {
        return varFFTValue;
    }

    public String[] getBinLabels() {
        return binLabels;
    }

    public int getMaxSampleCount() {
        return maxSampleCount;
    }

    public double getPeakToPeakLimit() {
        return peakToPeakLimit;
    }

    public int getValueMin() {
        return valueMin;
    }

    public int getValueMax() {
        return valueMax;
    }

    public void setPeakToPeakLimit(double peakToPeakLimit) {
        this.peakToPeakLimit = peakToPeakLimit;
    }

    public DefaultFFTData() {
    }

    public DefaultFFTData(NeuroSettings bciSettings) {
        this(bciSettings.getSamplesPerSecond(), bciSettings.getBins(), bciSettings.getNumChannels());
    }

    public DefaultFFTData(int samplesPerSecond, int bins, int numChannels) {
        init(samplesPerSecond, bins, numChannels);
    }

    public void setFbCount(int fbCount) {
        this.maxSampleCount = fbCount;

    }

    public int getFbCount() {
        return maxSampleCount;
    }

    public void init(int samplesPerSecond, int bins, int numChannels) {
        this.windowSize = samplesPerSecond;
        this.bins = bins;
        this.numChannels = numChannels;

        this.windows = new double[numChannels][samplesPerSecond];
        this.currentFFTs = new double[numChannels][samplesPerSecond];
        this.meanFFTs = new double[numChannels][samplesPerSecond];

        this.meanFFTValue = new double[numChannels];
        this.varFFTValue = new double[numChannels];

        this.currentFFTPhases = new double[numChannels][samplesPerSecond];
        this.currentFFTValue = new double[numChannels];
        this.currentFFTBins = new double[bins][numChannels];
        this.meanFFTValueCount = new int[bins][numChannels];
        this.meanFFTBins = new double[bins][numChannels];
        this.varFFTBins = new double[bins][numChannels];
        this.relativeFFTBins = new double[bins][numChannels];
        this.baselineFFTValues = new double[bins][numChannels];
        this.shortMeanFFTBins = new double[bins][numChannels];
        this.shortVarFFTBins = new double[bins][numChannels];
        this.rewardFFTBins = new double[bins][numChannels];
        this.currentFFTValue = new double[numChannels];
        this.maxFFTValue = new double[numChannels];
        this.notBrainwaves = new boolean[numChannels];
        this.packagePenalty = new int[numChannels];


        for (int c = 0; c < numChannels; c++) {
            this.maxFFTValue[c] = 1d;
            this.currentFFTValue[c] = .01d;
            this.meanFFTValue[c] = .5d;
        }
    }

    @Override
    public void setTrainingFactor(double trainingFactor) {
        this.trainingFactor = trainingFactor;
        System.out.println("set training factor to " + trainingFactor);
    }

    @Override
    public double getTrainingFactor() {
        return this.trainingFactor;
    }

    public void updateFFTData(double[][] fftData) {
        for (int c = 0; c < numChannels; c++) {
            if ((peakToPeakCheck) && (packagePenalty[c] > 0)) {
                packagePenalty[c]--;
                continue;
            }

            for (int f = 0; f < windowSize; f++)
                meanFFTs[c][f] = MathBasics.updateMean(meanFFTs[c][f], windowSize / 2, fftData[c][f]);

            for (int b = 0; b < bins; b++) {

                currentFFTBins[b][c] = 0d;
                for (int i = binRanges[(b * 2)]; i <= binRanges[(b * 2) + 1]; i++) {
                    if (i < currentFFTs.length)
                        currentFFTBins[b][c] += currentFFTs[c][i];
                }


                currentFFTBins[b][c] /= binRangesAmount[b];

                if (meanFFTValueCount[b][c] < maxSampleCount)
                    meanFFTValueCount[b][c] += 1;
                else
                    meanFFTValueCount[b][c] = maxSampleCount;

                if (meanFFTValueCount[b][c] <= 1)
                    meanFFTValueCount[b][c] = 2; //min default

                //long-term mean
                meanFFTBins[b][c] = (meanFFTBins[b][c] * trainingFactor) + MathBasics.updateMean(meanFFTBins[b][c], meanFFTValueCount[b][c], currentFFTBins[b][c]) * (1d - trainingFactor);
                varFFTBins[b][c] = (varFFTBins[b][c] * trainingFactor) + MathBasics.updateVariance(varFFTBins[b][c], meanFFTValueCount[b][c], currentFFTBins[b][c], meanFFTBins[b][c]) * (1d - trainingFactor);

                //short-term mean
                shortMeanFFTBins[b][c] = MathBasics.updateMean(shortMeanFFTBins[b][c], Math.min(250, meanFFTValueCount[b][c]), currentFFTBins[b][c]);
                shortVarFFTBins[b][c] = MathBasics.updateVariance(shortVarFFTBins[b][c], Math.min(250, meanFFTValueCount[b][c]), currentFFTBins[b][c], meanFFTBins[b][c]);

            }

            maxFFTValue[c] = Math.max(maxFFTValue[c], currentFFTValue[c]);

            meanFFTValue[c] = MathBasics.updateMean(meanFFTValue[c], meanFFTValueCount[0][c], currentFFTValue[c]);
            varFFTValue[c] = MathBasics.updateVariance(varFFTValue[c], meanFFTValueCount[0][c], currentFFTValue[c], meanFFTValue[c]);
        }
    }

    @Override
    public void updateFFTData() {
        updateFFTData(currentFFTs);
    }

    public void setBinRanges(int[] binRanges) {
        this.binRanges = binRanges;
        this.binRangesAmount = new int[binRanges.length / 2];
        this.bins = this.binRangesAmount.length;
        System.out.println();
        System.out.println("setting " + this.bins + " bins ; bin ranges: " + binRanges.length);

        for (int i = 0; i < this.binRangesAmount.length; i++)
            this.binRangesAmount[i] = (binRanges[i * 2 + 1] - binRanges[i * 2] + 1) * maxSampleCount + (int) peakToPeakLimit;

        init(this.windowSize, this.bins, this.numChannels);
    }

    public int[] getBinRangeValues() {
        setBinRanges(binRanges);
        return binRangesAmount;
    }

    public void reset() {
        init(windowSize, bins, numChannels);
//		for (int c = 0; c < numChannels; c++)
//		{
//			for (int b = 0; b < bins; b++)
//				meanFFTValueCount[b][c] = 1;
//		}
    }
}
