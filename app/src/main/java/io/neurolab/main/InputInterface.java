package io.neurolab.main;

public interface InputInterface extends Runnable {

    public int shutDown();
    public boolean isConnectionSuccessful();
    public boolean sendCommand(String string);
    public boolean record(String filename);
    public void stopRecording();
    public boolean isRecording();

}
