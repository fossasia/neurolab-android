package io.neurolab.utilities;

import java.util.Arrays;

public class MathBasics {
    public static double getMedian(double[] input) {
        double[] values = input.clone();

        Arrays.sort(values);
        int middle = ((values.length) / 2);
        double median;
        if (values.length % 2 == 0) {
            double medianA = values[middle];
            double medianB = values[middle - 1];
            median = (medianA + medianB) / 2d;
        } else {
            median = values[middle];
        }
        return median;
    }

    public static double updateMean(double currentMean, int n, double val) {
        if(n <= 0)
            throw new IllegalArgumentException("Number of elements(n) cannot be less than or equal to 0");
        double nD = (double) n;
        return ( (nD - 1) * currentMean + val ) / nD;
    }

    public static double updateVariance(double currentVariance, int n, double newValue, double currentMean) {
        double nD = (double) n;
        if(n <= 1)
            throw new IllegalArgumentException("Number of elements(n) cannot be less than or equal to 1");
        else{
            double result = (newValue - currentMean) * (newValue - currentMean); // 1/n * xn
            result = (result / (nD - 1));
            result += (((nD - 1) / (nD)) * currentVariance); // + (n-1)/n * (n-1)
            return result;
        }
    }

    public static double getZScore(double value, double mean, double standardDeviation) {
        return (value - mean) / standardDeviation;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double[] getLPCoefficientsButterworth2Pole(int sampleRate, double cutoff) {
        double[] coeffs = new double[6];
        double PI = 3.1415926535897932385d;
        double sqrt2 = 1.4142135623730950488d;
        double QcRaw = (2d * PI * cutoff) / (sampleRate); // find cutoff frequency
        double QcWarp = Math.tan(QcRaw); // warp cutoff frequency

        double gain = (1d / (1d + sqrt2 / QcWarp + 2d / (QcWarp * QcWarp)));
        coeffs[0] = 1d * gain;
        coeffs[1] = 2d * gain;
        coeffs[2] = 1d * gain;

        coeffs[3] = (1d - sqrt2 / QcWarp + 2d / (QcWarp * QcWarp)) * gain;   //by[0]
        coeffs[4] = (2d - 2d * 2d / (QcWarp * QcWarp)) * gain;                 //by[1]
        coeffs[5] = 1d;                                                         //by[2]

        return coeffs;

    }

    public static double[] filter(double[] samples, double[] coeffs) {

        // coeffs = getLPCoefficientsButterworth2Pole(44100, 40);
        double[] newSamples = new double[samples.length];
        double[] xv = new double[3];
        double[] yv = new double[3];

        for (int i = 0; i < samples.length; i++) {
            xv[2] = xv[1];
            xv[1] = xv[0];
            xv[0] = samples[i];
            yv[2] = yv[1];
            yv[1] = yv[0];

            yv[0] = (coeffs[0] * xv[0] + coeffs[1] * xv[1] + coeffs[2] * xv[2] - coeffs[4] * yv[0] - coeffs[5] * yv[1]);
            newSamples[i] = yv[0];
        }
        return newSamples;
    }

}
