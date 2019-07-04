package io.neurolab.main;

import android.content.Context;

import io.neurolab.settings.FeedbackSettings;
import io.neurolab.interfaces.Task;

public class NFBGraph implements Task {

    private Context context;
    private FeedbackSettings feedbackSettings;
    private int pointer;
    private int bins;
    private int channels;
    private NFBServer nfbServer;
    private int s;
    private long ts = -1;
    private boolean slowMode = false;

    public NFBGraph(Context context, NFBServer nfbServer, FeedbackSettings feedbackSettings) {
        this.context = context;
        this.feedbackSettings = feedbackSettings;
        this.nfbServer = nfbServer;
        this.init();
    }

    @Override
    public void init() {
        bins = feedbackSettings.getBins();
        channels = feedbackSettings.getNumChannels();
        pointer = 0;
        s = 255 / (bins * channels);
    }

    @Override
    public void run() {
        if ((slowMode) && (System.currentTimeMillis() - ts < 75))
            return;

        if (nfbServer.getNumSamples() < 1)
            return;

        int numSamples = 1;
        ts = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        // TODO: Needs to be implemented for stopping the task.
    }

}
