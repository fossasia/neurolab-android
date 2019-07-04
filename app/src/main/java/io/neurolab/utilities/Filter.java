package io.neurolab.utilities;

public class Filter {

    private double resonance;
    private double frequency;
    private int sampleRate;
    private boolean lowPass;
    private double c, a1, a2, a3, b1, b2;
    private double[] inputHistory = new double[2];
    private double[] outputHistory = new double[3];

    public Filter() {
    }

    public Filter(double frequency, int sampleRate, boolean lowPass, double resonance) {
        setFilter(frequency, sampleRate, lowPass, resonance);
    }

    private void setCoefficients() {
        if (lowPass) {
            c = 1.0f / (float) Math.tan((Math.PI * frequency) / sampleRate);
            a1 = 1.0f / (1.0f + (resonance * c) + (c * c));
            a2 = 2f * a1;
            a3 = a1;
            b1 = 2.0f * (1.0f - (c * c)) * a1;
            b2 = (1.0f - (resonance * c) + (c * c)) * a1;

        } else {    // high Pass filter
            c = (float) Math.tan((Math.PI * frequency) / sampleRate);
            a1 = 1.0f / (1.0f + (resonance * c) + (c * c));
            a2 = -2f * a1;
            a3 = a1;
            b1 = 2.0f * ((c * c) - 1.0f) * a1;
            b2 = (1.0f - (resonance * c) + (c * c)) * a1;
        }
    }

    public void setFilter(double frequency, int sampleRate, boolean lowPass, double resonance) {
        this.resonance = resonance;
        this.frequency = frequency;
        this.sampleRate = sampleRate;
        this.lowPass = lowPass;
        setCoefficients();
    }

    public double update(double newInput) {
        double newOutput = (a1 * newInput) + (a2 * this.inputHistory[0]) + (a3 * this.inputHistory[1])
                - (b1 * this.outputHistory[0]) - (b2 * this.outputHistory[1]);

        this.inputHistory[1] = this.inputHistory[0];
        this.inputHistory[0] = newInput;

        this.outputHistory[2] = this.outputHistory[1];
        this.outputHistory[1] = this.outputHistory[0];
        this.outputHistory[0] = newOutput;

        return this.outputHistory[0];
    }

    public double getValue() {
        return this.outputHistory[0];
    }

}
