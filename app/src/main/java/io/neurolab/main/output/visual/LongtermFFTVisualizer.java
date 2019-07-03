package io.neurolab.main.output.visual;

import com.jogamp.newt.event.MouseEvent;

import io.neurolab.main.NFBServer;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.interfaces.Task;
import io.neurolab.settings.FeedbackSettings;

public class LongtermFFTVisualizer implements Task {

    private DefaultFFTData fftData;

    private int windowWidth = 800;
    private int windowHeight = 600;

    private int topOffset = 50;
    private int displayHeight = 550;

    private int numChannels = 2;
    private int heightPerChannel = displayHeight / numChannels;

    private int sideBorder = 150;
    private NFBServer nfbServer;

    private int fftHeight = 256;
    private int nSteps = 8;
    private int step = fftHeight / nSteps;

    private int playbackSpeed = 1;
    private int numPlaybackModes = 3;

    private int freqZoom = 4;
    private double maxFFT = 250;
    private double minFFT = 0;

    private long lastTimestamp = 0l;
    private FeedbackSettings fbSettings;
    int[] sampleCounter;

    public LongtermFFTVisualizer(DefaultFFTData fftData, NFBServer nfbServer) {
        this.sampleCounter = new int[numPlaybackModes];
        this.nfbServer = nfbServer;
        this.fftData = fftData;
        this.numChannels = nfbServer.getNumChannels();
        this.displayHeight = windowHeight - topOffset;
        this.fbSettings = nfbServer.getCurrentFeedbackSettings();
    }

    public void mouseClicked(MouseEvent e) {
        playbackSpeed++;
        if (playbackSpeed >= numPlaybackModes)
            playbackSpeed = 0;
    }

    @Override
    public void init() {
        // initialize the Visualizer
    }

    @Override
    public void run() {
        if (nfbServer.getNumSamples() < 1)
            return;

        int numSamples = nfbServer.getNumSamples();
        for (int i = 0; i < numPlaybackModes; i++) {
            sampleCounter[i] += numSamples;
        }

        for (int c = 0; c < numChannels; c++)
            for (int s = 0; s < numSamples; s++) {
                for (int i = 0; i < numChannels; i++) {

                    for (int f = 0; f < fftData.getCurrentFFTs()[i].length / 8; f++) {

                        maxFFT = Math.max(maxFFT, fftData.getCurrentFFTs()[i][f]);
                        minFFT = Math.min(minFFT, fftData.getCurrentFFTs()[i][f]);

                        int colorVal = (int) (((fftData.getCurrentFFTs()[i][f] - minFFT) / maxFFT) * 255d);

                        if (colorVal > 255)
                            colorVal = 255;
                        else if (colorVal < 0)
                            colorVal = 0;
                    }
                }
                if (this.fbSettings != null) {
                    int colorVal = (int) (this.fbSettings.getCurrentFeedback() * 1024d);

                    if (colorVal > 255)
                        colorVal = 255;
                    else if (colorVal < 0)
                        colorVal = 0;
                }
            }
        for (int p = 0; p < numPlaybackModes; p++) {
            if (sampleCounter[p] >= (p + 1) * nfbServer.getSamplesPerSecond() / 4)
                for (int c = 0; c < numChannels; c++) {

                    for (int f = 0; f < fftData.getCurrentFFTs()[c].length / (freqZoom * 2); f++) {
                        int colorVal = (int) (((fftData.getMeanFFTs()[c][f] - minFFT) / maxFFT) * 255d);
                        if (colorVal > 255)
                            colorVal = 255;
                        else if (colorVal < 0)
                            colorVal = 0;
                    }
                }
            if (this.fbSettings != null) {
                int colorVal = (int) (this.fbSettings.getCurrentFeedback() * 1024d);

                if (colorVal > 255)
                    colorVal = 255;
                else if (colorVal < 0)
                    colorVal = 0;
            }

            sampleCounter[p] = sampleCounter[p] - ((p + 1) * nfbServer.getSamplesPerSecond() / 4);
        }
    }

    @Override
    public void stop() {
        // stop the visualizer
    }
}
