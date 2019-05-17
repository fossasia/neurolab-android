package io.neurolab.model;

import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.main.MathBasics;
import io.neurolab.settings.FeedbackSettings;

public class GenericFeedbackSettings extends FeedbackSettings {
    protected int[] binRanges = {4, 7, 8, 11, 12, 17, 18, 35};
    protected int[] binRangesAmount = {4, 4, 6, 18};
    private String[] binlabels;

    @Override
    public String getFeedbackSettingsName() {
        return "generic";
    }

    public GenericFeedbackSettings(DefaultFFTData fftData, ReentrantLock lock, Config config) {
        super(fftData, lock, config);

        this.binLabels = new String[]{"theta", "lowalpha", "highalpha", "beta"};
        this.fftData.setBinRanges(binRanges);
        binlabels = this.fftData.getBinLabels();
        binlabels = this.binLabels;
    }

    @Override
    public void updateFeedback() {

        for (int c = 0; c < fftData.getNumChannels(); c++)
            for (int b = 0; b < fftData.getBins(); b++)
                fftData.getRelativeFFTBins()[b][c] = MathBasics.getZScore(fftData.getShortMeanFFTBins()[b][c], fftData.getMeanFFTBins()[b][c], Math.sqrt(fftData.getVarFFTBins()[b][c]));

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
