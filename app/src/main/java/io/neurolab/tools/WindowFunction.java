package io.neurolab.tools;

public class WindowFunction {

    public enum FunctionType {
        RECTANGULAR, HANNING, HAMMING, BLACKMAN
    }

    FunctionType windowType = FunctionType.HAMMING;
    private double[] filter;
    private FunctionType type;
    private final static double freq = 1d;

    // private static double[] staticFilter = generate(8,FunctionType.HAMMING);
    private static double[] staticFilter = generate(4, FunctionType.HAMMING); // default

    public static void setStaticFilter(int width, FunctionType type) {
        WindowFunction.staticFilter = generate(width, type);
    }

    public WindowFunction() {
    }

    public WindowFunction(int filterWidth, FunctionType type) {
        this.filter = generate(filterWidth, type);
        this.type = type;

    }

    public void setWindowType(FunctionType w) {
        windowType = w;
    }

    public FunctionType getWindowType() {
        return windowType;
    }

    public static double[] generate(int nSamples, FunctionType windowType) {
        int m = nSamples / 2;
        double r;
        double pi = Math.PI;
        double[] w = new double[nSamples];
        switch (windowType) {
            case HANNING:
                r = pi / (m + 1);
                for (int n = -m; n < m; n++)
                    w[m + n] = 0.5f + 0.5f * Math.cos(n * r);
                break;
            case HAMMING: // Hamming window
                r = pi / m;
                for (int n = -m; n < m; n++)
                    w[m + n] = 0.54f + 0.46f * Math.cos(n * r);
                break;
            case BLACKMAN: // Blackman window
                r = pi / m;
                for (int n = -m; n < m; n++)
                    w[m + n] = 0.42f + 0.5f * Math.cos(n * r) + 0.08f * Math.cos(2 * n * r);
                break;
            default: // Rectangular window function
                for (int n = 0; n < nSamples; n++)
                    w[n] = 1.0f;
        }

        for (int i = 0; i < w.length; i++)
            System.out.print(w[i] +  ", ");

        return w;
    }

    public void setWindow(int filterWidth, FunctionType type) {
        this.filter = generate(filterWidth, type);
        this.type = type;
    }

    public static double[] convolveDefault(double[] input) {
        return convolve(input, staticFilter);
    }

    public double[] convolve(double[] input) {

        if ((filter == null) || (type == null)) {
            return null;
        }

        return convolve(input, filter);
    }

    public static double[] convolve(double[] input, double[] filter) {
        double[] conv = new double[input.length];

        // fade in and out
        if (input.length == filter.length) {
            for (int i = 0; i < input.length; i++) {

                conv[i] += input[i] * filter[i];
            }
        } else // convolution
        {
            for (int i = 0; i < input.length; i++) {
                double convolutedValue = 0;
                int halfW = filter.length / 2;
                int offset = 0;
                for (int j = 0; j < filter.length; j++) {
                    offset = i - halfW + j;
                    if (offset >= input.length)
                        offset = input.length - 1;
                    else if (offset < 0)
                        offset = 0;

                    convolutedValue += input[offset] * filter[j];
                }

                conv[i] = convolutedValue / (filter.length / 2) - filter.length / 2;
            }

        }

        return conv;

    }

    public double[] getFilter() {
        return filter;
    }

    public FunctionType getType() {
        return type;
    }

    public double[] shift(double[] conv) {
        double[] newArray = new double[conv.length];

        for (int i = 0; i < conv.length / 2; i++) {
            newArray[i] = conv[conv.length / 2 + i];
            newArray[conv.length / 2 + i] = conv[i];
        }
        return newArray;
    }

}