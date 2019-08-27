package io.neurolab.settings;

public class NFBRelaxSettings {

    private double duration = 25;
    private int currentSession = 0;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(int currentSession) {
        this.currentSession = currentSession;
    }

    // Converts to the string representation and can be used for debugging purposes.
    @Override
    public String toString() {
        return "duration = " + duration + " | " + "currentSession = " + currentSession;
    }

}
