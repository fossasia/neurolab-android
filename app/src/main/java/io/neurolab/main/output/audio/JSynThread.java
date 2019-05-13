package io.neurolab.main.output.audio;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;

public class JSynThread implements Runnable {

    private Synthesizer synth;

    @Override
    public void run() {
        synth = JSyn.createSynthesizer( AudioDeviceFactory.createAudioDeviceManager( true ));
    }

    public synchronized Synthesizer getSynth() {
        return synth;
    }

}