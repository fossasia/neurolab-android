package io.neurolab.interfaces;

public interface InputInterface extends Runnable {

    int shutDown();
    boolean isConnectionSuccessful();
    boolean sendCommand(String string);
    boolean record(String filename);
    void stopRecording();
    boolean isRecording();

}
