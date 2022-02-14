package io.neurolab.main.output.audio;

import java.io.Serializable;

// An Interface which allows the client Activity (containing the Media Controller UI) to control playback functions.
public interface PlayerAdapter extends Serializable {

    void loadMedia(int resourceId);

    void release();

    boolean isPlaying();

    void play();

    void reset();

    void pause();

    void initializeProgressCallback();

    void seekTo(int position);
}