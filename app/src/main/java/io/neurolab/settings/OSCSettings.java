package io.neurolab.settings;

public class OSCSettings {

    // The IP address and port would be modified once the networking functionalities are added successfully.
    // For the time being it just contains a dummy placeholder value.
    private String ip = "A.B.C.D";
    private String port = "E";

    private String address0 = "/lowleft";
    private String address1 = "/al";
    private String address2 = "/ch0/highalpha";
    private String address3 = "/highleft";
    private String address4 = "/lowright";
    private String address5 = "/ar";
    private String address6 = "/ch1/highalpha";
    private String address7 = "/highright";

    private double address0Min = -0.33;
    private double address1Min = -0.22;
    private double address2Min = -0.89;
    private double address3Min = -0.33;
    private double address4Min = -0.22;
    private double address5Min = -1.46;
    private double address6Min = -1.31;
    private double address7Min = -0.22;

    private double address0Max = 1.34;
    private double address1Max = 2.4;
    private double address2Max = 2.93;
    private double address3Max = 1.1;
    private double address4Max = 0.92;
    private double address5Max = 3.0;
    private double address6Max = 3.0;
    private double address7Max = 1.48;

    private String feedback = "/feedback/";

    private int mode = 0;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAddress0() {
        return address0;
    }

    public void setAddress0(String address0) {
        this.address0 = address0;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getAddress6() {
        return address6;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public String getAddress7() {
        return address7;
    }

    public void setAddress7(String address7) {
        this.address7 = address7;
    }

    public double getAddress0Min() {
        return address0Min;
    }

    public void setAddress0Min(double address0Min) {
        this.address0Min = address0Min;
    }

    public double getAddress1Min() {
        return address1Min;
    }

    public void setAddress1Min(double address1Min) {
        this.address1Min = address1Min;
    }

    public double getAddress2Min() {
        return address2Min;
    }

    public void setAddress2Min(double address2Min) {
        this.address2Min = address2Min;
    }

    public double getAddress3Min() {
        return address3Min;
    }

    public void setAddress3Min(double address3Min) {
        this.address3Min = address3Min;
    }

    public double getAddress4Min() {
        return address4Min;
    }

    public void setAddress4Min(double address4Min) {
        this.address4Min = address4Min;
    }

    public double getAddress5Min() {
        return address5Min;
    }

    public void setAddress5Min(double address5Min) {
        this.address5Min = address5Min;
    }

    public double getAddress6Min() {
        return address6Min;
    }

    public void setAddress6Min(double address6Min) {
        this.address6Min = address6Min;
    }

    public double getAddress7Min() {
        return address7Min;
    }

    public void setAddress7Min(double address7Min) {
        this.address7Min = address7Min;
    }

    public double getAddress0Max() {
        return address0Max;
    }

    public void setAddress0Max(double address0Max) {
        this.address0Max = address0Max;
    }

    public double getAddress1Max() {
        return address1Max;
    }

    public void setAddress1Max(double address1Max) {
        this.address1Max = address1Max;
    }

    public double getAddress2Max() {
        return address2Max;
    }

    public void setAddress2Max(double address2Max) {
        this.address2Max = address2Max;
    }

    public double getAddress3Max() {
        return address3Max;
    }

    public void setAddress3Max(double address3Max) {
        this.address3Max = address3Max;
    }

    public double getAddress4Max() {
        return address4Max;
    }

    public void setAddress4Max(double address4Max) {
        this.address4Max = address4Max;
    }

    public double getAddress5Max() {
        return address5Max;
    }

    public void setAddress5Max(double address5Max) {
        this.address5Max = address5Max;
    }

    public double getAddress6Max() {
        return address6Max;
    }

    public void setAddress6Max(double address6Max) {
        this.address6Max = address6Max;
    }

    public double getAddress7Max() {
        return address7Max;
    }

    public void setAddress7Max(double address7Max) {
        this.address7Max = address7Max;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
        return "ip = " + ip + " | " + "port = " + port + " | " + "address0 = " + address0 + " | "
                + "address1 = " + address1 + " | " + "address2 = " + address2 + " | "
                + "address3 = " + address3 + " | " + "address4 = " + address4 + " | "
                + "address5 = " + address5 + " | " + "address6 = " + address6 + " | "
                + "address0 = " + address7 + " | " + "address0Min = " + address0Min + " | "
                + "address1Min = " + address1Min + " | " + "address2Min = " + address2Min + " | "
                + "address3Min = " + address3Min + " | " + "address4Min = " + address4Min + " | "
                + "address5Min = " + address5Min + " | " + "address6Min = " + address6Min + " | "
                + "address7Min = " + address7Min + " | " + "address0Max = " + address0Max + " | "
                + "address1Max = " + address1Max + " | " + "address2Max = " + address2Max + " | "
                + "address3Max = " + address3Max + " | " + "address4Max = " + address4Max + " | "
                + "address5Max = " + address5Max + " | " + "address6Max = " + address6Max + " | "
                + "address7Max = " + address7Max + " | " + "feedback = " + feedback + " | "
                + "mode = " + mode;
    }

}
