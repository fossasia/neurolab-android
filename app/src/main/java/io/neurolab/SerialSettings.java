package io.neurolab;

public class SerialSettings {

    private String address = "/dev/ttyACM0";

    private double baudrate = 230400;

    private double message0Min = -0.11;
    private double message1Min = -0.88;
    private double message2Min = -0.89;
    private double message3Min = -0.64;
    private double message4Min = -1.99;
    private double message0Max = 0.18;
    private double message1Max = 0.79;
    private double message2Max = 1.26;
    private double message3Max = 2.33;
    private double message4Max = 3.0;

    private int mode = 0;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(double baudrate) {
        this.baudrate = baudrate;
    }

    public double getMessage0Min() {
        return message0Min;
    }

    public void setMessage0Min(double message0Min) {
        this.message0Min = message0Min;
    }

    public double getMessage1Min() {
        return message1Min;
    }

    public void setMessage1Min(double message1Min) {
        this.message1Min = message1Min;
    }

    public double getMessage2Min() {
        return message2Min;
    }

    public void setMessage2Min(double message2Min) {
        this.message2Min = message2Min;
    }

    public double getMessage3Min() {
        return message3Min;
    }

    public void setMessage3Min(double message3Min) {
        this.message3Min = message3Min;
    }

    public double getMessage4Min() {
        return message4Min;
    }

    public void setMessage4Min(double message4Min) {
        this.message4Min = message4Min;
    }

    public double getMessage0Max() {
        return message0Max;
    }

    public void setMessage0Max(double message0Max) {
        this.message0Max = message0Max;
    }

    public double getMessage1Max() {
        return message1Max;
    }

    public void setMessage1Max(double message1Max) {
        this.message1Max = message1Max;
    }

    public double getMessage2Max() {
        return message2Max;
    }

    public void setMessage2Max(double message2Max) {
        this.message2Max = message2Max;
    }

    public double getMessage3Max() {
        return message3Max;
    }

    public void setMessage3Max(double message3Max) {
        this.message3Max = message3Max;
    }

    public double getMessage4Max() {
        return message4Max;
    }

    public void setMessage4Max(double message4Max) {
        this.message4Max = message4Max;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    // Converts to the string representation and can be used for debugging purposes.
    @Override
    public String toString() {
        return "address = " + address + " | " + "baudrate = " + baudrate + " | " + "message0Min = " + message0Min + " | "
                + "message1Min = " + message1Min + " | " + "message2Min = " + message2Min + " | "
                + "message3Min = " + message3Min + " | " + "message4Min = " + message4Min + " | "
                + "message0Max = " + message0Max + " | " + "message1Max = " + message1Max + " | "
                + "message2Max = " + message2Max + " | " + "message3Max = " + message3Max + " | "
                + "message4Max = " + message4Max + " | " +  "mode = " + mode;
    }

}
