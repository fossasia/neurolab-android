package io.neurolab.main.task;

import io.neurolab.main.LongtermGraph;
import io.neurolab.model.DefaultFFTData;
import io.neurolab.settings.FeedbackSettings;
import io.neurolab.interfaces.Task;

public class LongtermFeedbackTrackerTask implements Task{

    private DefaultFFTData defaultFFData;
    private FeedbackSettings feedbackSettings;
    private LongtermGraph longtermGraph;

    public LongtermFeedbackTrackerTask(DefaultFFTData defaultFFTData, FeedbackSettings feedbackSettings, LongtermGraph longtermGraph) {
        this.defaultFFData = defaultFFTData;
        this.feedbackSettings = feedbackSettings;
        this.longtermGraph = longtermGraph;
    }

    @Override
    public void init() {

    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }
}
