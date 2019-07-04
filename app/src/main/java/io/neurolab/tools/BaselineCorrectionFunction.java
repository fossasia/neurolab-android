package io.neurolab.tools;

import android.annotation.TargetApi;

import java.util.function.Function;

import io.neurolab.utilities.MathBasics;

@TargetApi(24)
public class BaselineCorrectionFunction implements Function<Double, Double> {

    private int sampleSize;
    private int sampleCount;
    private double currentMean;

    public BaselineCorrectionFunction(int sampleSize) {
        this.sampleSize = sampleSize;
        this.sampleCount = 0;
    }

    @Override
    public Double apply(Double value) {
        incrementSampleCount();

        currentMean = MathBasics.updateMean(currentMean, sampleCount, value);
        return value - currentMean;
    }

    private void incrementSampleCount() {
        sampleCount = sampleCount < sampleSize ? sampleCount + 1 : sampleCount;
    }

}
