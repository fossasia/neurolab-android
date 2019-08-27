package io.neurolab.settings;

import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.utilities.MathBasics;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;

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
        String[] localBinlabels = this.fftData.getBinLabels();
        localBinlabels = this.binLabels;
        this.bins = 3;
    }

    @Override
    public void updateFeedback() {
        for (int c = 0; c < fftData.getNumChannels(); c++)
            for (int b = 0; b < fftData.getBins(); b++)
                fftData.getRewardFFTBins()[b][c] = MathBasics.getZScore(fftData.getShortMeanFFTBins()[b][c], fftData.getMeanFFTBins()[b][c], Math.sqrt(fftData.getVarFFTBins()[b][c]));

        currentFeedback = 0;
        for (int c = 0; c < fftData.getNumChannels(); c++) {
            for (int b = 0; b < binRangesAmount.length; b++) {
                double rewardBin = fftData.getRewardFFTBins()[b][c] * -1d;
                if (b == 1)
                    rewardBin *= -2d;
                currentFeedback += rewardBin;
            }
            currentFeedback /= (double) binRangesAmount.length;
            currentFeedback /= (float) (fftData.getNumChannels());
            lastFeedback = currentFeedback;
        }
        super.updateFeedback();
    }

}