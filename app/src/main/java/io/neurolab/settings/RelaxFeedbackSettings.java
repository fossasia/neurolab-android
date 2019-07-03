package io.neurolab.settings;

import java.util.concurrent.locks.ReentrantLock;

import io.neurolab.utilities.MathBasics;
import io.neurolab.model.Config;
import io.neurolab.model.DefaultFFTData;

public class RelaxFeedbackSettings extends FeedbackSettings {
    private float lastFeedback;


    public double trainingFactor = 0.1;

    @Override
    public String getFeedbackSettingsName() {
        return "relax";
    }

    public RelaxFeedbackSettings(DefaultFFTData fftData, ReentrantLock lock, Config config) {
        super(fftData, lock, config);
        this.binLabels = new String[]{"low theta", "theta", "alpha", "beta"};
        this.binRanges = new int[]{3, 4, 5, 9, 10, 12, 15, 35};
        this.binRangesAmount = new int[]{2, 5, 3, 21};
        this.fftData.setBinRanges(binRanges);
        String[] binLabels = this.fftData.getBinLabels();
        binLabels = this.binLabels;
    }

    @Override
    public void updateFeedback() {

        for (int c = 0; c < fftData.getNumChannels(); c++)
            for (int b = 0; b < fftData.getBins(); b++) {
                double oldValue = fftData.getRewardFFTBins()[b][c];
                if (fftData.getPackagePenalty()[c] > 0) {
                    fftData.getRewardFFTBins()[b][c] = oldValue * .15d;
                    continue;
                }
                fftData.getRewardFFTBins()[b][c] = MathBasics.getZScore(fftData
                                .getShortMeanFFTBins()[b][c], fftData.getMeanFFTBins()[b][c],
                        Math.sqrt(fftData.getVarFFTBins()[b][c]));
            }

        if (!Float.isNaN(currentFeedback))
            lastFeedback = currentFeedback;

        currentFeedback = 0;
        double totalReward = 0d;
        for (int c = 0; c < fftData.getNumChannels(); c++) {
            for (int b = 0; b < binRangesAmount.length; b++) {
                double rewardBin = fftData.getRewardFFTBins()[b][c] * -1d;
                totalReward += rewardBin;
                if (b == 0)
                    rewardBin *= 0.25d;
                if ((b == 1) || (b == 2))
                    rewardBin *= -3.75d;
                if (b == 3)
                    rewardBin *= 6.75d;
                currentFeedback += rewardBin;
            }
        }
        currentFeedback /= (double) binRangesAmount.length;
        currentFeedback /= (float) (fftData.getNumChannels());

        currentFeedback *= getSensitivity();

        lastFeedback = currentFeedback;

        super.updateFeedback();
    }


}
