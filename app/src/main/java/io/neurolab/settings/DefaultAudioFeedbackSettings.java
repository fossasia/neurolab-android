package io.neurolab.settings;

public class DefaultAudioFeedbackSettings {

    private String sample0 = "audio/pad_.wav";
    private String sample1 = "audio/newpad.wav";
    private String sample2 = "audio/wind.wav";
    private String sample3 = "audio/pad2.wav";
    private String sample4 = "audio/forest.wav";

    private double volume0 = 0.93621325;
    private double volume1 = 0.04654903;
    private double volume2 = 0.041319266;
    private double volume3 = 2.5E-5;
    private double volume4 = 0.7;

    private int x = 19;
    private int y = 17;

    public String getSample0() {
        return sample0;
    }

    public void setSample0(String sample0) {
        this.sample0 = sample0;
    }

    public String getSample1() {
        return sample1;
    }

    public void setSample1(String sample1) {
        this.sample1 = sample1;
    }

    public String getSample2() {
        return sample2;
    }

    public void setSample2(String sample2) {
        this.sample2 = sample2;
    }

    public String getSample3() {
        return sample3;
    }

    public void setSample3(String sample3) {
        this.sample3 = sample3;
    }

    public String getSample4() {
        return sample4;
    }

    public void setSample4(String sample4) {
        this.sample4 = sample4;
    }

    public double getVolume0() {
        return volume0;
    }

    public void setVolume0(double volume0) {
        this.volume0 = volume0;
    }

    public double getVolume1() {
        return volume1;
    }

    public void setVolume1(double volume1) {
        this.volume1 = volume1;
    }

    public double getVolume2() {
        return volume2;
    }

    public void setVolume2(double volume2) {
        this.volume2 = volume2;
    }

    public double getVolume3() {
        return volume3;
    }

    public void setVolume3(double volume3) {
        this.volume3 = volume3;
    }

    public double getVolume4() {
        return volume4;
    }

    public void setVolume4(double volume4) {
        this.volume4 = volume4;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Converts to the string representation and can be used for debugging purposes.
    @Override
    public String toString() {
        return "sample0 = " + sample0 + " | " + "sample1 = " + sample1 + " | " + "sample2 = " + sample2 + " | "
                + "sample3 = " + sample3 + " | " + "sample4 = " + sample4 + " | " + "volume0 = " + volume0 + " | "
                + "volume1 = " + volume1 + " | " + "volume2 = " + volume2 + " | " + "volume3 = " + volume3 + " | "
                + "volume4 = " + volume4 + " | " + "x = " + x + " | " + "y = " + y;
    }

}
