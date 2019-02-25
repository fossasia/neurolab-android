package io.neurolab.model;

import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.MathBasics;
import io.neurolab.settings.FeedbackSettings;

public class FocusFeedbackSettings extends FeedbackSettings {
    protected int[] binRanges = {4, 7, 12, 18, 22, 35};
    protected int[] binRangesAmount = {4, 7, 14};

    @Override
    public String getFeedbackSettingsName() {
        return "focus";
    }

    public FocusFeedbackSettings(DefaultFFTData fftData, ReentrantLock lock, Config config) {
        super(fftData, lock, config);
        this.binLabels = new String[]{"theta", "smr", "high beta"};
        this.fftData.setBinRanges(binRanges);
        this.fftData.binLabels = this.binLabels;
        this.bins = 3;
    }

    @Override
    public void updateFeedback() {
        for (int c = 0; c < fftData.numChannels; c++)
            for (int b = 0; b < fftData.bins; b++)
                fftData.rewardFFTBins[b][c] = MathBasics.getZScore(fftData.shortMeanFFTBins[b][c], fftData.meanFFTBins[b][c], Math.sqrt(fftData.varFFTBins[b][c]));

        currentFeedback = 0;
        for (int c = 0; c < fftData.numChannels; c++) {
            for (int b = 0; b < binRangesAmount.length; b++) {
                double rewardBin = fftData.rewardFFTBins[b][c] * -1d;
                if (b == 1)
                    rewardBin *= -2d;
                currentFeedback += rewardBin;
            }
            currentFeedback /= (double) binRangesAmount.length;
            currentFeedback /= (float) (fftData.numChannels);
            lastFeedback = currentFeedback;
        }
        super.updateFeedback();
    }

}